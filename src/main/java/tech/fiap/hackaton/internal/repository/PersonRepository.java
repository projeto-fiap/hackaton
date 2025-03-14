package tech.fiap.hackaton.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.fiap.hackaton.internal.entity.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);
}
