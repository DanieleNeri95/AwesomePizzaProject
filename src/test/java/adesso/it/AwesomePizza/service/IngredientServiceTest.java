package adesso.it.AwesomePizza.service;

import adesso.it.AwesomePizza.DTO.IngredientResponse;
import adesso.it.AwesomePizza.entity.Ingredient;
import adesso.it.AwesomePizza.exeption.IngredientAlreadyExistsException;
import adesso.it.AwesomePizza.exeption.IngredientNotFoundException;
import adesso.it.AwesomePizza.mapper.IngredientMapper;
import adesso.it.AwesomePizza.repository.IngredientRepository;
import adesso.it.AwesomePizza.service.serviceImpl.IngredientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {

    @Mock
    IngredientRepository ingredientRepository;

    @Mock
    IngredientMapper ingredientMapper;

    private IngredientServiceImpl ingredientService;

    @BeforeEach
    void setUp() {
        ingredientService = new IngredientServiceImpl(ingredientMapper, ingredientRepository);
    }

    @Test
    void testCreateIngredient_IngredientDoesNotExist_ShouldSaveIngredient() {
        String ingredientName = "Basilico";

        when(ingredientRepository.existsByName(ingredientName)).thenReturn(false);

        ingredientService.createIngredient(ingredientName);

        verify(ingredientRepository, times(1)).existsByName(ingredientName);
        verify(ingredientRepository, times(1)).save(new Ingredient(ingredientName));
    }

    @Test
    void testCreateIngredient_IngredientAlreadyExists_ShouldThrowException() {
        String ingredientName = "Mozzarella";

        when(ingredientRepository.existsByName(ingredientName)).thenReturn(true);

        Exception exception = assertThrows(IngredientAlreadyExistsException.class, () -> ingredientService.createIngredient(ingredientName));

        assertEquals("L'ingrediente 'Mozzarella' esiste già.", exception.getMessage());

        verify(ingredientRepository, times(1)).existsByName(ingredientName);
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void testDeleteIngredient_IngredientExists_ShouldDeleteIngredient() {
        String ingredientName = "Basilico";
        Ingredient ingredient = new Ingredient(ingredientName);

        when(ingredientRepository.findByName(ingredientName)).thenReturn(Optional.of(ingredient));

        ingredientService.deleteIngredient(ingredientName);

        verify(ingredientRepository, times(1)).findByName(ingredientName);
        verify(ingredientRepository, times(1)).delete(ingredient);
    }

    @Test
    void testDeleteIngredient_IngredientDoesNotExist_ShouldThrowException() {
        String ingredientName = "Pancetta";

        when(ingredientRepository.findByName(ingredientName)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IngredientNotFoundException.class, () -> ingredientService.deleteIngredient(ingredientName));

        assertEquals("L'ingrediente 'Pancetta' non esiste.", exception.getMessage());

        verify(ingredientRepository, times(1)).findByName(ingredientName);
        verify(ingredientRepository, never()).delete(any());
    }

    @Test
    void testGetAllIngredients_ShouldReturnIngredientList() {
        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(new Ingredient("Pomodoro"));
        ingredientList.add(new Ingredient("Mozzarella"));
        ingredientList.add(new Ingredient("Basilico"));

        List<IngredientResponse> ingredientResponseList = new ArrayList<>();
        ingredientResponseList.add(new IngredientResponse("Pomodoro"));
        ingredientResponseList.add(new IngredientResponse("Mozzarella"));
        ingredientResponseList.add(new IngredientResponse("Basilico"));


        when(ingredientRepository.findAll()).thenReturn(ingredientList);
        when(ingredientMapper.entotyToListDTO(ingredientList)).thenReturn(ingredientResponseList);

        List<IngredientResponse> result = ingredientService.getAllIngredients();

        assertEquals(3, result.size());
        assertEquals("Pomodoro", result.get(0).getName());
        assertEquals("Mozzarella", result.get(1).getName());
        assertEquals("Basilico", result.get(2).getName());

        verify(ingredientRepository, times(1)).findAll();
        verify(ingredientMapper, times(1)).entotyToListDTO(ingredientList);
    }

    @Test
    void testGetAllIngredients_EmptyList_ShouldReturnEmptyList() {
        when(ingredientRepository.findAll()).thenReturn(Collections.emptyList());
        when(ingredientMapper.entotyToListDTO(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<IngredientResponse> result = ingredientService.getAllIngredients();

        assertTrue(result.isEmpty());

        verify(ingredientRepository, times(1)).findAll();
        verify(ingredientMapper, times(1)).entotyToListDTO(Collections.emptyList());
    }

    @Test
    void testCreateIngredients_SuccessfulCreation_ShouldSaveAllIngredients() {
        List<String> ingredientNames = List.of("Pomodoro", "Mozzarella", "Basilico");

        when(ingredientRepository.existsByName(anyString())).thenReturn(false);

        ingredientService.createIngredients(ingredientNames);

        for (String ingredientName : ingredientNames) {
            verify(ingredientRepository, times(1)).save(new Ingredient(ingredientName));
        }
    }

    @Test
    void testCreateIngredients_EmptyList_ShouldThrowException() {
        List<String> emptyList = Collections.emptyList();

        Exception exception = assertThrows(IngredientNotFoundException.class, () -> ingredientService.createIngredients(emptyList));
        assertEquals("La lista degli ingredienti deve contenere almeno un ingrediente.", exception.getMessage());

        verify(ingredientRepository, never()).save(any());
    }

    @Test
    void testCreateIngredients_IngredientAlreadyExists_ShouldThrowException() {
        List<String> ingredientNames = List.of("Pomodoro", "Mozzarella");

        when(ingredientRepository.existsByName("Pomodoro")).thenReturn(true);

        Exception exception = assertThrows(IngredientAlreadyExistsException.class, () -> ingredientService.createIngredients(ingredientNames));
        assertEquals("L'ingrediente 'Pomodoro' esiste già.", exception.getMessage());

        verify(ingredientRepository, never()).save(any());
    }

}
