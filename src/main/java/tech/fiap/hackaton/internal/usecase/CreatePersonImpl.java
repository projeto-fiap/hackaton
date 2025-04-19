package tech.fiap.hackaton.internal.usecase;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tech.fiap.hackaton.api.model.PersonResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.CreatePerson;
import tech.fiap.hackaton.internal.dto.PersonDTO;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CreatePersonImpl implements CreatePerson {

	private final PersonRepository personRepository;

	private final PasswordEncoder passwordEncoder;


	@Value("${hackaton.keycloak-realm}")
	private String realm;

	@Value("${hackaton.keycloak-url}")
	private String baseUrl;

	public CreatePersonImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
		this.personRepository = personRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	@Transactional
	public PersonResponse createPerson(PersonDTO personDTO) {
		Person person = new Person();
		person.setNome(personDTO.getNome());
		person.setCpf(personDTO.getCpf());
		person.setSenha(passwordEncoder.encode(personDTO.getSenha())); // Criptografa a
																		// senha
		person.setEmail(personDTO.getEmail());

		createUserInKeycloak(personDTO);
		Person savedPerson = personRepository.save(person);
		return new PersonResponse(savedPerson.getId(), savedPerson.getNome(), savedPerson.getCpf(),
				savedPerson.getEmail());
	}

	public void createUserInKeycloak(PersonDTO personDTO) {
		String url = String.format("%s/admin/realms/%s/users", baseUrl, realm);
		String token = getAdminAccessToken();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);

		Map<String, Object> user = new HashMap<>();
		user.put("username", personDTO.getEmail());
		user.put("email", personDTO.getEmail());
		user.put("enabled", true);

		Map<String, String> credentials = new HashMap<>();
		credentials.put("type", "password");
		credentials.put("value", personDTO.getSenha());
		credentials.put("temporary", "false");

		user.put("credentials", List.of(credentials));

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);

		ResponseEntity<String> response = new RestTemplate().postForEntity(url, request, String.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			log.info("Usuário criado no Keycloak com sucesso.");
		}
		else {
			throw new RuntimeException("Falha ao criar usuário no Keycloak: " + response.getStatusCode());
		}
	}

	public String getAdminAccessToken() {
		String url = String.format("%s/realms/%s/protocol/openid-connect/token", baseUrl, realm);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", "admin-cli"); // ⚠️ ESSENCIAL!
		body.add("username", "user");
		body.add("password", "password"); // ou sua senha real
		body.add("grant_type", "password");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = new RestTemplate().postForEntity(url, request, Map.class);

		Map<String, Object> responseBody = response.getBody();
		if (responseBody == null || !responseBody.containsKey("access_token")) {
			throw new RuntimeException("Token de acesso não recebido do Keycloak.");
		}

		return (String) responseBody.get("access_token");
	}


}