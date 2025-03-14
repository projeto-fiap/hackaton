package tech.fiap.hackaton.internal.usecase;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.LoginUseCase;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class LoginUseCaseImpl implements LoginUseCase {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final String SECRET_KEY = "suaChaveSecretaMuitoSeguraEComplexa1234567890";

    public LoginUseCaseImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(String email, String senha) {
        // Busca a pessoa pelo email
        Optional<Person> optionalPerson = personRepository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();

            // Verifica se a senha corresponde
            if (passwordEncoder.matches(senha, person.getSenha())) {
                // Gera o token JWT
                String token = Jwts.builder()
                        .setSubject(person.getEmail())
                        .claim("roles", "USER") // Ajuste para as roles conforme necessário
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 dia de validade
                        .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                        .compact();

                return token; // Retorna o token gerado
            }
        }

        throw new RuntimeException("Credenciais inválidas"); // Retorna um erro caso as credenciais sejam inválidas
    }
}
