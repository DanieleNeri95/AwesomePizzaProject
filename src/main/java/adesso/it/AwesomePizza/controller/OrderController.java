package adesso.it.AwesomePizza.controller;

import adesso.it.AwesomePizza.DTO.OrderRequest;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<OrderResponse> orderResponse = orderService.getQueuedOrdersByArrival();
        return ResponseEntity.ok(orderResponse);
    }

    @PutMapping("/{code}/{pizzaMakerName}")
    public ResponseEntity<OrderResponse> assign(@PathVariable String code, @PathVariable String pizzaMakerName) {
        orderService.assign(code, pizzaMakerName);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<OrderResponse> getOrderByCode(@PathVariable String code) {
        OrderResponse orderResponse = orderService.getOrderByCode(code);
        return orderResponse != null ? ResponseEntity.ok(orderResponse) : ResponseEntity.notFound().build();
    }

}
