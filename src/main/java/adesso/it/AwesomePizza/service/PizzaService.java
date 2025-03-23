package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.PizzaDTO;

import java.util.List;
import java.util.UUID;

public interface PizzaService {
    void createPizza(PizzaDTO pizzaDTO);

    List<PizzaDTO> getAllPizzas();

    boolean deletePizza(UUID id);
}
