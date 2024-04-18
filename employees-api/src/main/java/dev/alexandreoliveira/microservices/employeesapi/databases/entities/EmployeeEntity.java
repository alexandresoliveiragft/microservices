package dev.alexandreoliveira.microservices.employeesapi.databases.entities;

import dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums.EmployeeJobLevelEnum;
import dev.alexandreoliveira.microservices.employeesapi.databases.entities.enums.EmployeeJobTitleEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tbl_employees")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeEntity extends BaseEntity implements Serializable {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_title", nullable = false, length = 20)
    private EmployeeJobTitleEnum jobTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_level", nullable = false, length = 20)
    private EmployeeJobLevelEnum jobLevel;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;

    @OneToOne(mappedBy = "employee", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private AddressEntity address;
}
