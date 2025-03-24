package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.OrderPizzaResponse;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.mapper.OrderMapper;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.repository.OrderRepository;
import adesso.it.AwesomePizza.repository.PizzaRepository;
import adesso.it.AwesomePizza.service.serviceImpl.OrderServiceImpl;
import adesso.it.AwesomePizza.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private PizzaMapper pizzaMapper;

    @Mock
    private OrderMapper orderMapper;

    private OrderServiceImpl orderService;

    private final String code = "12AB";

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, pizzaRepository, ingredientRepository, pizzaMapper, orderMapper);
    }

    @Test
    void testGetQueuedOrdersByArrival_ShouldReturnOrders() {
        List<Pizza> pizzas = new ArrayList<>();
        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        pizzas.add(new Pizza("BASEMargherita", ingredientsE, 0));
        List<Order> queuedOrders = new ArrayList<>();
        Date now = new Date();
        queuedOrders.add(new Order(code, pizzas, 10, now, OrderStatus.QUEUED));

        List<OrderPizzaResponse> pizzaResponseList = new ArrayList<>();
        pizzaResponseList.add(new OrderPizzaResponse(pizzas.get(0), 0));

        List<OrderResponse> expectedResponses = new ArrayList<>();
        expectedResponses.add(new OrderResponse(code, pizzaResponseList, 10, OrderStatus.QUEUED, now));


        when(orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED)).thenReturn(queuedOrders);
        when(orderMapper.mapOrderListToOrderResponse(queuedOrders)).thenReturn(expectedResponses);

        List<OrderResponse> actualResponses = orderService.getQueuedOrdersByArrival();

        assertEquals(expectedResponses.size(), actualResponses.size());
        verify(orderRepository, times(1)).findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED);
        verify(orderMapper, times(1)).mapOrderListToOrderResponse(queuedOrders);
    }

    @Test
    void testGetQueuedOrdersByArrival_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED)).thenReturn(Collections.emptyList());
        when(orderMapper.mapOrderListToOrderResponse(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<OrderResponse> actualResponses = orderService.getQueuedOrdersByArrival();

        assertTrue(actualResponses.isEmpty());
        verify(orderRepository, times(1)).findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED);
        verify(orderMapper, times(1)).mapOrderListToOrderResponse(Collections.emptyList());
    }
}

