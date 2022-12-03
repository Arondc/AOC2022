package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayThree {
  public static final Function<String, List<String>> SPLIT_IN_THE_MIDDLE = s -> List.of(
      s.substring(0, s.length() / 2), s.substring(s.length() / 2));
  public static final Function<List<String>, List<Set<String>>> TO_SETS_OF_SINGLE_ITEMS = sl -> sl.stream()
      .map(DayThree::stringToSet).collect(Collectors.toList());

  public void run() {
    try {
      log.info("=== Day 3 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day3.txt"));
      log.info("=== Day 3 - First star ===");
      firstStar(lines);
      log.info("=== Day 3 - Second star ===");
      secondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void firstStar(final List<String> lines) {
    long sumOfPriorities = lines.stream().map(SPLIT_IN_THE_MIDDLE)
        .map(TO_SETS_OF_SINGLE_ITEMS).map(DayThree::findCommonItem)
        .mapToInt(DayThree::priorityScore).sum();
    log.info(
        "The sum of all priorities of items that are in both compartments is " + sumOfPriorities);
  }

  private void secondStar(List<String> lines) {
    Collection<List<String>> groupsOfThreeElves = divideInGroupOfThrees(lines);
    long sumOfPriorities = groupsOfThreeElves.stream().map(TO_SETS_OF_SINGLE_ITEMS)
        .map(DayThree::findCommonItem).mapToInt(DayThree::priorityScore).sum();
    log.info("The sum of all priorities of items that are common in groups of three is "
        + sumOfPriorities);
  }

  private static Collection<List<String>> divideInGroupOfThrees(List<String> lines) {
    final AtomicInteger counter = new AtomicInteger();
    return lines.stream()
        .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / 3)).values();
  }

  private static Set<String> stringToSet(String string) {
    return Arrays.stream(string.split("")).collect(Collectors.toSet());
  }

  private static String findCommonItem(List<Set<String>> inventories) {
    for (Set<String> inventory : inventories) {
      inventories.get(0).retainAll(inventory);
    }
    return inventories.get(0).stream().findFirst().orElseThrow();
  }

  private static int priorityScore(String item) {
    int code = item.toCharArray()[0];
    int priority;
    if (code > 96) {
      //lowercase letters 1-26
      priority = code - 96;
    } else {
      //uppercase letters 27-52
      priority = code - 64 + 26;
    }
    log.debug(item + " has priority " + priority);
    return priority;
  }
}
