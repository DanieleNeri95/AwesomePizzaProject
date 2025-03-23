package adesso.it.AwesomePizza.entity;

import adesso.it.AwesomePizza.utils.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String code;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Pizza> orderedPizzas;

    @Column(nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private String takedBy;

    @Column(nullable = false)
    private Date createdAt;

    private Date updatedAt;

    public Order(String code, List<Pizza> orderedPizzas, double totalPrice, Date createdAt, OrderStatus status) {
        this.code = code;
        this.orderedPizzas = orderedPizzas;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.status = status;
    }
}
