package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.api.model.PersonResponse;

public interface RetriveByIdPerson {

	PersonResponse getPersonById(Long id);

}
