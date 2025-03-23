package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.OrderRequest;
import adesso.it.AwesomePizza.DTO.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse getOrderByCode(String code);

    List<OrderResponse> getQueuedOrdersByArrival();

    void assign(String code, String pizzaMakerName);

    void completeOrder(String code);
}
