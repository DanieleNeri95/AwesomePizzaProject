package adesso.it.AwesomePizza.service.serviceImpl;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.exeption.IngredientAlreadyExistsException;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.mapper.IngredientMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.service.IngredientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class IngredientServiceImpl implements IngredientService {

    public IngredientServiceImpl(IngredientMapper ingredientMapper, IngredientRepository ingredientRepository) {
        this.ingredientMapper = ingredientMapper;
        this.ingredientRepository = ingredientRepository;
    }

    private final IngredientMapper ingredientMapper;

    private final IngredientRepository ingredientRepository;


    @Override
    public void createIngredient(String ingredientName) {

        if(ingredientRepository.existsByName(ingredientName))
            throw new IngredientAlreadyExistsException("L'ingrediente '" + ingredientName + "' esiste già.");

        ingredientRepository.save(new Ingredient(ingredientName));
    }

    @Override
    public void deleteIngredient(String ingredientName) {
        var ingredient = ingredientRepository.findByName(ingredientName);
        if(!ingredient.isPresent())
            throw new IngredientNotFoundException("L'ingrediente '" + ingredientName + "' non esiste.");

        ingredientRepository.delete(ingredient.get());
    }

    @Override
    public List<IngredientResponse> getAllIngredients() {
        return ingredientMapper.entotyToListDTO(ingredientRepository.findAll());
    }

    @Override
    @Transactional
    public void createIngredients(List<String> ingredientNameList) {
        if(!CollectionUtils.isEmpty(ingredientNameList)){
            for (String ingredientName: ingredientNameList) {
                createIngredient(ingredientName);
            }
        }else
            throw new IngredientNotFoundException("La lista degli ingredienti deve contenere almeno un ingrediente.");
    }
}
