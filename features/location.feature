Feature: Location

  Scenario: As a user with a new location I should see city I'm in
    Given I set my location to 34.052235, -118.243683
    And I wait for 5 seconds
    Then I should see "You are in Los Angeles"

    When I set my location to 40.748817, -73.985428
    And I wait for 5 seconds
    Then I should see "You are in New York"