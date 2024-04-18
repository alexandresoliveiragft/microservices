package dev.alexandreoliveira.microservices.cardsapi.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardDto extends BaseDto implements Serializable {
    private UUID externalId;
    private String cardNumber;
    private String cardType;
    private String secureCode;
    private LocalDateTime validDate;
    private BigDecimal limitValue;
    private Byte dueDay;
}
