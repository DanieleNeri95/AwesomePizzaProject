package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.IngredientResponse;

import java.util.List;

public interface IngredientService {
    void createIngredient(String ingredientName);

    void deleteIngredient(String ingredientName);

    List<IngredientResponse> getAllIngredients();

    void createIngredients(List<String> ingredientNameList);
}
