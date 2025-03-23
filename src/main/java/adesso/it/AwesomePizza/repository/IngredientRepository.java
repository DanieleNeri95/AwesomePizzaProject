package adesso.it.AwesomePizza.repository;


import adesso.it.AwesomePizza.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    boolean existsByName(String name);
    List<Ingredient> findByNameIn(List<String> names);
}