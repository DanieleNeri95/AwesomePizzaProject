package adesso.it.AwesomePizza.service.serviceImpl;

import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
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

    private final PizzaMapper pizzaMapper;

    public PizzaServiceImpl(PizzaMapper pizzaMapper) {
        this.pizzaMapper = pizzaMapper;
    }

    @Override
    public void createPizza(PizzaDTO pizzaDTO) {
        var pizza = pizzaRepository.save(pizzaMapper.mapToEntity(pizzaDTO));
    }

    @Override
    public List<PizzaDTO> getAllPizzas() {
        return null;
    }

    @Override
    public PizzaDTO getPizzaById(UUID id) {
        return null;
    }

    @Override
    public PizzaDTO updatePizza(UUID id, PizzaDTO pizzaDTO) {
        return null;
    }

    @Override
    public boolean deletePizza(UUID id) {
        return false;
    }
}
