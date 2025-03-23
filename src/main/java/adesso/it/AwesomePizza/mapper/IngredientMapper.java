package adesso.it.AwesomePizza.mapper;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientMapper {

    public List<IngredientResponse> entotyToListDTO (List<Ingredient> ingredientList){
        List<IngredientResponse> dtoList = new ArrayList<>();
        for (Ingredient ingredient : ingredientList) {
            dtoList.add(new IngredientResponse(ingredient.getName()));
        }
        return dtoList;
    }
}
