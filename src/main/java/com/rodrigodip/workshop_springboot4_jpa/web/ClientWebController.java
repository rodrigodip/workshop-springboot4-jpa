package com.rodrigodip.workshop_springboot4_jpa.web;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rodrigodip.workshop_springboot4_jpa.dto.OrderItemRequestDTO;
import com.rodrigodip.workshop_springboot4_jpa.dto.OrderRequestDTO;
import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.entities.enums.OrderStatus;
import com.rodrigodip.workshop_springboot4_jpa.services.CategoryService;
import com.rodrigodip.workshop_springboot4_jpa.services.OrderService;
import com.rodrigodip.workshop_springboot4_jpa.services.ProductService;
import com.rodrigodip.workshop_springboot4_jpa.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/fragments/client")
public class ClientWebController {

        static final String SESSION_CLIENT = "currentClientId";

        static final DateTimeFormatter MOMENT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
                        .withZone(ZoneOffset.UTC);

        private final UserService userService;
        private final ProductService productService;
        private final CategoryService categoryService;
        private final OrderService orderService;

        public ClientWebController(UserService userService, ProductService productService,
                        CategoryService categoryService, OrderService orderService) {
                this.userService = userService;
                this.productService = productService;
                this.categoryService = categoryService;
                this.orderService = orderService;
        }

        @GetMapping("/panel")
        public String panel(HttpSession session, Model model) {
                Long clientId = (Long) session.getAttribute(SESSION_CLIENT);
                if (clientId == null) {
                        return "fragments/client/gate :: gate";
                }
                populateStorefront(model, clientId, null);
                return "fragments/client/storefront :: storefront";
        }

        @PostMapping("/session-client")
        public String createSessionClient(@RequestParam String name,
                        @RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam String password,
                        HttpSession session, Model model) {
                String error = validate(name, email, phone, password);
                if (error != null) {
                        model.addAttribute("gateError", error);
                        return "fragments/client/gate :: gate";
                }
                User created = userService.insert(new User(null, name.trim(), email.trim(), phone.trim(), password));
                session.setAttribute(SESSION_CLIENT, created.getId());
                populateStorefront(model, created.getId(), null);
                return "fragments/client/storefront :: storefront";
        }

        @DeleteMapping("/session-client")
        public String resetSessionClient(HttpSession session) {
                session.removeAttribute(SESSION_CLIENT);
                return "fragments/client/gate :: gate";
        }

        @GetMapping("/products")
        public String productCards(@RequestParam(required = false) Long categoryId,
                        @RequestParam(defaultValue = "0") int page, Model model) {
                addCatalogPageAttributes(model, categoryId, page);
                return "fragments/client/product-cards :: cards";
        }

        @GetMapping("/products/{id}")
        public String productDetail(@PathVariable Long id, Model model) {
                model.addAttribute("product", productService.FindByID(id));
                return "fragments/client/product-detail :: detail";
        }

        @GetMapping("/orders")
        public String myOrders(HttpSession session, Model model) {
                Long clientId = (Long) session.getAttribute(SESSION_CLIENT);
                addOrderListAttributes(model, clientId == null ? List.of()
                                : orderService.findFiltered(null, clientId));
                return "fragments/client/my-orders :: list";
        }

        @GetMapping("/orders/{id}")
        public String orderDetail(@PathVariable Long id, HttpSession session, Model model) {
                Order order = orderService.FindByID(id);
                Long clientId = (Long) session.getAttribute(SESSION_CLIENT);
                if (clientId != null && order.getClient() != null
                                && !clientId.equals(order.getClient().getId())) {
                        addOrderListAttributes(model, orderService.findFiltered(null, clientId));
                        model.addAttribute("fragmentError", "Order #" + id + " belongs to another client.");
                        return "fragments/client/my-orders :: list";
                }
                model.addAttribute("order", order);
                model.addAttribute("moment",
                                order.getMoment() == null ? "" : MOMENT_FMT.format(order.getMoment()));
                return "fragments/client/order-detail :: detail";
        }

