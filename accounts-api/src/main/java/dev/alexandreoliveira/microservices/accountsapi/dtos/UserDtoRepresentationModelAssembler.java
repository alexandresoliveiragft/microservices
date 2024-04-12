package dev.alexandreoliveira.microservices.accountsapi.dtos;

import dev.alexandreoliveira.microservices.accountsapi.controllers.UsersController;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerCreateRequest;
import dev.alexandreoliveira.microservices.accountsapi.controllers.data.users.UserControllerUpdateRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.sql.Array;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserDtoRepresentationModelAssembler implements RepresentationModelAssembler<UserDto, EntityModel<UserDto>> {
    @Override
    public EntityModel<UserDto> toModel(UserDto entity) {
        var links = new Link[] {
                linkTo(methodOn(UsersController.class).show(entity.getId())).withSelfRel(),
        };

        return EntityModel.of(
                entity,
                links
        );
    }

    @Override
    public CollectionModel<EntityModel<UserDto>> toCollectionModel(Iterable<? extends UserDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
