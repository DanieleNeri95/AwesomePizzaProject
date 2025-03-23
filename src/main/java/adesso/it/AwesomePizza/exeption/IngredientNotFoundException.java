package adesso.it.AwesomePizza.exeption;

public class IngredientNotFoundException extends RuntimeException {

    public IngredientNotFoundException(String message) {
        super(message);
    }
}
