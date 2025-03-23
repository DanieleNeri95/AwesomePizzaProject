package adesso.it.AwesomePizza.controller;

import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.service.PizzaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/pizza")
public class PizzaController {

    private final PizzaService pizzaService;

    public PizzaController(PizzaService pizzaService) {
        this.pizzaService = pizzaService;
    }


    @PostMapping
    public ResponseEntity createPizza(@Valid @RequestBody PizzaDTO pizzaDTO) {
        pizzaService.createPizza(pizzaDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PizzaDTO>> getAllPizzas() {
        List<PizzaDTO> pizzas = pizzaService.getAllPizzas();
        return ResponseEntity.ok(pizzas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PizzaDTO> getPizzaById(@PathVariable UUID id) {
        PizzaDTO pizza = pizzaService.getPizzaById(id);
        return pizza != null ? ResponseEntity.ok(pizza) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PizzaDTO> updatePizza(@PathVariable UUID id, @Valid @RequestBody PizzaDTO pizzaDTO) {
        PizzaDTO updatedPizza = pizzaService.updatePizza(id, pizzaDTO);
        return updatedPizza != null ? ResponseEntity.ok(updatedPizza) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePizza(@PathVariable UUID id) {
        boolean deleted = pizzaService.deletePizza(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


}
