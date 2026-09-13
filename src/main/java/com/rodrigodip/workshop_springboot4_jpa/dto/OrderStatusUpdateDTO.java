package com.rodrigodip.workshop_springboot4_jpa.dto;

public class OrderStatusUpdateDTO {

        private String orderStatus;

        public OrderStatusUpdateDTO() {
        }

        public String getOrderStatus() {
                return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
                this.orderStatus = orderStatus;
        }
}
