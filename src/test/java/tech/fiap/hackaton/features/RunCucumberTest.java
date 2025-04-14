package tech.fiap.hackaton.features;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import java.net.URL;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/java/resources/features/person.feature", // Mantendo
																				// seu
																				// caminho
																				// customizado
		glue = { "tech.fiap.hackaton.features.steps", "tech.fiap.hackaton.features" // Incluindo
																					// o
																					// pacote
																					// da
																					// configuração
		}, plugin = { "pretty", "html:target/cucumber-reports/cucumber.html",
				"json:target/cucumber-reports/cucumber.json" })
public class RunCucumberTest {

}