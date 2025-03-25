package adesso.it.AwesomePizza.repository;

import adesso.it.AwesomePizza.entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Usa il database reale
public class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    private Ingredient mozzarella;
    private Ingredient pomodoro;
    private Ingredient basilico;

    @BeforeEach
    void setUp() {
        mozzarella = new Ingredient("MozzarellaTest");
        pomodoro = new Ingredient("PomodoroTest");
        basilico = new Ingredient("BasilicoTest");

        ingredientRepository.save(mozzarella);
        ingredientRepository.save(pomodoro);
        ingredientRepository.save(basilico);
    }

    @Test
    void testFindByName() {
        Optional<Ingredient> found = ingredientRepository.findByName("MozzarellaTest");

        assertTrue(found.isPresent(), "L'ingrediente MozzarellaTest dovrebbe essere presente");
        assertEquals("Mozzarella", found.get().getName(), "Il nome dell'ingrediente dovrebbe essere MozzarellaTest");
    }

    @Test
    void testFindByName_ShouldReturnEmptyWhenNotFound() {
        Optional<Ingredient> found = ingredientRepository.findByName("NonEsistente");

        assertFalse(found.isPresent(), "L'ingrediente NonEsistente non dovrebbe essere trovato");
    }

    @Test
    void testExistsByName() {
        boolean exists = ingredientRepository.existsByName("PomodoroTest");

        assertTrue(exists, "L'ingrediente PomodoroTest dovrebbe esistere");

        exists = ingredientRepository.existsByName("AnanasTest");
        assertFalse(exists, "L'ingrediente AnanasTest non dovrebbe esistere");
    }

    @Test
    void testFindByNameIn() {
        List<String> ingredientNames = List.of("MozzarellaTest", "BasilicoTest");
        List<Ingredient> foundIngredients = ingredientRepository.findByNameIn(ingredientNames);

        assertNotNull(foundIngredients, "La lista di ingredienti trovati non dovrebbe essere nulla");
        assertEquals(2, foundIngredients.size(), "Dovrebbero essere trovati due ingredienti");
        assertTrue(foundIngredients.stream().anyMatch(i -> i.getName().equals("MozzarellaTest")), "Dovrebbe esserci MozzarellaTest");
        assertTrue(foundIngredients.stream().anyMatch(i -> i.getName().equals("BasilicoTest")), "Dovrebbe esserci BasilicoTest");
    }

    @Test
    void testSaveIngredient() {
        Ingredient nuovoIngrediente = new Ingredient("OliveTest");
        Ingredient savedIngredient = ingredientRepository.save(nuovoIngrediente);

        assertNotNull(savedIngredient, "L'ingrediente salvato non dovrebbe essere nullo");
        assertNotNull(savedIngredient.getId(), "L'ID dell'ingrediente salvato non dovrebbe essere nullo");
        assertEquals("Olive", savedIngredient.getName(), "Il nome dell'ingrediente salvato dovrebbe essere OliveTest");
    }

    @Test
    void testDeleteIngredient() {
        ingredientRepository.delete(pomodoro);

        Optional<Ingredient> deletedIngredient = ingredientRepository.findByName("PomodoroTest");
        assertFalse(deletedIngredient.isPresent(), "L'ingrediente PomodoroTest dovrebbe essere stato eliminato");
    }

    @Test
    void testFindAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();

        assertNotNull(ingredients, "La lista di ingredienti non dovrebbe essere nulla");
        assertTrue(ingredients.size() >= 3, "Dovrebbero esserci almeno tre ingredienti nel database");
    }
}
