package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.UpdatePerson;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

@Service
public class UpdatePersonImpl implements UpdatePerson {
    private final PersonRepository personRepository;

    public UpdatePersonImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public PersonResponse updatePerson(Long id, PersonDTO personDTO) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

        person.setNome(personDTO.getNome());
        person.setCpf(personDTO.getCpf());
        person.setSenha(personDTO.getSenha());
        person.setEmail(personDTO.getEmail());

        Person updatedPerson = personRepository.save(person);

        return new PersonResponse(updatedPerson.getId(), updatedPerson.getNome(), updatedPerson.getCpf(), updatedPerson.getEmail());
    }

}
