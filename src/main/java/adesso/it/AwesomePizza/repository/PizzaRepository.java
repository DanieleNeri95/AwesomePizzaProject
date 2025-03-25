package adesso.it.AwesomePizza.repository;

import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.entity.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PizzaRepository extends JpaRepository<Pizza, UUID> {
    Optional<Pizza> findByName(String name);

    List<Pizza> findAllByNameContains(String base);

    boolean existsByName(String name);
}
