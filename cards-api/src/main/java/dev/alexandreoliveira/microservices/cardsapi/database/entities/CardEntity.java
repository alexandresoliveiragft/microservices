package dev.alexandreoliveira.microservices.cardsapi.database.entities;

import dev.alexandreoliveira.microservices.cardsapi.database.entities.enums.CardTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_cards")
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
public class CardEntity extends BaseEntity implements Serializable {

    @Column(name = "external_id", nullable = false)
    private UUID externalId;

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_type", nullable = false, length = 5)
    private CardTypeEnum cardType;

    @Column(name = "secure_code", nullable = false, length = 3)
    private String secureCode;

    @Column(name = "valid_date", nullable = false)
    private LocalDateTime validDate;

    @Column(name = "limit_value", nullable = false, precision = 20, scale = 2)
    private BigDecimal limitValue;

    @Column(name = "due_day", nullable = false)
    private Byte dueDay;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled;
}
