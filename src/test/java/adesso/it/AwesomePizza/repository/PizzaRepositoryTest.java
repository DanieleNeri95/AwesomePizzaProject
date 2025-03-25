package adesso.it.AwesomePizza.repository;

import adesso.it.AwesomePizza.entity.Pizza;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PizzaRepositoryTest {

    @Autowired
    private PizzaRepository pizzaRepository;

    // Dati di test
    private Pizza pizzaMargherita;
    private Pizza pizzaDiavola;

    @BeforeEach
    void setUp() {
        // Crea due pizze di esempio
        pizzaMargherita = new Pizza("BASEMargheritaTest", null, 0);
        pizzaMargherita.setPrice(10.0);
        pizzaDiavola = new Pizza("BASEDiavolaTest", null, 0);
        pizzaDiavola.setPrice(11.0);

        // Salva le pizze nel repository prima di ogni test
        pizzaRepository.save(pizzaMargherita);
        pizzaRepository.save(pizzaDiavola);
    }

    @Test
    void testFindByName() {
        Optional<Pizza> pizza = pizzaRepository.findByName("BASEMargheritaTest");

        assertTrue(pizza.isPresent(), "La pizza BASEMargheritaTest dovrebbe essere presente");
        assertEquals("BASEMargheritaTest", pizza.get().getName(), "Il nome della pizza dovrebbe essere BASEMargheritaTest");
    }

    @Test
    void testFindByName_ShouldReturnEmptyWhenNotFound() {
        Optional<Pizza> pizza = pizzaRepository.findByName("NonExistentPizza");

        assertFalse(pizza.isPresent(), "La pizza NonExistentPizza non dovrebbe essere trovata");
    }

    @Test
    void testFindAllByNameContains() {
        List<Pizza> pizzas = pizzaRepository.findAllByNameContains("BASEDiavolaTest");

        assertNotNull(pizzas, "La lista di pizze non dovrebbe essere nulla");
        assertEquals(1, pizzas.size(), "Dovrebbe esserci solo una pizza con nome che contiene 'BASEDiavolaTest'");
        assertEquals("BASEDiavolaTest", pizzas.get(0).getName(), "La pizza trovata dovrebbe essere BASEDiavolaTest");
    }

    @Test
    void testExistsByName() {
        boolean exists = pizzaRepository.existsByName("BASEDiavolaTest");
        assertTrue(exists, "La pizza BASEDiavolaTest dovrebbe esistere");

        exists = pizzaRepository.existsByName("NonExistentPizza");
        assertFalse(exists, "La pizza NonExistentPizza non dovrebbe esistere");
    }

    @Test
    void testSavePizza() {
        Pizza newPizza = new Pizza("BASECapricciosaTest", null, 0);
        Pizza savedPizza = pizzaRepository.save(newPizza);

        assertNotNull(savedPizza, "La pizza salvata non dovrebbe essere nulla");
        assertNotNull(savedPizza.getId(), "L'ID della pizza salvata non dovrebbe essere nullo");
        assertEquals("BASECapricciosaTest", savedPizza.getName(), "Il nome della pizza salvata dovrebbe essere BASECapricciosaTest");
    }

    @Test
    void testDeletePizza() {
        pizzaRepository.delete(pizzaMargherita);

        Optional<Pizza> deletedPizza = pizzaRepository.findByName("BASEMargheritaTest");
        assertFalse(deletedPizza.isPresent(), "La pizza BASEMargheritaTest dovrebbe essere stata eliminata");
    }

    @Test
    void testFindAll() {
        List<Pizza> pizzas = pizzaRepository.findAll();

        assertNotNull(pizzas, "La lista di pizze non dovrebbe essere nulla");
        assertTrue(pizzas.size() > 0, "Dovrebbero esserci almeno delle pizze nel database");
    }
}
