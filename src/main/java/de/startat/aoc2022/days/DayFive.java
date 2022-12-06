package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayFive {

  public void run() {
    try {
      log.info("=== Day 5 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day5.txt"));
      log.info("=== Day 5 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 5 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    StackConfiguration sc = divideInput(lines);

    List<List<String>> crateLines = sc.startConfiguration.stream().map(this::reformatToCrateList)
        .collect(Collectors.toList());
    Map<Integer, Stack<String>> stacks = buildStartingStacks(crateLines);
    processOrders(sc, stacks, true);

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < stacks.size(); i++) {
      sb.append(stacks.get(i + 1).peek());
    }
    log.info("The top crates are: " + sb);
  }

  private void dayOneSecondStar(List<String> lines) {
    StackConfiguration sc = divideInput(lines);
    List<List<String>> crateLines = sc.startConfiguration.stream().map(this::reformatToCrateList)
        .collect(Collectors.toList());
    Map<Integer, Stack<String>> stacks = buildStartingStacks(crateLines);
    processOrders(sc, stacks, false);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < stacks.size(); i++) {
      sb.append(stacks.get(i + 1).peek());
    }
    log.info("The top crates are: " + sb);
  }

  private static void processOrders(StackConfiguration sc, Map<Integer, Stack<String>> stacks,
      boolean isCreateMover9000Crane) {
    for (String order : sc.rearrangementOrders) {
      Matcher m = Pattern.compile("move (\\d*) from (\\d*) to (\\d*)").matcher(order);
      if (m.find()) {
        int count = Integer.parseInt(m.group(1));
        int from = Integer.parseInt(m.group(2));
        int to = Integer.parseInt(m.group(3));

        if (isCreateMover9000Crane) {
          crateMover9000Processing(stacks, count, from, to);
        } else {
          crateMover9001Processing(stacks, count, from, to);
        }
      }
    }
  }

  private static void crateMover9000Processing(Map<Integer, Stack<String>> stacks, int count,
      int from, int to) {
    for (int i = 0; i < count; i++) {
      String crate = stacks.get(from).pop();
      stacks.get(to).push(crate);
    }
  }

  private static void crateMover9001Processing(Map<Integer, Stack<String>> stacks, int count,
      int from, int to) {
    Stack<String> craneArm = new Stack<>();
    for (int i = 0; i < count; i++) {
      craneArm.push(stacks.get(from).pop());
    }
    while (!craneArm.isEmpty()) {
      stacks.get(to).push(craneArm.pop());
    }
  }

  private static Map<Integer, Stack<String>> buildStartingStacks(List<List<String>> crateLines) {
    int countOfStacks = crateLines.stream().mapToInt(List::size).max().orElseThrow();
    Map<Integer, Stack<String>> stacks = new HashMap<>();
    for (int i = 1; i < countOfStacks + 1; i++) {
      stacks.put(i, new Stack<>());
    }

    for (int i = crateLines.size() - 1; i >= 0; i--) {
      List<String> currentLine = crateLines.get(i);
      for (int j = 0; j < currentLine.size(); j++) {
        if (currentLine.get(j) != null) {
          stacks.get(j + 1).push(currentLine.get(j));
        }
      }
    }
    return stacks;
  }

  private List<String> reformatToCrateList(String line) {
    Matcher m = Pattern.compile("([ \\[]( |\\w)[ \\]])\\W?+").matcher(line);
    List<String> tmp = new ArrayList<>();
    while (m.find()) {
      String find = m.group(2);
      tmp.add(find.isBlank() ? null : find);
    }
    return tmp;
  }


  record StackConfiguration(List<String> startConfiguration, List<String> rearrangementOrders) {

  }

  private static StackConfiguration divideInput(List<String> lines) {
    List<String> stackConfigurationLines = Collections.emptyList();
    List<String> rearrangementLines = Collections.emptyList();
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).isBlank()) {
        stackConfigurationLines = lines.subList(0, i - 1);
        rearrangementLines = lines.subList(i + 1, lines.size());
      }
    }
    return new StackConfiguration(stackConfigurationLines, rearrangementLines);
  }
}
