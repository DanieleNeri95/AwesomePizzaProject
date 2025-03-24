package adesso.it.AwesomePizza.exeption;

public class PizzaNameUsedException extends RuntimeException {
    public PizzaNameUsedException(String message) {
        super(message);
    }
}
