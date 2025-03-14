package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;

public interface CreatePersonUseCase {
    PersonResponse createPerson(PersonDTO personDTO);
}
