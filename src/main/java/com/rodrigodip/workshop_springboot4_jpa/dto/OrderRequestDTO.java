package com.rodrigodip.workshop_springboot4_jpa.dto;

import java.util.ArrayList;
import java.util.List;

public class OrderRequestDTO {

        private Long clientId;
        private String orderStatus;
        private List<OrderItemRequestDTO> items = new ArrayList<>();

        public OrderRequestDTO() {
        }

        public Long getClientId() {
                return clientId;
        }

        public void setClientId(Long clientId) {
                this.clientId = clientId;
        }

        public String getOrderStatus() {
                return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
                this.orderStatus = orderStatus;
        }

        public List<OrderItemRequestDTO> getItems() {
                return items;
        }

        public void setItems(List<OrderItemRequestDTO> items) {
                this.items = items;
        }
}
