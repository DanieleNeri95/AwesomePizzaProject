package adesso.it.AwesomePizza.DTO;

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
public class PizzaDTO {

    @NotBlank(message = "Il nome della pizza è obbligatorio.")
    private String name;
    @Size(min = 1, message = "La pizza deve contenere almeno un ingrediente.")
    @Valid
    private List<IngredientResponse> ingredients;
    private List<IngredientResponse> removedIngredients;
    private List<IngredientResponse> addedIngredients;
    private int quantity = 1;
    @NotNull(message = "Il prezzo della pizza è obbligatorio.")
    @Positive(message = "Il prezzo della pizza deve essere maggiore di zero.")
    private Double price;


}