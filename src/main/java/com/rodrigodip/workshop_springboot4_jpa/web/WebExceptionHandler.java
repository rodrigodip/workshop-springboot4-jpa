package com.rodrigodip.workshop_springboot4_jpa.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.DataBaseException;
import com.rodrigodip.workshop_springboot4_jpa.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice(basePackages = "com.rodrigodip.workshop_springboot4_jpa.web")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler {

        private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public String notFound(ResourceNotFoundException e, HttpServletRequest request,
                        HttpServletResponse response, Model model) {
                model.addAttribute("fragmentError", e.getMessage() + " (" + request.getRequestURI() + ")");
                String uri = request.getRequestURI();
                if (uri.contains("/admin/")) {
                        model.addAttribute("orders", List.of());
                        response.setHeader("HX-Retarget", "#admin-modal");
                        return "fragments/_error :: box";
                }
                return "fragments/_error :: box";
        }

        @ExceptionHandler(DataBaseException.class)
        public String badRequest(DataBaseException e, HttpServletRequest request,
                        HttpServletResponse response, Model model) {
                String raw = e.getMessage() == null ? "" : e.getMessage();
                if (looksLikeConstraintViolation(raw)) {
                        log.warn("FK violation on {}: {}", request.getRequestURI(), raw);
                        model.addAttribute("fragmentError", friendlyDeleteMessage(request.getRequestURI()));
                } else {
                        model.addAttribute("fragmentError", raw);
                }
                if (request.getRequestURI().contains("/admin/")) {
                        response.setHeader("HX-Retarget", "#admin-modal");
                }
                return "fragments/_error :: box";
        }

        private boolean looksLikeConstraintViolation(String message) {
                String lower = message.toLowerCase();
                return lower.contains("constraint") || lower.contains("violation")
                                || lower.contains("foreign key") || lower.contains("fk_")
                                || lower.contains("sqlstate") || lower.contains("referential integrity");
        }

        private String friendlyDeleteMessage(String uri) {
                if (uri.contains("/admin/users/")) {
                        return "This client has orders and cannot be deleted.";
                }
                if (uri.contains("/admin/products/")) {
                        return "This product is in one or more orders and cannot be deleted.";
                }
                if (uri.contains("/admin/categories/")) {
                        return "This category still has products. Remove them from the category first.";
                }
                if (uri.contains("/admin/orders/")) {
                        return "This order has a linked payment and cannot be deleted.";
                }
                return "This record is linked to other data and cannot be deleted.";
        }
}
