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
    private final String BASE = "BASE";

    public PizzaServiceImpl(PizzaMapper pizzaMapper) {
        this.pizzaMapper = pizzaMapper;
    }

    @Override
    public void createPizza(PizzaDTO pizzaDTO) {
        var pizza = pizzaRepository.save(pizzaMapper.mapToEntity(pizzaDTO));
    }

    @Override
    public List<PizzaDTO> getAllPizzas() {
        return pizzaMapper.toDTOList(pizzaRepository.findAllByNameContains(BASE));
    }

    @Override
    public boolean deletePizza(UUID id) {
        return false;
    }
}
