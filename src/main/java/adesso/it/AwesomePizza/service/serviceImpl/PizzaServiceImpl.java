package adesso.it.AwesomePizza.service.serviceImpl;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.exeption.PizzaNameUsedException;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.repository.PizzaRepository;
import adesso.it.AwesomePizza.service.PizzaService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;
    private final IngredientRepository ingredientRepository;

    private final PizzaMapper pizzaMapper;
    private final String BASE = "BASE";

    public PizzaServiceImpl(PizzaRepository pizzaRepository, IngredientRepository ingredientRepository, PizzaMapper pizzaMapper) {
        this.pizzaRepository = pizzaRepository;
        this.ingredientRepository = ingredientRepository;
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
        Optional<Pizza> pizza = pizzaRepository.findById(id);
        if(pizza.isPresent()){
            pizzaRepository.delete(pizza.get());
            return true;
        }
        return false;
    }

    // verifico l'esistenza degli ingredienti
    public void ingredientsCheck(List<IngredientResponse> ingredientList){
        if(CollectionUtils.isEmpty(ingredientList))
            throw new IngredientNotFoundException("La lista degli ingredienti non può essere vuota.");
        for (IngredientResponse ingredientResponse: ingredientList) {
            if(!ingredientRepository.existsByName(ingredientResponse.getName()))
                throw new IngredientNotFoundException("L'ingrediente "+ingredientResponse.getName()+" non disponibile.");
        }
    }
}
