package tech.fiap.hackaton.integration.person;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.CreatePerson;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;
import tech.fiap.hackaton.internal.usecase.CreatePersonImpl;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CreatePersonImplIT {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private CreatePerson createPerson;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@TestConfiguration
	static class TestConfig {

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}

		@Bean
		public CreatePerson createPerson(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
			return new CreatePersonImpl(personRepository, passwordEncoder);
		}

	}

	@Test
	void createPerson_ShouldSavePersonWithEncryptedPassword() {
		// Arrange
		PersonDTO personDTO = new PersonDTO();
		personDTO.setNome("João Silva");
		personDTO.setCpf("123.456.789-00");
		personDTO.setSenha("senha123");
		personDTO.setEmail("joao@example.com");

		// Act
		PersonResponse result = createPerson.createPerson(personDTO);

		// Assert
		assertNotNull(result.id());
		assertEquals(personDTO.getNome(), result.nome());
		assertEquals(personDTO.getCpf(), result.cpf());
		assertEquals(personDTO.getEmail(), result.email());

		// Verifica se a pessoa foi persistida no banco
		Person savedPerson = personRepository.findById(result.id()).orElse(null);
		assertNotNull(savedPerson);
		assertEquals(personDTO.getNome(), savedPerson.getNome());
		assertEquals(personDTO.getCpf(), savedPerson.getCpf());
		assertEquals(personDTO.getEmail(), savedPerson.getEmail());

		// Verifica se a senha foi criptografada
		assertTrue(passwordEncoder.matches(personDTO.getSenha(), savedPerson.getSenha()));
	}

	@Test
	void createPerson_ShouldReturnResponseWithCorrectData() {
		// Arrange
		PersonDTO personDTO = new PersonDTO();
		personDTO.setNome("Maria Souza");
		personDTO.setCpf("987.654.321-00");
		personDTO.setSenha("outrasenha");
		personDTO.setEmail("maria@example.com");

		// Act
		PersonResponse result = createPerson.createPerson(personDTO);

		// Assert
		assertAll(() -> assertNotNull(result.id()), () -> assertEquals(personDTO.getNome(), result.nome()),
				() -> assertEquals(personDTO.getCpf(), result.cpf()),
				() -> assertEquals(personDTO.getEmail(), result.email()));
	}

}
