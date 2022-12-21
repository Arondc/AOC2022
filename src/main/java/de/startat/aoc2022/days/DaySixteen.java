package de.startat.aoc2022.days;

import static org.apache.commons.lang3.Range.between;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DaySixteen implements RunnableDay {

  public static final Pattern INPUT_PATTERN = Pattern.compile(
      "Valve (\\w+) has flow rate=(\\d+); tunnels? leads? to valves? (.*)");

  public void run() {
    try {
      log.info("=== Day 16 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day16_test.txt"));
      log.info("=== Day 16 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 16 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    Map<String, Valve> valves = inputToValveMap(lines);
    valves.values().forEach(v -> log.info(v.toString()));


  }

  private static Map<String, Valve> inputToValveMap(List<String> lines) {
    Map<String, Valve> valves = new HashMap<>();
    lines.forEach(line -> {
      Matcher matcher = INPUT_PATTERN.matcher(line);
      if(matcher.matches()) {

        valves.put(matcher.group(1),
            new Valve(matcher.group(1), Integer.parseInt(matcher.group(2)),
                Arrays.stream(matcher.group(3).split(", ")).collect(
                    Collectors.toSet())));
      } else {
        log.error("Line unrecognizable: " + line);
      }
    });
    return valves;
  }

  private void dayOneSecondStar(List<String> lines) {

  }

  @RequiredArgsConstructor
  @Data
  private static class Valve{
    final String name;
    final int flowRate;
    final Set<String> tunnels;
  }

}