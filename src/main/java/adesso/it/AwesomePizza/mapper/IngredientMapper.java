package adesso.it.AwesomePizza.mapper;

import adesso.it.AwesomePizza.DTO.IngredientDTO;
import adesso.it.AwesomePizza.entity.Ingredient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientMapper {

    public List<IngredientDTO> entotyToListDTO (List<Ingredient> ingredientList){
        List<IngredientDTO> dtoList = new ArrayList<>();
        for (Ingredient ingredient : ingredientList) {
            dtoList.add(new IngredientDTO(ingredient.getName()));
        }
        return dtoList;
    }
}
