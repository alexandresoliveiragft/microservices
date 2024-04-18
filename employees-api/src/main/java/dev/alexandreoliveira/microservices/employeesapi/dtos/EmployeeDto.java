package dev.alexandreoliveira.microservices.employeesapi.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeDto extends BaseDto implements Serializable {
    private String name;
    private String email;
    private String phoneNumber;
    private String jobTitle;
    private String jobLevel;
    private Boolean isEnabled;
    private AddressDto address;
}
