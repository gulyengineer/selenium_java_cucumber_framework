package step_definitions;

import io.cucumber.java8.En;

import static org.openqa.selenium.By.xpath;
import static utils.PageUtil.click;

public class ActionSteps implements En {

    public ActionSteps() {

        When("I submit the form", () -> {
            click(xpath("//a[text()='Submit']"));
        });

    }
}
