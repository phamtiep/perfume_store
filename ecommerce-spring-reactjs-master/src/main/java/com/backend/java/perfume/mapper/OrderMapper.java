package com.backend.java.perfume.mapper;

import com.backend.java.perfume.domain.Order;
import com.backend.java.perfume.dto.HeaderResponse;
import com.backend.java.perfume.dto.order.OrderItemResponse;
import com.backend.java.perfume.dto.order.OrderRequest;
import com.backend.java.perfume.dto.order.OrderResponse;
import com.backend.java.perfume.exception.InputFieldException;
import com.backend.java.perfume.service.OrderService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final CommonMapper commonMapper;
    private final OrderService orderService;
    
    public OrderResponse getOrderById(Long orderId) {
        return commonMapper.convertToResponse(orderService.getOrderById(orderId), OrderResponse.class);
    }
    
    public List<OrderItemResponse> getOrderItemsByOrderId(Long orderId) {
        return commonMapper.convertToResponseList(orderService.getOrderItemsByOrderId(orderId), OrderItemResponse.class);
    }

    public HeaderResponse<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> orders = orderService.getAllOrders(pageable);
        return commonMapper.getHeaderResponse(orders.getContent(), orders.getTotalPages(), orders.getTotalElements(), OrderResponse.class);
    }

    public HeaderResponse<OrderResponse> getUserOrders(String email, Pageable pageable) {
        Page<Order> orders = orderService.getUserOrders(email, pageable);
        return commonMapper.getHeaderResponse(orders.getContent(), orders.getTotalPages(), orders.getTotalElements(), OrderResponse.class);
    }

    public String deleteOrder(Long orderId) {
        return orderService.deleteOrder(orderId);
    }

    public OrderResponse postOrder(OrderRequest orderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
        Order order = orderService.postOrder(commonMapper.convertToEntity(orderRequest, Order.class), orderRequest.getPerfumesId());
        return commonMapper.convertToResponse(order, OrderResponse.class);
    }
}
