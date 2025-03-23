package adesso.it.AwesomePizza.DTO;

import adesso.it.AwesomePizza.entity.Pizza;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderPizzaResponse {

    private String name;
    private List<String> ingredients;
    private int quantity = 1;
    private Double price;

    public OrderPizzaResponse(Pizza pizza, int quantity) {
        this.name = pizza.getName();
        this.ingredients = pizza.getIngredients().stream().map(t -> t.getName()).toList();
        this.quantity = quantity;
        this.price = pizza.getPrice();
    }
}
