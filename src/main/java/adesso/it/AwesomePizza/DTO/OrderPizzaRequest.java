package adesso.it.AwesomePizza.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderPizzaRequest {

    @NotBlank(message = "Il nome della pizza è obbligatorio.")
    private String name;
    private List<String> removedIngredients;
    private List<String> addedIngredients;
    private int quantity = 1;
}
