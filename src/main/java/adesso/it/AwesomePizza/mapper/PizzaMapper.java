package adesso.it.AwesomePizza.mapper;

import adesso.it.AwesomePizza.DTO.IngredientDTO;
import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PizzaMapper {

    @Autowired
    IngredientRepository ingredientRepository;

    public Pizza mapToEntity(PizzaDTO pizzaDTO) {

        Pizza pizza = new Pizza();
        pizza.setName(pizzaDTO.getName());
        pizza.setPrice(pizzaDTO.getPrice());

        List<IngredientDTO> ingredients = pizzaDTO.getIngredients();

        if (pizzaDTO.getRemovedIngredients() != null) {
            ingredients.removeAll(pizzaDTO.getRemovedIngredients());
        }
        if (pizzaDTO.getAddedIngredients() != null) {
            ingredients.addAll(pizzaDTO.getAddedIngredients());
        }
        pizza.setIngredients(ingredientRepository.findByNameIn(ingredients.stream().map(i -> i.getName()).toList()));
        return pizza;
    }

    public List<Pizza> mapPizzaToEntityList(List<PizzaDTO> pizzeDTO) {

        List<Pizza> pizze = new ArrayList<>();
        var pizza = new Pizza();
        for (PizzaDTO pizzaDto: pizzeDTO) {
            pizza = mapToEntity(pizzaDto);
            pizze.add(pizza);
        }
        return pizze;
    }
}
