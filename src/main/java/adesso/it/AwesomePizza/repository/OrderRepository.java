package adesso.it.AwesomePizza.repository;

import adesso.it.AwesomePizza.entity.Order;
import adesso.it.AwesomePizza.utils.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    boolean existsByCode(String code);

    List<Order> findByStatusOrderByCreatedAtAsc(Enum status);

    Optional<Order> findByCodeAndStatus(String code, OrderStatus status);

    boolean existsByTakedByAndStatusNot(String takedBy, OrderStatus status);

    Optional<Order> findByCode(String code);
}
