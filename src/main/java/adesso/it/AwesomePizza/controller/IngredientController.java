package adesso.it.AwesomePizza.controller;

import adesso.it.AwesomePizza.DTO.IngredientDTO;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping("/{ingredientName}")
    public ResponseEntity<IngredientDTO> createIngredient(@PathVariable String ingredientName) {
        ingredientService.createIngredient(ingredientName);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<IngredientDTO> createIngredients(@RequestBody List<String> ingredientNameList) {
        ingredientService.createIngredients(ingredientNameList);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ingredientName}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable String ingredientName) {
        ingredientService.deleteIngredient(ingredientName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<IngredientDTO>> getAllIngredients() {
        return new ResponseEntity<>(ingredientService.getAllIngredients(), HttpStatus.OK);
    }
}