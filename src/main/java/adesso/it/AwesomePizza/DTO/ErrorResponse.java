package adesso.it.AwesomePizza.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponse {

    private int status;
    private String message;
    private long timestamp;

}
