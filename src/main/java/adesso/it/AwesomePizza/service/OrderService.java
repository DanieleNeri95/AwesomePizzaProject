package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.OrderRequest;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    Order getOrder(String cod);

    OrderResponse getOrderByCode(String code);

    OrderResponse getOrderById(UUID id);

    List<OrderResponse> getQueuedOrdersByArrival();

    void assign(String code, String pizzaMakerName);
}
