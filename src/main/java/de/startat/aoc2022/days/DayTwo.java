package de.startat.aoc2022.days;

import static de.startat.aoc2022.days.DayTwo.Outcome.DRAW;
import static de.startat.aoc2022.days.DayTwo.Outcome.LOST;
import static de.startat.aoc2022.days.DayTwo.Outcome.WIN;
import static de.startat.aoc2022.days.DayTwo.Choice.PAPER;
import static de.startat.aoc2022.days.DayTwo.Choice.ROCK;
import static de.startat.aoc2022.days.DayTwo.Choice.SCISSORS;

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
      return new Strategy(Choice.convertTo(choiceStrings[0]), Choice.convertTo(choiceStrings[1]));
    }).toList();
    long sumOfPointsForStrategy = strategies.stream().mapToInt(CALCULATE_STRATEGY_POINTS).sum();
    log.info("If the second column means the hand I should use to counter, the sum of points is "
        + sumOfPointsForStrategy);
  }

  private void secondStar(List<String> lines) {
    List<Strategy> strategies = lines.stream().map(l -> {
      String[] choiceStrings = l.split(" ");
      return new Strategy(Choice.convertTo(choiceStrings[0]), Outcome.convertTo(choiceStrings[1]));
    }).toList();
    long sumOfPointsForStrategy = strategies.stream().mapToInt(CALCULATE_STRATEGY_POINTS).sum();
    log.info(
        "If the second column means the desired outcome a round should have, the sum of points is "
            + sumOfPointsForStrategy);
  }

  @Data
  private static class Strategy {

    private Choice myChoice;
    private Choice enemyChoice;

    private Outcome outcome;

    Strategy(Choice enemyChoice, Choice myChoice) {
      this.enemyChoice = enemyChoice;
      this.myChoice = myChoice;
      determineOutcome();
    }

    Strategy(Choice enemyChoice, Outcome outcome) {
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
  enum Choice {
    ROCK(1), PAPER(2), SCISSORS(3);

    private final int points;

    Choice(int points) {
      this.points = points;
    }

    public static Choice convertTo(String choiceString) {
      return switch (choiceString) {
        case "A", "X" -> ROCK;
        case "B", "Y" -> PAPER;
        case "C", "Z" -> SCISSORS;
        default -> throw new RuntimeException(choiceString + " is not a mappable choice");
      };
    }
  }

  @Getter
  enum Outcome {
    LOST(0), DRAW(3), WIN(6);

    private final int points;

    Outcome(int points) {
      this.points = points;
    }

    public static Outcome convertTo(String choiceString) {
      return switch (choiceString) {
        case "X" -> LOST;
        case "Y" -> DRAW;
        case "Z" -> WIN;
        default -> throw new RuntimeException(choiceString + " is not a mappable outcome");
      };
    }
  }
}
