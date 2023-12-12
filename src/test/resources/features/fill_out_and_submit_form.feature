@solo
Feature: Fill out and submit a form

  As an
  I want  To

  Scenario Outline: Fill the form and submit
    Given I am on the "https://formy-project.herokuapp.com/form" page
    And I set current time to CurrentDateTime
    And I fill out the form with the following details:
      | First Name   | Last Name   | Job   | Education   | Sex   | Experience   |
      | <First name> | <Last Name> | <Job> | <Education> | <Sex> | <Experience> |
    And I submit the form
    Then I am on the Thanks page
    Examples:
      | First name | Last Name | Job     | Education   | Sex  | Experience |
      | Testing    | Test      | QA Test | Grad School | Male | 0-1        |
