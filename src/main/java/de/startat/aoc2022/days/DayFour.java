package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayFour {

  public static final Function<String, List<Elf>> TO_ELVE_PAIRS = s -> Arrays.stream(s.split(","))
      .map(Elf::new).collect(
          Collectors.toList());

  public void run() {
    try {
      log.info("=== Day 4 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day4.txt"));
      log.info("=== Day 4 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 4 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    long overlappingElveRanges = getCountWithFilter(lines, DayFour::fullyContainsEachOther);
    log.info("The number of fully overlapping ranges of elves are " + overlappingElveRanges);
  }

  private void dayOneSecondStar(List<String> lines) {
    long overlappingElveRanges = getCountWithFilter(lines, DayFour::overlapsEachOther);
    log.info("The number of overlapping ranges of elves are " + overlappingElveRanges);
  }

  private static long getCountWithFilter(List<String> lines, Function<List<Elf>,Boolean> filterFunc) {
    return lines.stream()
        .map(TO_ELVE_PAIRS)
        .map(filterFunc)
        .filter(e -> e).count();
  }

  private static class Elf{
    public Elf(String rangeString){
      String[] rangeLimits = rangeString.split("-");
      startRange = Integer.parseInt(rangeLimits[0]);
      endRange = Integer.parseInt(rangeLimits[1]);
    }
    int startRange;
    int endRange;
  }

  public static boolean fullyContainsEachOther(List<Elf> elfPair) {
    Elf e1 = elfPair.get(0);
    Elf e2 = elfPair.get(1);
    return (e1.startRange >= e2.startRange && e1.endRange <= e2.endRange) || (
        e2.startRange >= e1.startRange && e2.endRange <= e1.endRange);
  }

  public static boolean overlapsEachOther(List<Elf> elfPair) {
    Elf e1 = elfPair.get(0);
    Elf e2 = elfPair.get(1);
    return
        (e1.endRange >= e2.startRange && e1.endRange <= e2.endRange) ||
            (e2.endRange >= e1.startRange && e2.endRange <= e1.endRange) ||
            (e1.startRange >= e2.startRange && e1.startRange <= e2.endRange) ||
            (e2.startRange >= e1.startRange && e2.startRange <= e1.endRange);
  }
}
