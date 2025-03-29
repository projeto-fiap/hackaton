package tech.fiap.hackaton.internal.usecase;


import tech.fiap.hackaton.api.model.PersonResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.CreatePerson;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

@Service
public class CreatePersonImpl implements CreatePerson {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public CreatePersonImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PersonResponse createPerson(PersonDTO personDTO) {
        Person person = new Person();
        person.setNome(personDTO.getNome());
        person.setCpf(personDTO.getCpf());
        person.setSenha(passwordEncoder.encode(personDTO.getSenha())); // Criptografa a senha
        person.setEmail(personDTO.getEmail());

        Person savedPerson = personRepository.save(person);

        return new PersonResponse(
                savedPerson.getId(),
                savedPerson.getNome(),
                savedPerson.getCpf(),
                savedPerson.getEmail()
        );
    }
}