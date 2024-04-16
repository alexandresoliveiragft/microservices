package dev.alexandreoliveira.microservices.cardsapi.database.repositories;

import dev.alexandreoliveira.microservices.cardsapi.database.entities.CardEntity;
import dev.alexandreoliveira.microservices.cardsapi.helpers.ValidationHelper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public interface CardsRepository extends JpaRepository<CardEntity, UUID>, JpaSpecificationExecutor<CardEntity> {

    /**
     * Where condition example:
     * <code>
     *     if (Objects.nonNull(request.createdAtStart())) {
     *         Predicate createdAtStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.createdAtStart());
     *         predicates.add(createdAtStart);
     *     }
     * </code>
     *
     */
    default Specification<CardEntity> where(Example<CardEntity> example) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (ValidationHelper.isAllNull(example.getProbe())) {
                predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, example));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
