package stepdefinitions;

import io.cucumber.java8.En;

import static org.junit.Assert.assertEquals;
import static utils.SeleniumConfig.driver;

public class ValidationSteps implements En {

    public ValidationSteps() {

        When("I am on the Thanks page", () -> {
            String actualPageUrl = driver().getCurrentUrl();
            assertEquals("https://formy-project.herokuapp.com/thanks", actualPageUrl);
        });

    }
}
