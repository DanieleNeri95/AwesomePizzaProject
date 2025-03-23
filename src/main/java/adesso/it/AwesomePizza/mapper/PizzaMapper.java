package adesso.it.AwesomePizza.mapper;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.repository.IngredientRepository;
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

        List<IngredientResponse> ingredients = pizzaDTO.getIngredients();

        pizza.setIngredients(ingredientRepository.findByNameIn(ingredients.stream().map(i -> i.getName()).toList()));
        return pizza;
    }

    public PizzaDTO mapToDTO(Pizza pizza) {
        PizzaDTO pizzaDTO = new PizzaDTO();
        pizzaDTO.setName(pizza.getName());
        pizzaDTO.setPrice(pizza.getPrice());

        pizzaDTO.setIngredients(pizza.getIngredients().stream().map(i -> new IngredientResponse(i.getName())).toList());
        return pizzaDTO;
    }

    public List<PizzaDTO> toDTOList (List<Pizza> pizzas){
        List<PizzaDTO> pizzaDTOS = new ArrayList<>();
        for (Pizza pizza : pizzas) {
            var dto = mapToDTO(pizza);
            pizzaDTOS.add(dto);
        }
        return pizzaDTOS;
    }
}
