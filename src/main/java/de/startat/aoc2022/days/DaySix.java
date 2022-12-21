package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DaySix implements RunnableDay{

  public static final Function<String, List<String>> TO_LIST_OF_CHARS = l -> List.of(
      l.split(""));

  public void run() {
    try {
      log.info("=== Day 6 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day6.txt"));
      log.info("=== Day 6 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 6 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    log.info(lines.stream().map(TO_LIST_OF_CHARS)
        .map(l -> findMarker(l,4))
        .toList().toString());
  }

  private int findMarker(List<String> first, int markerLength) {
    Queue<String> buffer = new LinkedList<>();
    for (int i = 0; i < first.size(); i++) {
      String s = first.get(i);
      buffer.offer(s);
      if (buffer.size() > markerLength) {
        buffer.remove();
      }
      if (isMarker(markerLength, buffer)) {
        return i + 1;
      }
    }
    throw new RuntimeException();
  }

  private static boolean isMarker(int markerLength, Queue<String> buffer) {
    return new HashSet<>(buffer).size() == markerLength;
  }

  private void dayOneSecondStar(List<String> lines) {
    log.info(lines.stream().map(l -> List.of(l.split("")))
        .map(l -> findMarker(l,14))
        .toList().toString());
  }
}
