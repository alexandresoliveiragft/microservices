package dev.alexandreoliveira.microservices.cardsapi.dtos;

import dev.alexandreoliveira.microservices.cardsapi.controllers.CardsController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CardDtoRepresentationModelAssembler implements RepresentationModelAssembler<CardDto, EntityModel<CardDto>> {

    @Override
    public EntityModel<CardDto> toModel(CardDto entity) {
        var links = new Link[] {
                linkTo(methodOn(CardsController.class).show(entity.getId())).withSelfRel(),
        };

        return EntityModel.of(
                entity,
                links
        );
    }

    @Override
    public CollectionModel<EntityModel<CardDto>> toCollectionModel(Iterable<? extends CardDto> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }
}
