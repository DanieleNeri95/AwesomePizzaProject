package adesso.it.AwesomePizza.controller;

import adesso.it.AwesomePizza.DTO.OrderRequest;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.service.OrderService;
import adesso.it.AwesomePizza.utils.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    private ResponseEntity<OrderResponse> createOrder (@Valid @RequestBody OrderRequest orderRequest){
        return ResponseEntity.status(201).body(orderService.createOrder(orderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getQueuedOrdersByArrival() {
        List<OrderResponse> orders = orderService.getQueuedOrdersByArrival();
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/{code}/{pizzaMakerName}")
    public ResponseEntity<OrderResponse> assign(@PathVariable String code, @PathVariable String pizzaMakerName) {
        orderService.assign(code, pizzaMakerName);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<OrderResponse> getOrderByCode(@PathVariable String code) {
        OrderResponse orderResponse = orderService.getOrderByCode(code);
        return orderResponse != null ? ResponseEntity.ok(orderResponse) : ResponseEntity.notFound().build();
    }

}
