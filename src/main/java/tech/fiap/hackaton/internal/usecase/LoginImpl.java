package tech.fiap.hackaton.internal.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tech.fiap.hackaton.api.usecase.Login;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Map;
import java.util.Optional;

@Service
public class LoginImpl implements Login {

	private final PersonRepository personRepository;

	@Value("${hackaton.keycloak-realm}")
	private String realm;

	@Value("${hackaton.keycloak-url}")
	private String baseUrl;

	@Value("${hackaton.keycloak-clientSecret}")
	private String clientSecret;

	public LoginImpl(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	@Override
	public String login(String email, String senha) {

		Optional<Person> optionalPerson = personRepository.findByEmail(email);

		if (optionalPerson.isPresent()) {
			Person person = optionalPerson.get();
			return getUserToken(person.getEmail(), senha);
		}

		throw new RuntimeException("Credenciais inválidas");
	}

	public String getUserToken(String username, String password) {
		String url = String.format("%s/realms/%s/protocol/openid-connect/token", baseUrl, realm);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("client_id", "hackaton");
		body.add("client_secret", clientSecret);
		body.add("grant_type", "password");
		body.add("username", username);
		body.add("password", password);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

		ResponseEntity<Map> response = new RestTemplate().postForEntity(url, request, Map.class);

		Map<String, Object> responseBody = response.getBody();
		if (responseBody == null || !responseBody.containsKey("access_token")) {
			throw new RuntimeException("Token de acesso não recebido do Keycloak.");
		}

		return (String) responseBody.get("access_token");
	}


}
