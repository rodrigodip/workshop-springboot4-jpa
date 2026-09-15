package com.rodrigodip.workshop_springboot4_jpa.web;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

import com.rodrigodip.workshop_springboot4_jpa.entities.Category;
import com.rodrigodip.workshop_springboot4_jpa.entities.Order;
import com.rodrigodip.workshop_springboot4_jpa.entities.Product;
import com.rodrigodip.workshop_springboot4_jpa.entities.User;
import com.rodrigodip.workshop_springboot4_jpa.entities.enums.OrderStatus;
import com.rodrigodip.workshop_springboot4_jpa.services.CategoryService;
import com.rodrigodip.workshop_springboot4_jpa.services.OrderService;
import com.rodrigodip.workshop_springboot4_jpa.services.ProductService;
import com.rodrigodip.workshop_springboot4_jpa.services.UserService;

@Controller
@RequestMapping("/fragments/admin")
public class AdminWebController {

        private static final DateTimeFormatter MOMENT_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
                        .withZone(ZoneOffset.UTC);

        private final UserService userService;
        private final ProductService productService;
        private final CategoryService categoryService;
        private final OrderService orderService;

        public AdminWebController(UserService userService, ProductService productService,
                        CategoryService categoryService, OrderService orderService) {
                this.userService = userService;
                this.productService = productService;
                this.categoryService = categoryService;
                this.orderService = orderService;
        }

        @GetMapping("/dashboard")
        public String dashboard(Model model) {
                List<?> orders = orderService.FindAll();
                double revenue = orderService.FindAll().stream().mapToDouble(o -> o.getTotal()).sum();
                model.addAttribute("userCount", userService.FindAll().size());
                model.addAttribute("productCount", productService.FindAll().size());
                model.addAttribute("orderCount", orders.size());
                model.addAttribute("revenue", revenue);
                return "fragments/admin/dashboard :: panel";
        }

        // ---- Products ----
        @GetMapping("/products")
        public String products(Model model) {
                model.addAttribute("products", productService.FindAll());
                model.addAttribute("categories", sortedById(categoryService.FindAll()));
                return "fragments/admin/products :: panel";
        }

        @PostMapping("/products")
        public String createProduct(@RequestParam String name,
                        @RequestParam(required = false) String description,
                        @RequestParam Double price,
                        @RequestParam(required = false) String imgUrl,
                        @RequestParam(required = false) List<Long> categoryIds,
                        Model model) {
                Product p = new Product(null, name, description == null ? "" : description, price,
                                imgUrl == null ? "" : imgUrl);
                if (categoryIds != null) {
                        categoryIds.forEach(id -> p.getCategories().add(new Category(id, null)));
                }
                productService.insert(p);
                return products(model);
        }

        @GetMapping("/products/{id}/edit")
        public String editProduct(@PathVariable Long id, Model model) {
                model.addAttribute("product", productService.FindByID(id));
                model.addAttribute("allCategories", sortedById(categoryService.FindAll()));
                return "fragments/admin/product-edit :: edit";
        }

        @PutMapping("/products/{id}")
        public String updateProduct(@PathVariable Long id,
                        @RequestParam String name,
                        @RequestParam(required = false) String description,
                        @RequestParam Double price,
                        @RequestParam(required = false) List<Long> categoryIds,
                        Model model) {
                Product data = new Product(null, name, description == null ? "" : description, price, "");
                if (categoryIds != null) {
                        categoryIds.forEach(cid -> data.getCategories().add(new Category(cid, null)));
                }
                productService.update(id, data);
                return products(model);
        }

        @DeleteMapping("/products/{id}")
        public String deleteProduct(@PathVariable Long id, Model model) {
                productService.delete(id);
                return products(model);
        }

        // ---- Categories ----
        @GetMapping("/categories")
        public String categories(Model model) {
                model.addAttribute("categories", sortedById(categoryService.FindAll()));
                return "fragments/admin/categories :: panel";
        }

