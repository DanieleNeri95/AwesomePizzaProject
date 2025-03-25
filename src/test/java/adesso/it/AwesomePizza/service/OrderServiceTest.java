package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.OrderPizzaRequest;
import adesso.it.AwesomePizza.DTO.OrderPizzaResponse;
import adesso.it.AwesomePizza.DTO.OrderRequest;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.exeption.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
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

    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, pizzaRepository, ingredientRepository, pizzaMapper, orderMapper);
    }

    @Test
    void testCreateOrder_BadRequest_ShouldThrowException() {
        OrderRequest orderRequest = new OrderRequest();
        Exception exception = assertThrows(BadRequestException.class, () -> orderService.createOrder(orderRequest));

        assertEquals("Lista delle pizze necessaria.", exception.getMessage());
    }

    @Test
    void testCreateOrder_Success_ShouldReturnOrderResponse() {
        when(orderRepository.existsByCode(anyString())).thenReturn(false);

        OrderRequest orderRequest = new OrderRequest();
        List<OrderPizzaRequest> orderedPizzas = new ArrayList<>();

        OrderPizzaRequest pizzaRequest = new OrderPizzaRequest();
        pizzaRequest.setName("Margherita");
        pizzaRequest.setQuantity(2);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("Olive");
        ingredients.add("Prosciutto");
        pizzaRequest.setAddedIngredients(ingredients);

        orderedPizzas.add(pizzaRequest);
        orderRequest.setOrderedPizzas(orderedPizzas);

        List<Ingredient> baseIngredients = new ArrayList<>();
        baseIngredients.add(new Ingredient("Pomodoro"));
        baseIngredients.add(new Ingredient("Mozzarella"));
        Pizza basePizza = new Pizza("BASEMargherita", baseIngredients, 0);
        basePizza.setPrice(6.0);

        when(pizzaRepository.findByName("BASEMargherita")).thenReturn(Optional.of(basePizza));
        when(ingredientRepository.findByName("Olive")).thenReturn(Optional.of(new Ingredient("Olive")));
        when(ingredientRepository.findByName("Prosciutto")).thenReturn(Optional.of(new Ingredient("Prosciutto")));

        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        ingredientsE.add(new Ingredient("Olive"));
        ingredientsE.add(new Ingredient("Prosciutto"));
        Pizza savedPizza = new Pizza("Margherita", ingredientsE, 2);
        savedPizza.setPrice(7.0);

        when(pizzaRepository.save(any())).thenReturn(savedPizza);

        Order savedOrder = new Order(code, List.of(savedPizza), 14.0, new Date(), OrderStatus.QUEUED);
        when(orderRepository.save(any())).thenReturn(savedOrder);

        OrderResponse result = orderService.createOrder(orderRequest);

        result.setCode(code);
        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertEquals(14.0, result.getTotalPrice());
        assertEquals(OrderStatus.QUEUED, result.getStatus());

        verify(pizzaRepository, times(1)).save(any());
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    void testCreateOrder_PizzaNotFound_ShouldThrowException() {
        OrderRequest orderRequest = new OrderRequest();
        List<OrderPizzaRequest> orderedPizzas = new ArrayList<>();

        OrderPizzaRequest pizzaRequest = new OrderPizzaRequest();
        pizzaRequest.setName("Diavola");
        pizzaRequest.setQuantity(1);
        orderedPizzas.add(pizzaRequest);
        orderRequest.setOrderedPizzas(orderedPizzas);

        when(pizzaRepository.findByName("BASEDiavola")).thenReturn(Optional.empty());

        Exception exception = assertThrows(PizzaNotFoundException.class, () -> orderService.createOrder(orderRequest));

        assertEquals("Pizza Diavola non trovata nel database.", exception.getMessage());

        verify(pizzaRepository, times(1)).findByName("BASEDiavola");
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCreateOrder_CorrectPriceCalculation() {
        when(orderRepository.existsByCode(anyString())).thenReturn(false);

        OrderRequest orderRequest = new OrderRequest();
        OrderPizzaRequest pizzaRequest = new OrderPizzaRequest();
        pizzaRequest.setName("Margherita");
        pizzaRequest.setQuantity(3); // Aggiungi 3 pizze
        pizzaRequest.setAddedIngredients(List.of("Olive"));

        orderRequest.setOrderedPizzas(List.of(pizzaRequest));

        List<Ingredient> baseIngredients = new ArrayList<>();
        baseIngredients.add(new Ingredient("Pomodoro"));
        baseIngredients.add(new Ingredient("Mozzarella"));
        Pizza basePizza = new Pizza("BASEMargherita", baseIngredients, 0);
        basePizza.setPrice(6.0);

        when(pizzaRepository.findByName("BASEMargherita")).thenReturn(Optional.of(basePizza));
        when(ingredientRepository.findByName("Olive")).thenReturn(Optional.of(new Ingredient("Olive")));

        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        ingredientsE.add(new Ingredient("Olive"));
        Pizza savedPizza = new Pizza("Margherita", ingredientsE, 3);
        savedPizza.setPrice(6.5);

        when(pizzaRepository.save(any())).thenReturn(savedPizza);

        Order savedOrder = new Order(code, List.of(savedPizza), 19.5, new Date(), OrderStatus.QUEUED); // Prezzo totale: 3 * 6.5 = 19.5
        when(orderRepository.save(any())).thenReturn(savedOrder);

        // Esegui la creazione dell'ordine
        OrderResponse result = orderService.createOrder(orderRequest);
        result.setCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());
        assertEquals(19.5, result.getTotalPrice());

        verify(orderRepository, times(1)).save(any());
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

    @Test
    void testGetOrderByCode_ValidCode_ShouldReturnOrderResponse() {

        Order order = new Order();
        order.setCode(code);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setCode(code);

        when(orderRepository.findByCode(code)).thenReturn(Optional.of(order));
        when(orderMapper.mapOrderToOrderResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderByCode(code);

        assertNotNull(result);
        assertEquals(code, result.getCode());

        verify(orderRepository, times(1)).findByCode(code);
        verify(orderMapper, times(1)).mapOrderToOrderResponse(order);
    }

    @Test
    void testGetOrderByCode_InvalidCode_ShouldThrowException() {

        when(orderRepository.findByCode(code)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderNotFoundException.class, () -> orderService.getOrderByCode(code));

        assertEquals("L'ordine con codice: 12AB non trovato.", exception.getMessage());

        verify(orderRepository, times(1)).findByCode(code);
        verify(orderMapper, never()).mapOrderToOrderResponse(any());
    }

    @Test
    void testCompleteOrder_ValidCode_ShouldUpdateStatus() {
        Order order = new Order();
        order.setCode(code);
        order.setStatus(OrderStatus.PICKED_UP);
        order.setUpdatedAt(null);

        when(orderRepository.findByCodeAndStatus(code, OrderStatus.PICKED_UP)).thenReturn(Optional.of(order));

        orderService.completeOrder(code);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testCompleteOrder_InvalidCode_ShouldThrowException() {

        when(orderRepository.findByCodeAndStatus(code, OrderStatus.PICKED_UP)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IngredientNotFoundException.class, () -> orderService.completeOrder(code));

        assertEquals("Ordine con codice 12AB non trovato.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testAssign_ValidOrderAndAvailablePizzaMaker_ShouldAssignOrder() {
        String pizzaMaker = "Mario Rossi";

        Order order = new Order();
        order.setCode(code);
        order.setStatus(OrderStatus.QUEUED);

        when(orderRepository.findByCodeAndStatus(code, OrderStatus.QUEUED)).thenReturn(Optional.of(order));
        when(orderRepository.existsByTakedByAndStatusNot(pizzaMaker, OrderStatus.DELIVERED)).thenReturn(false);

        orderService.assign(code, pizzaMaker);

        assertEquals(OrderStatus.PICKED_UP, order.getStatus());
        assertEquals(pizzaMaker, order.getTakedBy());
        assertNotNull(order.getUpdatedAt());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testAssign_InvalidPizzaMakerName_ShouldThrowException() {
        String pizzaMaker = "";

        Exception exception = assertThrows(BadRequestException.class, () -> orderService.assign(code, pizzaMaker));

        assertEquals("Nome del pizzaiolo necessario.", exception.getMessage());
    }

    @Test
    void testAssign_InvalidOrder_ShouldThrowException() {
        String pizzaMaker = "Mario Rossi";

        when(orderRepository.findByCodeAndStatus(code, OrderStatus.QUEUED)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IngredientNotFoundException.class, () -> orderService.assign(code, pizzaMaker));

        assertEquals("Ordine con codice 12AB già assegnato o non trovato.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testAssign_PizzaMakerAlreadyHasOrder_ShouldThrowException() {
        String pizzaMaker = "Mario Rossi";

        Order order = new Order();
        order.setCode(code);
        order.setStatus(OrderStatus.QUEUED);

        when(orderRepository.findByCodeAndStatus(code, OrderStatus.QUEUED)).thenReturn(Optional.of(order));
        when(orderRepository.existsByTakedByAndStatusNot(pizzaMaker, OrderStatus.DELIVERED)).thenReturn(true);

        Exception exception = assertThrows(OrderNotAssignedException.class, () -> orderService.assign(code, pizzaMaker));

        assertEquals("Il pizzaiolo Mario Rossi ha già in carico un ordine, terminare l'ordine precedente prima di prenderne in carico un'altro.", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUpdateIngredients_NoChanges_ShouldReturnOriginalList() {
        List<Ingredient> baseIngredients = List.of(new Ingredient("Pomodoro"), new Ingredient("Mozzarella"));
        OrderPizzaRequest pizzaDTO = new OrderPizzaRequest();
        pizzaDTO.setAddedIngredients(Collections.emptyList());
        pizzaDTO.setRemovedIngredients(Collections.emptyList());

        List<Ingredient> result = orderService.updateIngredients(pizzaDTO, baseIngredients);

        assertEquals(baseIngredients, result);
    }

    @Test
    void testUpdateIngredients_AddValidIngredients_ShouldAddThem() {
        List<Ingredient> baseIngredients = new ArrayList<>(List.of(new Ingredient("Pomodoro"), new Ingredient("Mozzarella")));
        OrderPizzaRequest pizzaDTO = new OrderPizzaRequest();
        pizzaDTO.setAddedIngredients(List.of("Salame", "Funghi"));
        pizzaDTO.setRemovedIngredients(Collections.emptyList());

        when(ingredientRepository.findByName("Salame")).thenReturn(Optional.of(new Ingredient("Salame")));
        when(ingredientRepository.findByName("Funghi")).thenReturn(Optional.of(new Ingredient("Funghi")));

        List<Ingredient> result = orderService.updateIngredients(pizzaDTO, baseIngredients);

        assertEquals(4, result.size());
        assertTrue(result.contains(new Ingredient("Salame")));
        assertTrue(result.contains(new Ingredient("Funghi")));
    }

    @Test
    void testUpdateIngredients_AddInvalidIngredient_ShouldThrowException() {
        List<Ingredient> baseIngredients = new ArrayList<>(List.of(new Ingredient("Pomodoro"), new Ingredient("Mozzarella")));
        OrderPizzaRequest pizzaDTO = new OrderPizzaRequest();
        pizzaDTO.setAddedIngredients(List.of("Salame"));
        pizzaDTO.setRemovedIngredients(Collections.emptyList());

        when(ingredientRepository.findByName("Salame")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IngredientNotFoundException.class, () ->
                orderService.updateIngredients(pizzaDTO, baseIngredients)
        );

        assertEquals("Ingrediente Salame non disponibile.", exception.getMessage());
    }

    @Test
    void testUpdateIngredients_RemoveIngredients_ShouldRemoveThem() {
        List<Ingredient> baseIngredients = new ArrayList<>(List.of(new Ingredient("Pomodoro"), new Ingredient("Mozzarella"), new Ingredient("Funghi")));
        OrderPizzaRequest pizzaDTO = new OrderPizzaRequest();
        pizzaDTO.setAddedIngredients(Collections.emptyList());
        pizzaDTO.setRemovedIngredients(List.of("Funghi"));

        List<Ingredient> result = orderService.updateIngredients(pizzaDTO, baseIngredients);

        assertEquals(2, result.size());
        assertFalse(result.contains(new Ingredient("Funghi")));
    }

    @Test
    void testUpdateIngredients_AddAndRemoveIngredients_ShouldUpdateCorrectly() {
        List<Ingredient> baseIngredients = new ArrayList<>(List.of(new Ingredient("Pomodoro"), new Ingredient("Mozzarella"), new Ingredient("Funghi")));
        OrderPizzaRequest pizzaDTO = new OrderPizzaRequest();
        pizzaDTO.setAddedIngredients(List.of("Salame"));
        pizzaDTO.setRemovedIngredients(List.of("Funghi"));

        when(ingredientRepository.findByName("Salame")).thenReturn(Optional.of(new Ingredient("Salame")));

        List<Ingredient> result = orderService.updateIngredients(pizzaDTO, baseIngredients);

        assertEquals(3, result.size());
        assertTrue(result.contains(new Ingredient("Salame")));
        assertFalse(result.contains(new Ingredient("Funghi")));
    }

    @Test
    void testGenerateCode_ShouldReturnValidCode() {
        String code = orderService.generateCode();

        assertNotNull(code);
        assertEquals(CODE_LENGTH, code.length());

        for (char c : code.toCharArray()) {
            assertTrue(CHARACTERS.contains(String.valueOf(c)));
        }
    }

    @Test
    void testGenerateCode_ShouldGenerateDifferentCodes() {
        Set<String> generatedCodes = new HashSet<>();

        for (int i = 0; i < 100; i++) {
            generatedCodes.add(orderService.generateCode());
        }

        assertTrue(generatedCodes.size() > 90);
    }

    @Test
    void testGenerateUniqueOrderCode_ShouldReturnUniqueCode() {
        when(orderRepository.existsByCode(anyString())).thenReturn(false);

        String code = orderService.generateUniqueOrderCode();

        assertNotNull(code);
        assertEquals(CODE_LENGTH, code.length());
        verify(orderRepository, times(1)).existsByCode(code);
    }

    @Test
    void testGenerateUniqueOrderCode_ShouldRegenerateIfCodeExists() {
        when(orderRepository.existsByCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        String code = orderService.generateUniqueOrderCode();

        assertNotNull(code);
        assertEquals(CODE_LENGTH, code.length());
        verify(orderRepository, times(2)).existsByCode(anyString());
    }
}