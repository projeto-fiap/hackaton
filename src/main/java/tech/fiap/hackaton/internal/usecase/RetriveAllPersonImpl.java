package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.RetriveAllPerson;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RetriveAllPersonImpl implements RetriveAllPerson {
    private final PersonRepository personRepository;

    public RetriveAllPersonImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public List<PersonResponse> getAllPersons() {
        List<Person> persons = personRepository.findAll();
        return persons.stream()
                .map(person -> new PersonResponse(person.getId(), person.getNome(), person.getCpf(), person.getEmail()))
                .collect(Collectors.toList());
    }

}
