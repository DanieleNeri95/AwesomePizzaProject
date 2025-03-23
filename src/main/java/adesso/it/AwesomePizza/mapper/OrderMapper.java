package adesso.it.AwesomePizza.mapper;

import adesso.it.AwesomePizza.DTO.OrderPizzaResponse;
import adesso.it.AwesomePizza.DTO.OrderResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.entity.Pizza;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse mapOrderToOrderResponse(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setCode(order.getCode());
        orderResponse.setTotalPrice(order.getTotalPrice());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setCreatedAt(order.getCreatedAt());

        // Mappo le pizze ordinate
        List<OrderPizzaResponse> orderedPizzas = new ArrayList<>();
        for (Pizza pizza : order.getOrderedPizzas()) {
            OrderPizzaResponse pizzaResponse = new OrderPizzaResponse();
            pizzaResponse.setName(pizza.getName());
            pizzaResponse.setPrice(pizza.getPrice());

            // Mappo gli ingredienti della pizza
            List<String> ingredients = new ArrayList<>();
            for (Ingredient ingredient : pizza.getIngredients()) {
                ingredients.add(ingredient.getName());
            }
            pizzaResponse.setIngredients(ingredients);
            pizzaResponse.setQuantity(pizza.getQuantity());
            orderedPizzas.add(pizzaResponse);
        }
        orderResponse.setOrderedPizzas(orderedPizzas);
        return orderResponse;
    }

    public List<OrderResponse> mapOrderListToOrderResponse(List<Order> orders) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Order order: orders) {
            orderResponses.add(mapOrderToOrderResponse(order));
        }
        return orderResponses;
    }

}