        @PostMapping("/categories")
        public String createCategory(@RequestParam String name, Model model) {
                categoryService.insert(new Category(null, name));
                return categories(model);
        }

        @GetMapping("/categories/{id}/edit")
        public String editCategory(@PathVariable Long id, Model model) {
                model.addAttribute("category", categoryService.FindByID(id));
                return "fragments/admin/category-edit :: edit";
        }

        @PutMapping("/categories/{id}")
        public String updateCategory(@PathVariable Long id,
                        @RequestParam String name, Model model) {
                categoryService.update(id, new Category(null, name));
                return categories(model);
        }

        @DeleteMapping("/categories/{id}")
        public String deleteCategory(@PathVariable Long id, Model model) {
                categoryService.delete(id);
                return categories(model);
        }

        @GetMapping("/orders")
        public String orders(@RequestParam(required = false) String status,
                        @RequestParam(required = false) String clientName,
                        Model model) {
                List<Order> orders = orderService.findFilteredByClientName(status, clientName);
                model.addAttribute("orders", orders);
                model.addAttribute("orderDates", orderDates(orders));
                model.addAttribute("statuses", OrderStatus.values());
                model.addAttribute("selectedStatus", (status == null || status.isBlank()) ? "ALL" : status);
                model.addAttribute("selectedClientName", clientName == null ? "" : clientName);
                return "fragments/admin/orders :: panel";
        }

        private List<Category> sortedById(List<Category> categories) {
                return categories.stream()
                                .sorted(Comparator.comparing(
                                                category -> category.getId(),
                                                Comparator.nullsLast(Comparator.naturalOrder())))
                                .toList();
        }

        private Map<Long, String> orderDates(List<Order> orders) {
                Map<Long, String> dates = new HashMap<>();
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy").withZone(ZoneOffset.UTC);
                for (Order o : orders) {
                        if (o.getMoment() != null) {
                                dates.put(o.getId(), fmt.format(o.getMoment()));
                        }
                }
                return dates;
        }

        @GetMapping("/orders/{id}")
        public String orderDetail(@PathVariable Long id, Model model) {
                Order order = orderService.FindByID(id);
                model.addAttribute("order", order);
                model.addAttribute("moment",
                                order.getMoment() == null ? "" : MOMENT_FMT.format(order.getMoment()));
                model.addAttribute("statuses", OrderStatus.values());
                return "fragments/admin/order-detail :: panel";
        }

        @PutMapping("/orders/{id}/status")
        public String updateOrderStatus(@PathVariable Long id,
                        @RequestParam String orderStatus, Model model) {
                orderService.updateStatus(id, orderStatus);
                return orderDetail(id, model);
        }

        @DeleteMapping("/orders/{id}")
        public String deleteOrder(@PathVariable Long id, Model model) {
                orderService.delete(id);
                return orders(null, null, model);
        }

        // ---- Users ----
        @GetMapping("/users")
        public String users(Model model) {
                model.addAttribute("users", userService.FindAll());
                return "fragments/admin/users :: panel";
        }

        @PostMapping("/users")
        public String createUser(@RequestParam String name,
                        @RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam String password,
                        Model model) {
                userService.insert(new User(null, name, email, phone, password));
                return users(model);
        }

        @GetMapping("/users/{id}/edit")
        public String editUser(@PathVariable Long id, Model model) {
                model.addAttribute("client", userService.FindByID(id));
                return "fragments/admin/user-edit :: edit";
        }

        @PutMapping("/users/{id}")
        public String updateUser(@PathVariable Long id,
                        @RequestParam String name,
                        @RequestParam String email,
                        @RequestParam String phone,
                        @RequestParam(required = false) String password,
                        Model model) {
                String pw = (password == null || password.isBlank()) ? null : password;
                userService.update(id, new User(null, name, email, phone, pw));
                return users(model);
        }

        @DeleteMapping("/users/{id}")
        public String deleteUser(@PathVariable Long id, Model model) {
                userService.delete(id);
                return users(model);
        }
}
