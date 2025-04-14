package tech.fiap.hackaton.features;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.fiap.hackaton.HackatonApplication;

@CucumberContextConfiguration
@SpringBootTest(classes = HackatonApplication.class)
@ActiveProfiles("test")
public class CucumberSpringConfiguration {

}
