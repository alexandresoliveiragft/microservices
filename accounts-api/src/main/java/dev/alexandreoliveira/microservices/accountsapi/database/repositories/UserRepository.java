package dev.alexandreoliveira.microservices.accountsapi.database.repositories;

import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerIndexRequest;
import dev.alexandreoliveira.microservices.accountsapi.database.entities.UserEntity;
import dev.alexandreoliveira.microservices.accountsapi.helpers.ValidationHelper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmailIgnoreCaseOrMobileNumber(String email, String mobileNumber);

    default Specification<UserEntity> where(UserControllerIndexRequest request, Example<UserEntity> example) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.createdAtStart())) {
                Predicate createdAtStart = criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), request.createdAtStart());
                predicates.add(createdAtStart);
            }

            if (Objects.nonNull(request.createdAtEnd())) {
                Predicate createdAtEnd = criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), request.createdAtEnd());
                predicates.add(createdAtEnd);
            }

            if (Objects.nonNull(request.updatedAtStart())) {
                Predicate updatedAtStart = criteriaBuilder.greaterThanOrEqualTo(root.get("updatedAt"), request.updatedAtStart());
                predicates.add(updatedAtStart);
            }

            if (Objects.nonNull(request.updatedAtEnd())) {
                Predicate updatedAtEnd = criteriaBuilder.lessThanOrEqualTo(root.get("updatedAt"), request.updatedAtEnd());
                predicates.add(updatedAtEnd);
            }

            if (!CollectionUtils.isEmpty(request.accountsNumber())) {
                for (String account : request.accountsNumber()) {
                    Predicate like = criteriaBuilder.like(root.join("accounts").get("accountNumber"), "%" + account + "%");
                    predicates.add(like);
                }
            }

            if (!ValidationHelper.isAllNull(example.getProbe())) {
                predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, example));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
