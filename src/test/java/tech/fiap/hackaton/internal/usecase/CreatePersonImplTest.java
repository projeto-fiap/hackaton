package tech.fiap.hackaton.internal.usecase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePersonImplTest {

	@Mock
	private PersonRepository personRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private CreatePersonImpl createPerson;

	private PersonDTO personDTO;

	private Person person;

	private Person savedPerson;

	@BeforeEach
	void setUp() {
		// Configuração do DTO de entrada
		personDTO = new PersonDTO();
		personDTO.setNome("João Silva");
		personDTO.setCpf("123.456.789-00");
		personDTO.setSenha("senha123");
		personDTO.setEmail("joao@example.com");

		// Configuração da entidade Person
		person = new Person();
		person.setNome(personDTO.getNome());
		person.setCpf(personDTO.getCpf());
		person.setSenha("senhaCriptografada");
		person.setEmail(personDTO.getEmail());

		// Configuração da entidade salva (com ID)
		savedPerson = new Person();
		savedPerson.setId(1L);
		savedPerson.setNome(personDTO.getNome());
		savedPerson.setCpf(personDTO.getCpf());
		savedPerson.setSenha("senhaCriptografada");
		savedPerson.setEmail(personDTO.getEmail());
	}

	@Test
	void createPerson_ShouldEncodePasswordAndSavePerson() {
		// Arrange
		when(passwordEncoder.encode(personDTO.getSenha())).thenReturn("senhaCriptografada");
		when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

		// Act
		PersonResponse result = createPerson.createPerson(personDTO);

		// Assert
		assertNotNull(result);
		assertEquals(savedPerson.getId(), result.id());
		assertEquals(savedPerson.getNome(), result.nome());
		assertEquals(savedPerson.getCpf(), result.cpf());
		assertEquals(savedPerson.getEmail(), result.email());

		// Verifica se a senha foi criptografada
		verify(passwordEncoder, times(1)).encode(personDTO.getSenha());

		// Verifica se a pessoa foi salva no repositório
		verify(personRepository, times(1)).save(any(Person.class));
	}

	@Test
	void createPerson_ShouldMapAllFieldsCorrectly() {
		// Arrange
		when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
		when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

		// Act
		PersonResponse result = createPerson.createPerson(personDTO);

		// Assert
		assertAll(() -> assertEquals(savedPerson.getId(), result.id()),
				() -> assertEquals(personDTO.getNome(), result.nome()),
				() -> assertEquals(personDTO.getCpf(), result.cpf()),
				() -> assertEquals(personDTO.getEmail(), result.email()));
	}

	@Test
	void createPerson_ShouldReturnPersonResponseWithCorrectData() {
		// Arrange
		when(passwordEncoder.encode(anyString())).thenReturn("senhaCriptografada");
		when(personRepository.save(any(Person.class))).thenReturn(savedPerson);

		// Act
		PersonResponse result = createPerson.createPerson(personDTO);

		// Assert
		PersonResponse expectedResponse = new PersonResponse(1L, "João Silva", "123.456.789-00", "joao@example.com");

		assertEquals(expectedResponse, result);
	}

}