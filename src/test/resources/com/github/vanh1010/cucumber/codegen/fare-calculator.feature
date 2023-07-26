Feature: Fare calculator

  Example: Taxi fare from 8 a.m. to 10 a.m.
    Given the fare rate at 8 a.m. till 10 a.m. is $10/km
    When I book a taxi from 9 a.m.
    And the taxi travels 10 killometers
    Then the total cost should be $100
