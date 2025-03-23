package adesso.it.AwesomePizza.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pizza_ingredients",
            joinColumns = @JoinColumn(name = "pizza_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients;

    @Column(nullable = false)
    private Double price;

    private int quantity;

    @ManyToMany(mappedBy = "orderedPizzas", fetch = FetchType.LAZY)
    private List<Order> orders;

    public Pizza(String name, List<Ingredient> ingredients, int quantity) {
        this.name = name;
        this.ingredients = ingredients;
        this.quantity = quantity;
    }
}
