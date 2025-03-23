package adesso.it.AwesomePizza.service.serviceImpl;

import adesso.it.AwesomePizza.DTO.*;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.exeption.OrderNotAssignedException;
import adesso.it.AwesomePizza.exeption.OrderNotFoundException;
import adesso.it.AwesomePizza.exeption.PizzaNotFoundException;
import adesso.it.AwesomePizza.mapper.OrderMapper;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.repository.OrderRepository;
import adesso.it.AwesomePizza.repository.PizzaRepository;
import adesso.it.AwesomePizza.service.OrderService;
import adesso.it.AwesomePizza.utils.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    public OrderServiceImpl(PizzaMapper pizzaMapper, OrderMapper orderMapper) {
        this.pizzaMapper = pizzaMapper;
        this.orderMapper = orderMapper;
    }

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PizzaRepository pizzaRepository;

    @Autowired
    IngredientRepository ingredientRepository;

    private final PizzaMapper pizzaMapper;
    private final OrderMapper orderMapper;

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 4;
    private static final Random RANDOM = new Random();
    private static final String BASE = "BASE";

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        // mi genero un codice univoco, controllando che non esista già a db un ordine con lo stesso codice
        String code = generateUniqueOrderCode();
        List<Pizza> pizzaEntities = new ArrayList<>();
        List<OrderPizzaResponse> pizzaResponseList = new ArrayList<>();
        double totaleOrdine = 0;

        // ciclo le pizze ordinate per elaborarle
        for (OrderPizzaRequest pizzaDTO : orderRequest.getOrderedPizzas()) {

            // verifico che esista la versiona base della pizza ordinata
            Pizza pizza = pizzaRepository.findByName(BASE + pizzaDTO.getName())
                    .orElseThrow(() -> new PizzaNotFoundException("Pizza " + pizzaDTO.getName() + " non trovata nel database."));

            var newPizza = new Pizza(pizzaDTO.getName(), updateIngredients(pizzaDTO, pizza.getIngredients()), pizzaDTO.getQuantity());

            // calcolo il prezzo custom in base alle aggiunte
            // 0,50 per ogni ingrediente aggiunto, e moltiplico il totale per il numero di pizze
            // altrimenti lascio quello della pizza base
            if(!CollectionUtils.isEmpty(pizzaDTO.getAddedIngredients()))
                newPizza.setPrice((pizza.getPrice() + (pizzaDTO.getAddedIngredients().size() * 0.5)));
            else
                newPizza.setPrice(pizza.getPrice());
            totaleOrdine = totaleOrdine + (newPizza.getPrice() * pizzaDTO.getQuantity());

            // inserisco la pizza ordinata a db
            newPizza = pizzaRepository.save(newPizza);

            pizzaResponseList.add(new OrderPizzaResponse(newPizza, pizzaDTO.getQuantity()));
            pizzaEntities.add(newPizza);
        }
        var order = orderRepository.save(new Order(code, pizzaEntities, totaleOrdine, new Date(), OrderStatus.QUEUED));

        return new OrderResponse(code, pizzaResponseList, order.getTotalPrice(), order.getStatus(), order.getCreatedAt());
    }

    @Override
    public OrderResponse getOrderByCode(String code) {
        var order = orderRepository.findByCode(code);

        if(!order.isPresent())
            throw new OrderNotFoundException("L'ordine con codice: "+code+" non trovato.");


        return orderMapper.mapOrderToOrderResponse(order.get());
    }

    @Override
    public List<OrderResponse> getQueuedOrdersByArrival() {
        return orderMapper.mapOrderListToOrderResponse(orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED));
    }

    @Override// cambiare nome in presaInCarico
    public void assign(String code, String pizzaMakerName) {

        var order = orderRepository.findByCodeAndStatus(code, OrderStatus.QUEUED).orElseThrow(() -> new IngredientNotFoundException("Ordine con codice "+code+" già assegnato o non trovato."));

        if(orderRepository.existsByTakedByAndStatusNot(pizzaMakerName, OrderStatus.DELIVERED))
            throw new OrderNotAssignedException("Il pizzaiolo "+pizzaMakerName+" ha già in carico un ordine, terminare l'ordine precedente prima di prenderne in carico un'altro.");

        order.setStatus(OrderStatus.PICKED_UP);
        order.setTakedBy(pizzaMakerName);
        order.setUpdatedAt(new Date());

        orderRepository.save(order);
    }

    @Override
    public void completeOrder(String code) {
        var order = orderRepository.findByCodeAndStatus(code, OrderStatus.PICKED_UP).orElseThrow(() -> new IngredientNotFoundException("Ordine con codice "+code+" non trovato."));
        order.setStatus(OrderStatus.DELIVERED);
        order.setUpdatedAt(new Date());
        orderRepository.save(order);
    }

    // preparo la lista degli ingredienti della pizza
    private List<Ingredient> updateIngredients (OrderPizzaRequest pizzaDTO, List<Ingredient> ingredientList){
        // inserisco gli ingredienti della pizza base alla lista
        List<Ingredient> updatedIngredients = new ArrayList<>(ingredientList);
        // per ognuno verifico la sua esistenza a db,
        // se non esistesse rispondo con messaggio di errore appropriato e lo catturo dal GlobalExceptionHandler
        if (!CollectionUtils.isEmpty(pizzaDTO.getAddedIngredients())) {
            for (String addedIngredient : pizzaDTO.getAddedIngredients()) {
                Ingredient ingredient = ingredientRepository.findByName(addedIngredient)
                        .orElseThrow(() -> new IngredientNotFoundException("Ingrediente " + addedIngredient + " non disponibile."));
                updatedIngredients.add(ingredient);
            }
        }
        // rimuovo dagli ingredienti, gli ingredienti rimossi dalla pizza ordinata
        if(!CollectionUtils.isEmpty(pizzaDTO.getRemovedIngredients()))
            updatedIngredients.removeIf(ingredient -> pizzaDTO.getRemovedIngredients().contains(ingredient.getName()));

        return updatedIngredients;
    }

    // verifico la sua univocità a db
    private String generateUniqueOrderCode() {
        String code;
        do {
            code = generateCode();
        } while (orderRepository.existsByCode(code));
        return code;
    }

    // genero un codice alfanumerico di 4 caratteri
    private String generateCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}