package adesso.it.AwesomePizza.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderRequest {

    @Size(min = 1, message = "L'ordine deve contenere almeno una pizza.")
    @Valid
    private List<OrderPizzaRequest> orderedPizzas;
}