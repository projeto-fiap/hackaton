package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.DeletePerson;
import tech.fiap.hackaton.internal.repository.PersonRepository;
@Service
public class DeletePersonImpl implements DeletePerson {

    private final PersonRepository personRepository;

    public DeletePersonImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void deletePerson(Long id) {
        if (!personRepository.existsById(id)) {
            throw new RuntimeException("Pessoa não encontrada");
        }
        personRepository.deleteById(id);
    }
}
