package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.dto.PersonDTO;

public interface CreatePerson {
    PersonResponse createPerson(PersonDTO personDTO);
}
