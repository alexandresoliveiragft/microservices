package dev.alexandreoliveira.microservices.employeesapi.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddressDto extends BaseDto implements Serializable {
    private String description;
    private Integer number;
    private String complement;
    private String zipCode;
    private String neighborhood;
    private String city;
    private String state;
}
