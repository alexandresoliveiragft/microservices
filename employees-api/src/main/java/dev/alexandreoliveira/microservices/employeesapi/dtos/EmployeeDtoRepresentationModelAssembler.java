package dev.alexandreoliveira.microservices.employeesapi.dtos;

import dev.alexandreoliveira.microservices.employeesapi.controllers.EmployeesController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmployeeDtoRepresentationModelAssembler implements RepresentationModelAssembler<EmployeeDto, EntityModel<EmployeeDto>> {

    @Override
    public EntityModel<EmployeeDto> toModel(EmployeeDto entity) {
        var links = new Link[] {
                linkTo(methodOn(EmployeesController.class).show(entity.getId())).withSelfRel(),
        };

        return EntityModel.of(
                entity,
                links
        );
    }

    @Override
    public CollectionModel<EntityModel<EmployeeDto>> toCollectionModel(Iterable<? extends EmployeeDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
