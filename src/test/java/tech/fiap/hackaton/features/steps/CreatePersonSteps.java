package tech.fiap.hackaton.features.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.fiap.hackaton.api.model.PersonResponse;
import tech.fiap.hackaton.api.usecase.CreatePerson;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CreatePersonSteps {

	@Autowired
	private CreatePerson createPerson;

	@Autowired
	private PersonRepository personRepository;

	private PersonDTO personDTO;

	private PersonResponse personResponse;

	private Exception exception;

	@Given("Um novo cadastro de pessoa com dados válidos")
	public void um_novo_cadastro_de_pessoa_com_dados_válidos() {
		personDTO = new PersonDTO();
		personDTO.setNome("Maria Silva");
		personDTO.setCpf("12345678901");
		personDTO.setSenha("senha123");
		personDTO.setEmail("maria@example.com");
	}

	@Given("Um cadastro de pessoa com CPF já existente")
	public void um_cadastro_de_pessoa_com_cpf_já_existente() {
		Person existingPerson = new Person();
		existingPerson.setNome("João Existente");
		existingPerson.setCpf("98765432100");
		existingPerson.setSenha("senhaExistente");
		existingPerson.setEmail("joao@existente.com");
		personRepository.save(existingPerson);

		personDTO = new PersonDTO();
		personDTO.setNome("Outra Pessoa");
		personDTO.setCpf("98765432100");
		personDTO.setSenha("outraSenha");
		personDTO.setEmail("outro@email.com");
	}

	@Given("Um cadastro de pessoa com Email já existente")
	public void um_cadastro_de_pessoa_com_email_já_existente() {
		Person existingPerson = new Person();
		existingPerson.setNome("Maria Existente");
		existingPerson.setCpf("11122233344");
		existingPerson.setSenha("senhaExistente");
		existingPerson.setEmail("existente@email.com");
		personRepository.save(existingPerson);

		personDTO = new PersonDTO();
		personDTO.setNome("Nova Pessoa");
		personDTO.setCpf("99988877766");
		personDTO.setSenha("novaSenha");
		personDTO.setEmail("existente@email.com");
	}

	@When("Eu solicito a criação desta pessoa")
	public void eu_solicito_a_criação_desta_pessoa() {
		try {
			personResponse = createPerson.createPerson(personDTO);
		}
		catch (Exception e) {
			exception = e;
		}
	}

	@Then("A pessoa deve ser criada com sucesso")
	public void a_pessoa_deve_ser_criada_com_sucesso() {
		assertNotNull(personResponse, "A resposta não deve ser nula");
		assertNotNull(personResponse.id(), "O ID da pessoa não deve ser nulo");
		assertEquals(personDTO.getNome(), personResponse.nome(), "O nome não corresponde");
		assertEquals(personDTO.getCpf(), personResponse.cpf(), "O CPF não corresponde");
		assertEquals(personDTO.getEmail(), personResponse.email(), "O email não corresponde");
	}

	@Then("A senha deve ser criptografada")
	public void a_senha_deve_ser_criptografada() {
		Person savedPerson = personRepository.findById(personResponse.id()).orElse(null);
		assertNotNull(savedPerson, "Pessoa não encontrada no banco de dados");
		assertNotEquals(personDTO.getSenha(), savedPerson.getSenha(), "A senha não foi criptografada");
		assertTrue(savedPerson.getSenha().length() > 20, "A senha não parece estar criptografada");
	}

	@Then("Deve retornar um erro informando que o CPF já existe")
	public void deve_retornar_um_erro_informando_que_o_cpf_já_existe() {
		assertNotNull(exception, "Deveria ter lançado uma exceção");
		assertTrue(
				exception.getMessage().contains("CPF") || exception.getMessage().contains("cpf")
						|| exception.getMessage().contains("duplicado"),
				"Mensagem de erro deve mencionar CPF. Mensagem recebida: " + exception.getMessage());
	}

	@Then("Deve retornar um erro informando que o Email já existe")
	public void deve_retornar_um_erro_informando_que_o_email_já_existe() {
		assertNotNull(exception, "Deveria ter lançado uma exceção");
		assertTrue(
				exception.getMessage().contains("Email") || exception.getMessage().contains("email")
						|| exception.getMessage().contains("duplicado"),
				"Mensagem de erro deve mencionar Email. Mensagem recebida: " + exception.getMessage());
	}

}