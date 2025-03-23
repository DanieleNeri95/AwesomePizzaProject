package adesso.it.AwesomePizza.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {

    @NotBlank(message = "Il nome dell'ingrediente non può essere vuoto.")
    private String name;
}

