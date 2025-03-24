package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.DTO.PizzaDTO;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.entity.Pizza;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.exeption.PizzaNameUsedException;
import adesso.it.AwesomePizza.mapper.PizzaMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.repository.PizzaRepository;
import adesso.it.AwesomePizza.service.serviceImpl.PizzaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PizzaServiceTest {

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private PizzaMapper pizzaMapper;

    @Mock
    private IngredientRepository ingredientRepository;

    private PizzaServiceImpl pizzaService;

    private static final String BASE = "BASE";

    @BeforeEach
    void setUp() {
        pizzaService = new PizzaServiceImpl(pizzaRepository, ingredientRepository, pizzaMapper);
    }

    @Test
    void testCreatePizza_NameAlreadyExists_ShouldThrowException() {

        PizzaDTO pizzaDTO = new PizzaDTO();
        List<IngredientResponse> ingredients = new ArrayList<>();
        ingredients.add(new IngredientResponse("Pomodoro"));
        ingredients.add(new IngredientResponse("Mozzarella"));
        pizzaDTO.setName("Margherita");
        pizzaDTO.setIngredients(ingredients);

        when(pizzaRepository.existsByName(BASE + pizzaDTO.getName())).thenReturn(true);

        Exception exception = assertThrows(PizzaNameUsedException.class, () -> pizzaService.createPizza(pizzaDTO));
        assertTrue(exception.getMessage().contains("Esiste già una pizza chiamata"));

        verify(pizzaRepository, never()).save(any());
    }

    @Test
    void testCreatePizza_InvalidIngredients_ShouldThrowException() {

        PizzaDTO pizzaDTO = new PizzaDTO();
        List<IngredientResponse> ingredients = new ArrayList<>();
        ingredients.add(new IngredientResponse("Pomodoro"));
        ingredients.add(new IngredientResponse("Mozzarella"));
        ingredients.add(new IngredientResponse("Salame Piccante2"));
        pizzaDTO.setName("Diavola");
        pizzaDTO.setIngredients(ingredients);

        when(pizzaRepository.existsByName(BASE + pizzaDTO.getName())).thenReturn(false);

        Exception exception = assertThrows(IngredientNotFoundException.class, () -> pizzaService.createPizza(pizzaDTO));
        assertEquals("L'ingrediente Pomodoro non disponibile.", exception.getMessage());

        verify(pizzaRepository, never()).save(any());
    }

    @Test
    void testCreatePizza_SuccessfulCreation_ShouldSavePizza() {
        PizzaDTO pizzaDTO = new PizzaDTO();
        List<IngredientResponse> ingredientsR = new ArrayList<>();
        ingredientsR.add(new IngredientResponse("Pomodoro"));
        ingredientsR.add(new IngredientResponse("Mozzarella"));
        ingredientsR.add(new IngredientResponse("Salame Piccante"));
        pizzaDTO.setName("Diavola");
        pizzaDTO.setIngredients(ingredientsR);

        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        ingredientsE.add(new Ingredient("Salame Piccante"));

        Pizza pizzaEntity = new Pizza(BASE + pizzaDTO.getName(), ingredientsE, pizzaDTO.getQuantity());

        when(pizzaRepository.existsByName(BASE + pizzaDTO.getName())).thenReturn(false);
        when(pizzaMapper.mapToEntity(pizzaDTO)).thenReturn(pizzaEntity);
        when(ingredientRepository.existsByName("Pomodoro")).thenReturn(true);
        when(ingredientRepository.existsByName("Mozzarella")).thenReturn(true);
        when(ingredientRepository.existsByName("Salame Piccante")).thenReturn(true);

        pizzaService.createPizza(pizzaDTO);

        verify(pizzaRepository, times(1)).save(pizzaEntity);
        assertEquals(BASE + "Diavola", pizzaDTO.getName());
    }

    @Test
    void testGetAllPizzas_ShouldReturnPizzaList() {

        PizzaDTO pizzaDTO = new PizzaDTO();
        List<IngredientResponse> ingredientsR = new ArrayList<>();
        ingredientsR.add(new IngredientResponse("Pomodoro"));
        ingredientsR.add(new IngredientResponse("Mozzarella"));
        ingredientsR.add(new IngredientResponse("Salame Piccante"));
        pizzaDTO.setName("Diavola");
        pizzaDTO.setIngredients(ingredientsR);

        List<Pizza> pizzaEntities = new ArrayList<>();
        Pizza pizza = new Pizza();
        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        ingredientsE.add(new Ingredient("Salame Piccante"));
        pizza.setName("Diavola");
        pizza.setIngredients(ingredientsE);


        List<PizzaDTO> pizzaDTOs = new ArrayList<>();
        pizzaDTOs.add(pizzaDTO);
        pizzaDTOs.add(pizzaDTO);

        when(pizzaRepository.findAllByNameContains(BASE)).thenReturn(pizzaEntities);
        when(pizzaMapper.toDTOList(pizzaEntities)).thenReturn(pizzaDTOs);

        List<PizzaDTO> result = pizzaService.getAllPizzas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Diavola", result.get(0).getName());
        assertEquals("Diavola", result.get(1).getName());

        verify(pizzaRepository, times(1)).findAllByNameContains(BASE);
        verify(pizzaMapper, times(1)).toDTOList(pizzaEntities);
    }

    @Test
    void testGetAllPizzas_EmptyList_ShouldReturnEmptyList() {
        when(pizzaRepository.findAllByNameContains(BASE)).thenReturn(Collections.emptyList());
        when(pizzaMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<PizzaDTO> result = pizzaService.getAllPizzas();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(pizzaRepository, times(1)).findAllByNameContains(BASE);
        verify(pizzaMapper, times(1)).toDTOList(Collections.emptyList());
    }

    @Test
    void testDeletePizza_PizzaExists_ShouldReturnTrue() {
        UUID pizzaId = UUID.randomUUID();
        Pizza pizza = new Pizza();
        List<Ingredient> ingredientsE = new ArrayList<>();
        ingredientsE.add(new Ingredient("Pomodoro"));
        ingredientsE.add(new Ingredient("Mozzarella"));
        ingredientsE.add(new Ingredient("Salame Piccante"));
        pizza.setName("Diavola");
        pizza.setIngredients(ingredientsE);
        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.of(pizza));

        boolean result = pizzaService.deletePizza(pizzaId);

        assertTrue(result);
        verify(pizzaRepository, times(1)).findById(pizzaId);
        verify(pizzaRepository, times(1)).delete(pizza);
    }

    @Test
    void testDeletePizza_PizzaDoesNotExist_ShouldReturnFalse() {
        UUID pizzaId = UUID.randomUUID();

        when(pizzaRepository.findById(pizzaId)).thenReturn(Optional.empty());

        boolean result = pizzaService.deletePizza(pizzaId);

        assertFalse(result);
        verify(pizzaRepository, times(1)).findById(pizzaId);
        verify(pizzaRepository, never()).delete(any());
    }

}