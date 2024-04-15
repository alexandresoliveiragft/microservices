package dev.alexandreoliveira.microservices.accountsapi.database.entities;

import dev.alexandreoliveira.microservices.accountsapi.database.entities.enums.AccountTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Random;

@Entity
@Table(name = "tbl_accounts")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountEntity extends BaseEntity implements Serializable {

    @Column(name = "account_number", nullable = false, unique = true, length = 10)
    private String accountNumber;

    @Column(name = "account_type", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private AccountTypeEnum accountType;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @PrePersist
    public void prePersist() {
        if (StringUtils.hasText(accountNumber)) {
            return;
        }

        int randomAccountNumber = new Random().ints(1, Integer.MAX_VALUE).findFirst().getAsInt();

        this.accountNumber = String.format("%" + 10 + "s", randomAccountNumber).replace(' ', '0');
    }
}
