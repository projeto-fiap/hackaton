package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetriveAllPersonImplTest {

	@Mock
	private PersonRepository personRepository;

	@InjectMocks
	private RetriveAllPersonImpl retriveAllPerson;

	@Test
	void getAllPersons_ShouldReturnEmptyListWhenNoPersonsExist() {
		// Arrange
		when(personRepository.findAll()).thenReturn(List.of());

		// Act
		List<PersonResponse> result = retriveAllPerson.getAllPersons();

		// Assert
		assertTrue(result.isEmpty());
		verify(personRepository, times(1)).findAll();
	}

	@Test
	void getAllPersons_ShouldReturnListOfPersonResponses() {
		// Arrange
		Person person1 = new Person();
		person1.setId(1L);
		person1.setNome("João Silva");
		person1.setCpf("123.456.789-00");
		person1.setEmail("joao@example.com");

		Person person2 = new Person();
		person2.setId(2L);
		person2.setNome("Maria Souza");
		person2.setCpf("987.654.321-00");
		person2.setEmail("maria@example.com");

		when(personRepository.findAll()).thenReturn(Arrays.asList(person1, person2));

		// Act
		List<PersonResponse> result = retriveAllPerson.getAllPersons();

		// Assert
		assertEquals(2, result.size());

		PersonResponse response1 = result.get(0);
		assertEquals(person1.getId(), response1.id());
		assertEquals(person1.getNome(), response1.nome());
		assertEquals(person1.getCpf(), response1.cpf());
		assertEquals(person1.getEmail(), response1.email());

		PersonResponse response2 = result.get(1);
		assertEquals(person2.getId(), response2.id());
		assertEquals(person2.getNome(), response2.nome());
		assertEquals(person2.getCpf(), response2.cpf());
		assertEquals(person2.getEmail(), response2.email());

		verify(personRepository, times(1)).findAll();
	}

	@Test
	void getAllPersons_ShouldMapAllFieldsCorrectly() {
		// Arrange
		Person person = new Person();
		person.setId(3L);
		person.setNome("Carlos Oliveira");
		person.setCpf("456.789.123-00");
		person.setEmail("carlos@example.com");

		when(personRepository.findAll()).thenReturn(List.of(person));

		// Act
		List<PersonResponse> result = retriveAllPerson.getAllPersons();

		// Assert
		assertEquals(1, result.size());
		PersonResponse response = result.get(0);

		assertAll(() -> assertEquals(person.getId(), response.id()),
				() -> assertEquals(person.getNome(), response.nome()),
				() -> assertEquals(person.getCpf(), response.cpf()),
				() -> assertEquals(person.getEmail(), response.email()));
	}

	@Test
	void getAllPersons_ShouldReturnCorrectNumberOfItems() {
		// Arrange
		List<Person> persons = Arrays.asList(new Person(), new Person(), new Person() // 3
																						// pessoas
																						// genéricas
		);
		when(personRepository.findAll()).thenReturn(persons);

		// Act
		List<PersonResponse> result = retriveAllPerson.getAllPersons();

		// Assert
		assertEquals(3, result.size());
	}

}