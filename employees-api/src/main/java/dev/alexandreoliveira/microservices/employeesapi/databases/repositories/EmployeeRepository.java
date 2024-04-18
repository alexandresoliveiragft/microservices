package dev.alexandreoliveira.microservices.employeesapi.databases.repositories;

import dev.alexandreoliveira.microservices.employeesapi.controllers.request.employees.EmployeesControllerIndexRequest;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.EmployeeEntity;
import dev.alexandreoliveira.microservices.employeesapi.helpers.ValidationHelper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID>, JpaSpecificationExecutor<EmployeeEntity> {

    Optional<EmployeeEntity> findByNameIgnoreCaseOrEmailIgnoreCaseOrPhoneNumber(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber
    );

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
    default Specification<EmployeeEntity> where(EmployeesControllerIndexRequest request, Example<EmployeeEntity> example) {
        return (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();

            if (ValidationHelper.isAllNull(example.getProbe())) {
                predicates.add(QueryByExamplePredicateBuilder.getPredicate(root, criteriaBuilder, example));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
