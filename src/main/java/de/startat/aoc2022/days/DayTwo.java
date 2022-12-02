package de.startat.aoc2022.days;

import static de.startat.aoc2022.days.DayTwo.OUTCOME.DRAW;
import static de.startat.aoc2022.days.DayTwo.OUTCOME.LOST;
import static de.startat.aoc2022.days.DayTwo.OUTCOME.WIN;
import static de.startat.aoc2022.days.DayTwo.RPS.PAPER;
import static de.startat.aoc2022.days.DayTwo.RPS.ROCK;
import static de.startat.aoc2022.days.DayTwo.RPS.SCISSORS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.ToIntFunction;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayTwo {

  public static final ToIntFunction<Strategy> CALCULATE_STRATEGY_POINTS = s ->
      s.getMyChoice().getPoints() + s.getOutcome().getPoints();

  public void run() {
    try {
      log.info("=== Day 2 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day2.txt"));
      log.info("=== Day 2 - First star ===");
      firstStar(lines);
      log.info("=== Day 2 - Second star ===");
      secondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void firstStar(List<String> lines) {
    List<Strategy> strategies = lines.stream().map(l -> {
      String[] choiceStrings = l.split(" ");
      return new Strategy(RPS.convertTo(choiceStrings[0]), RPS.convertTo(choiceStrings[1]));
    }).toList();
    long sumOfPointsForStrategy = strategies.stream().mapToInt(CALCULATE_STRATEGY_POINTS).sum();
    log.info("If the second column means the hand I should use to counter, the sum of points is " +sumOfPointsForStrategy);
  }

  private void secondStar(List<String> lines) {
    List<Strategy> strategies = lines.stream().map(l -> {
      String[] choiceStrings = l.split(" ");
      return new Strategy(RPS.convertTo(choiceStrings[0]), OUTCOME.convertTo(choiceStrings[1]));
    }).toList();
    long sumOfPointsForStrategy = strategies.stream().mapToInt(CALCULATE_STRATEGY_POINTS).sum();
    log.info("If the second column means the desired outcome a round should have, the sum of points is " + sumOfPointsForStrategy);
  }

  @Data
  private static class Strategy {

    private RPS myChoice;
    private RPS enemyChoice;

    private OUTCOME outcome;

    Strategy(RPS enemyChoice, RPS myChoice) {
      this.enemyChoice = enemyChoice;
      this.myChoice = myChoice;
      determineOutcome();
    }

    Strategy(RPS enemyChoice, OUTCOME outcome) {
      this.enemyChoice = enemyChoice;
      this.outcome = outcome;
      determineMyChoice();
    }

    private void determineMyChoice() {
      myChoice = switch (outcome) {
        case DRAW -> enemyChoice;
        case WIN -> switch (enemyChoice) {
          case ROCK -> PAPER;
          case PAPER -> SCISSORS;
          case SCISSORS -> ROCK;
        };
        case LOST -> switch (enemyChoice) {
          case ROCK -> SCISSORS;
          case PAPER -> ROCK;
          case SCISSORS -> PAPER;
        };
      };
    }

    public String toString() {
      return myChoice.name() + "(" + myChoice.getPoints() + ") vs " + enemyChoice.name() + " -> "
          + outcome.name() + "(" + outcome.getPoints() + ")";
    }

    private void determineOutcome() {
      if (myChoice == ROCK && enemyChoice == PAPER || myChoice == PAPER && enemyChoice == SCISSORS
          || myChoice == SCISSORS && enemyChoice == ROCK) {
        outcome = LOST;
      } else if (myChoice == enemyChoice) {
        outcome = DRAW;
      } else {
        outcome = WIN;
      }
    }
  }

  @Getter
  enum RPS {
    ROCK(1), PAPER(2), SCISSORS(3);

    private final int points;

    RPS(int points) {
      this.points = points;
    }

    public static RPS convertTo(String choiceString) {
      switch (choiceString) {
        case "A":
        case "X":
          return ROCK;
        case "B":
        case "Y":
          return PAPER;
        case "C":
        case "Z":
          return SCISSORS;
      }
      throw new RuntimeException(choiceString + " is not a mappable choice");
    }
  }

  @Getter
  enum OUTCOME {
    LOST(0), DRAW(3), WIN(6);

    private final int points;

    OUTCOME(int points) {
      this.points = points;
    }

    public static OUTCOME convertTo(String choiceString) {
      switch (choiceString) {
        case "X":
          return LOST;
        case "Y":
          return DRAW;
        case "Z":
          return WIN;
      }
      throw new RuntimeException(choiceString + " is not a mappable outcome");
    }
  }
}
