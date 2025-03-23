package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.IngredientDTO;
import adesso.it.AwesomePizza.entity.Ingredient;

import java.util.List;

public interface IngredientService {
    void createIngredient(String ingredientName);

    void deleteIngredient(String ingredientName);

    List<IngredientDTO> getAllIngredients();

    void createIngredients(List<String> ingredientNameList);
}
