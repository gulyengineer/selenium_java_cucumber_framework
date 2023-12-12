@solo
Feature: Fill out and submit a form

  As an
  I want  To

  Scenario Outline: Fill the form and submit
    Given I am on the "https://formy-project.herokuapp.com/form" page
    And I set current time to CurrentDateTime
    And I fill out the form with the following details:
      | First Name   | Last Name   | Job   | Education   | Sex   | Experience   | Date                |
      | <First name> | <Last Name> | <Job> | <Education> | <Sex> | <Experience> | <<CurrentDateTime>> |
    And I wait for 10 seconds
    And I submit the form
    Then I am on the Thanks page
    Examples:
      | First name | Last Name | Job     | Education   | Sex  | Experience |
      | Testing    | Test      | QA Test | Grad School | Male | 0-1        |
