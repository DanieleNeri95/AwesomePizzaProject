package adesso.it.AwesomePizza.repository;

import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.utils.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order pendingOrder;
    private Order completedOrder;

    @BeforeEach
    void setUp() {
        pendingOrder = new Order();
        pendingOrder.setCode("BJ12");
        pendingOrder.setStatus(OrderStatus.QUEUED);
        pendingOrder.setCreatedAt(new Date());
        pendingOrder.setTakedBy("Marco");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = cal.getTime();

        completedOrder = new Order();
        completedOrder.setCode("IP32");
        completedOrder.setStatus(OrderStatus.DELIVERED);
        completedOrder.setCreatedAt(yesterday);
        completedOrder.setTakedBy("Lorenzo");

        orderRepository.save(pendingOrder);
        orderRepository.save(completedOrder);
    }

    @Test
    void testExistsByCode() {
        boolean exists = orderRepository.existsByCode("BJ12");

        assertTrue(exists, "L'ordine BJ12 dovrebbe esistere");

        exists = orderRepository.existsByCode("NONEXISTENT");
        assertFalse(exists, "L'ordine NONEXISTENT non dovrebbe esistere");
    }

    @Test
    void testFindByStatusOrderByCreatedAtAsc() {
        List<Order> orders = orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.QUEUED);

        assertNotNull(orders, "La lista degli ordini non dovrebbe essere nulla");
        assertNotEquals("BJ12", orders.get(0).getCode(), "L'ordine più vecchio dovrebbe essere BJ12");
    }

    @Test
    void testFindByCodeAndStatus() {
        Optional<Order> order = orderRepository.findByCodeAndStatus("BJ12", OrderStatus.QUEUED);

        assertTrue(order.isPresent(), "L'ordine BJ12 con stato PENDING dovrebbe esistere");
        assertEquals("BJ12", order.get().getCode(), "Il codice dell'ordine dovrebbe essere ORDER123");
    }

    @Test
    void testFindByCodeAndStatus_ShouldReturnEmptyWhenNotFound() {
        Optional<Order> order = orderRepository.findByCodeAndStatus("BJ12", OrderStatus.PICKED_UP);

        assertFalse(order.isPresent(), "L'ordine BJ12 con stato COMPLETED non dovrebbe essere trovato");
    }

    @Test
    void testExistsByTakedByAndStatusNot() {
        boolean exists = orderRepository.existsByTakedByAndStatusNot("Marco", OrderStatus.PICKED_UP);

        assertTrue(exists, "L'ordine assegnato a Marco dovrebbe esistere e non essere in stato PRESO IN CARICO");

        exists = orderRepository.existsByTakedByAndStatusNot("Lorenzo", OrderStatus.DELIVERED);
        assertFalse(exists, "L'ordine assegnato a Lorenzo non dovrebbe esistere con stato diverso da CONSEGANTO");
    }

    @Test
    void testFindByCode() {
        Optional<Order> order = orderRepository.findByCode("IP32");

        assertTrue(order.isPresent(), "L'ordine IP32 dovrebbe esistere");
        assertEquals(OrderStatus.DELIVERED, order.get().getStatus(), "L'ordine IP32 dovrebbe avere stato IN CODA");
    }

    @Test
    void testFindByCode_ShouldReturnEmptyWhenNotFound() {
        Optional<Order> order = orderRepository.findByCode("NONEXISTENT");

        assertFalse(order.isPresent(), "L'ordine NONEXISTENT non dovrebbe essere trovato");
    }

    @Test
    void testSaveOrder() {
        Order newOrder = new Order();
        newOrder.setCode("TU85");
        newOrder.setStatus(OrderStatus.QUEUED);
        newOrder.setCreatedAt(new Date());
        newOrder.setTakedBy("user3");

        Order savedOrder = orderRepository.save(newOrder);

        assertNotNull(savedOrder, "L'ordine salvato non dovrebbe essere nullo");
        assertNotNull(savedOrder.getId(), "L'ID dell'ordine salvato non dovrebbe essere nullo");
        assertEquals("TU85", savedOrder.getCode(), "Il codice dell'ordine salvato dovrebbe essere TU85");
    }

    @Test
    void testDeleteOrder() {
        orderRepository.delete(completedOrder);

        Optional<Order> deletedOrder = orderRepository.findByCode("IP32");
        assertFalse(deletedOrder.isPresent(), "L'ordine IP32 dovrebbe essere stato eliminato");
    }

    @Test
    void testFindAll() {
        List<Order> orders = orderRepository.findAll();

        assertNotNull(orders, "La lista degli ordini non dovrebbe essere nulla");
        assertTrue(orders.size() >= 2, "Dovrebbero esserci almeno due ordini nel database");
    }
}
