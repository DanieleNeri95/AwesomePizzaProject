package adesso.it.AwesomePizza.exeption;

public class OrderNotAssignedException extends RuntimeException{

    public OrderNotAssignedException(String message) {
        super(message);
    }
}
