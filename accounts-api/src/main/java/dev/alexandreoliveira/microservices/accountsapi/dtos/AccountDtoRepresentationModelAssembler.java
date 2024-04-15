package dev.alexandreoliveira.microservices.accountsapi.dtos;

import dev.alexandreoliveira.microservices.accountsapi.controllers.AccountsController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AccountDtoRepresentationModelAssembler implements RepresentationModelAssembler<AccountDto, EntityModel<AccountDto>> {

    @Override
    public EntityModel<AccountDto> toModel(AccountDto entity) {
        var links = new Link[] {
                linkTo(methodOn(AccountsController.class).show(entity.getId())).withSelfRel(),
        };

        return EntityModel.of(
                entity,
                links
        );
    }

    @Override
    public CollectionModel<EntityModel<AccountDto>> toCollectionModel(Iterable<? extends AccountDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
