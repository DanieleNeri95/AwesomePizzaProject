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
public class OrderResponse {

    private String code;
    private List<OrderPizzaResponse> orderedPizzas;

    private String pizzaMakerName;
    private Enum status;
    private Date createdAt;
    private Date updatedAt;
    private double totalPrice;

    public OrderResponse(String code, List<OrderPizzaResponse> orderedPizzas, double totalPrice, Enum status, Date createdAt) {
        this.code = code;
        this.orderedPizzas = orderedPizzas;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }
}