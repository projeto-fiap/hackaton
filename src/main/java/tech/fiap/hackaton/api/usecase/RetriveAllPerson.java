package tech.fiap.hackaton.api.usecase;

import tech.fiap.hackaton.api.model.PersonResponse;

import java.util.List;

public interface RetriveAllPerson {
    List<PersonResponse> getAllPersons();
}
