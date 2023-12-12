package step_definitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;

import java.util.Map;

import static org.openqa.selenium.By.id;
import static org.openqa.selenium.By.xpath;
import static utils.PageUtil.*;
import static utils.SeleniumConfig.driver;

public class NavigationSteps implements En {
    public NavigationSteps() {

        When("I am on the {string} page", (String url) -> {
            driver().manage().deleteAllCookies();
            driver().get(url);

        });

        When("I fill out the form with the following details:", (DataTable dataTable) -> {
            Map<String, String> regDetail = dataTable.asMaps().get(0);
            fillForm(regDetail.get("First Name"), regDetail.get("Last Name"), regDetail.get("Job"), regDetail.get("Experience"), regDetail.get("Date"));
        });
    }

    private void fillForm(String first_name, String last_name, String job_title,  String experience, String date) {
        typeInto(id("first-name"), first_name);
        typeInto(id("last-name"), last_name);
        typeInto(id("job-title"), job_title);
        click(xpath("//input[@id='radio-button-2']"));
        click(xpath("//input[@id='checkbox-1']"));
        selectFromDropdown(id("select-menu"), experience);
        typeInto(id("datepicker"), date);
    }
}

