package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayOne {

  public void run() {
    try {
      log.info("=== Day 1 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day1.txt"));
      log.info("=== Day 1 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 1 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    Integer maxElfCalories = null;

    int currentElfCalories = 0;
    for (String line : lines) {
      if (!line.isBlank()) {
        currentElfCalories += Integer.parseInt(line);
      } else {
        if (maxElfCalories == null || currentElfCalories > maxElfCalories) {
          maxElfCalories = currentElfCalories;
        }
        currentElfCalories = 0;
      }
    }
    log.info("The elf with the most calories carries %d".formatted(maxElfCalories));
  }

  private void dayOneSecondStar(List<String> lines) {
    List<Integer> elfCalories = new ArrayList<>();

    int currentElfCalories = 0;
    for (String line : lines) {
      if (!line.isBlank()) {
        currentElfCalories += Integer.parseInt(line);
      } else {
        elfCalories.add(currentElfCalories);
        currentElfCalories = 0;
      }
    }

    elfCalories.sort(Comparator.reverseOrder());
    log.info("The sum of the top three calories carried by elves is: %d".formatted(
        elfCalories.get(0) + elfCalories.get(1) + elfCalories.get(2)));
  }
}
