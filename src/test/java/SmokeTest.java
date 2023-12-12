import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        tags = "@smoke and not @ignore and not @prod",
        features = "classpath:features",
        plugin = {"pretty",
                "html:target/cucumber-reports/cucumber.html",
                "html:test-reports/index.html",
                "json:target/cucumber-reports/cucumber.json"
        })
public class SmokeTest {
}