        @PostMapping("/orders")
        public String createOrder(@RequestParam(name = "productId") List<Long> productIds,
                        @RequestParam(name = "quantity") List<Integer> quantities,
                        HttpSession session, Model model) {
                Long clientId = (Long) session.getAttribute(SESSION_CLIENT);
                if (clientId == null) {
                        model.addAttribute("gateError", "Create your client first.");
                        return "fragments/client/gate :: gate";
                }
                OrderRequestDTO dto = new OrderRequestDTO();
                dto.setClientId(clientId);
                dto.setOrderStatus("WAITING_PAYMENT");
                for (int i = 0; i < productIds.size(); i++) {
                        OrderItemRequestDTO item = new OrderItemRequestDTO();
                        item.setProductId(productIds.get(i));
                        item.setQuantity(quantities.size() > i ? quantities.get(i) : 1);
                        dto.getItems().add(item);
                }
                Order created = orderService.create(dto);
                model.addAttribute("order", created);
                return "fragments/client/order-success :: success";
        }

        @PutMapping("/orders/{id}/cancel")
        public String cancelOrder(@PathVariable Long id, HttpSession session, Model model) {
                Long clientId = (Long) session.getAttribute(SESSION_CLIENT);
                if (clientId == null) {
                        model.addAttribute("gateError", "Create your client first.");
                        return "fragments/client/gate :: gate";
                }
                Order order = orderService.FindByID(id);
                if (order.getClient() == null || !clientId.equals(order.getClient().getId())) {
                        addOrderListAttributes(model, orderService.findFiltered(null, clientId));
                        model.addAttribute("fragmentError", "Order #" + id + " belongs to another client.");
                        return "fragments/client/my-orders :: list";
                }
                if (order.getOrderStatus() != OrderStatus.WAITING_PAYMENT
                                && order.getOrderStatus() != OrderStatus.PAID) {
                        addOrderListAttributes(model, orderService.findFiltered(null, clientId));
                        model.addAttribute("fragmentError",
                                        "Only waiting-payment or paid orders can be cancelled.");
                        return "fragments/client/my-orders :: list";
                }
                orderService.updateStatus(id, "CANCELED");
                addOrderListAttributes(model, orderService.findFiltered(null, clientId));
                return "fragments/client/my-orders :: list";
        }

        private void addOrderListAttributes(Model model, List<Order> orders) {
                Map<Long, String> moments = new HashMap<>();
                double total = 0.0;
                for (Order o : orders) {
                        if (o.getMoment() != null) {
                                moments.put(o.getId(), MOMENT_FMT.format(o.getMoment()));
                        }
                        if (o.getTotal() != null) {
                                total += o.getTotal();
                        }
                }
                model.addAttribute("orders", orders);
                model.addAttribute("moments", moments);
                model.addAttribute("myOrdersTotal", total);
        }

        private void addCatalogPageAttributes(Model model, Long categoryId, int page) {
                org.springframework.data.domain.Page<Product> productPage = productService
                                .findCatalogPage(categoryId, page);
                model.addAttribute("products", productPage.getContent());
                model.addAttribute("currentPage", productPage.getNumber());
                model.addAttribute("totalPages", productPage.getTotalPages());
                model.addAttribute("selectedCategoryId", categoryId);
        }

        private void populateStorefront(Model model, Long clientId, Long categoryId) {
                User client = userService.FindByID(clientId);
                List<Category> categories = categoryService.FindAll();
                model.addAttribute("client", client);
                model.addAttribute("categories", categories);
                addCatalogPageAttributes(model, categoryId, 0);
                List<Order> myOrders = orderService.findFiltered(null, clientId);
                model.addAttribute("myOrders", myOrders);
                addOrderListAttributes(model, myOrders);
        }

        private String validate(String name, String email, String phone, String password) {
                if (name == null || name.isBlank())
                        return "Name is required.";
                if (email == null || !email.contains("@"))
                        return "A valid email is required.";
                if (phone == null || !phone.trim().matches("^[0-9]{8,15}$"))
                        return "Phone must contain digits only (8–15).";
                if (password == null || password.length() < 4)
                        return "Password must be at least 4 characters.";
                return null;
        }
}
