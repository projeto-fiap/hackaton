package tech.fiap.hackaton.internal.usecase;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.fiap.hackaton.api.usecase.Login;
import tech.fiap.hackaton.internal.entity.Person;
import tech.fiap.hackaton.internal.repository.PersonRepository;

import java.util.Date;
import java.util.Optional;

@Service
public class LoginImpl implements Login {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final String SECRET_KEY = "suaChaveSecretaMuitoSeguraEComplexa1234567890";

    public LoginImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String login(String email, String senha) {

        Optional<Person> optionalPerson = personRepository.findByEmail(email);

        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();


            if (passwordEncoder.matches(senha, person.getSenha())) {

                String token = Jwts.builder()
                        .setSubject(person.getEmail())
                        .claim("roles", "USER")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1200000))
                        .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                        .compact();

                return token;
            }
        }

        throw new RuntimeException("Credenciais inválidas");
    }
}
