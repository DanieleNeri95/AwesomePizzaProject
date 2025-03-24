package adesso.it.AwesomePizza.service.serviceImpl;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.exeption.PizzaNameUsedException;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.repository.PizzaRepository;
import adesso.it.AwesomePizza.service.PizzaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PizzaServiceImpl implements PizzaService {

    @Autowired
    PizzaRepository pizzaRepository;
    @Autowired
    IngredientRepository ingredientRepository;

    private final PizzaMapper pizzaMapper;
    private final String BASE = "BASE";

    public PizzaServiceImpl(PizzaMapper pizzaMapper) {
        this.pizzaMapper = pizzaMapper;
    }

    @Override
    public void createPizza(PizzaDTO pizzaDTO) {
        if(pizzaRepository.existsByName(BASE+pizzaDTO.getName()))
            throw new PizzaNameUsedException("Esiste già una pizza chiamata "+pizzaDTO.getName());
        ingredientsCheck(pizzaDTO.getIngredients());
        pizzaDTO.setName(BASE+pizzaDTO.getName());
        pizzaRepository.save(pizzaMapper.mapToEntity(pizzaDTO));
    }

    @Override
    public List<PizzaDTO> getAllPizzas() {
        return pizzaMapper.toDTOList(pizzaRepository.findAllByNameContains(BASE));
    }

    @Override
    public boolean deletePizza(UUID id) {
        return false;
    }

    // verifico l'esistenza degli ingredienti
    private void ingredientsCheck(List<IngredientResponse> ingredientList){
        for (IngredientResponse ingredientResponse: ingredientList) {
            if(!ingredientRepository.existsByName(ingredientResponse.getName()))
                throw new IngredientNotFoundException("L'ingrediente "+ingredientResponse.getName()+" non disponibile.");
        }
    }
}
