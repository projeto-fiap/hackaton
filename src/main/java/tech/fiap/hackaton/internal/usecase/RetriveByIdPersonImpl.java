package tech.fiap.hackaton.internal.usecase;

import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.RetriveByIdPerson;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

@Service
public class RetriveByIdPersonImpl implements RetriveByIdPerson {

	private final PersonRepository personRepository;

	public RetriveByIdPersonImpl(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Override
	public PersonResponse getPersonById(Long id) {
		Person person = personRepository.findById(id).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));

		return new PersonResponse(person.getId(), person.getNome(), person.getCpf(), person.getEmail());
	}

}
