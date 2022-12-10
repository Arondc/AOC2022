package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayNine {

  public void run() {
    try {
      log.info("=== Day 9 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day9.txt"));
      log.info("=== Day 9 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 9 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    final RopeEnd head = new RopeEnd(0, 0);
    final RopeEnd tail = new RopeEnd(0, 0);

    Set<String> visitedCoordinates = new HashSet<>();

    for(String l: lines){
      String[] s = l.split(" ");
      for(int i = 0; i < Integer.parseInt(s[1]); i++){
        switch (s[0]) {
          case "U" -> head.y += 1;
          case "D" -> head.y -= 1;
          case "L" -> head.x -= 1;
          case "R" -> head.x += 1;
        }
        tail.follow(head);
        log.info(head + " " + tail + " " + tail.touches(head));

        visitedCoordinates.add(tail.x + "-" + tail.y);
      }
    }

    log.info(visitedCoordinates.size() + "");

  }



  private void dayOneSecondStar(List<String> lines) {
    List<RopeEnd> ropeKnots = IntStream.range(0, 10).mapToObj(i -> new RopeEnd(0, 0)).toList();

    Set<String> visitedCoordinates = new HashSet<>();

    for(String l: lines){
      String[] s = l.split(" ");
      for(int i = 0; i < Integer.parseInt(s[1]); i++){
        switch (s[0]) {
          case "U" -> ropeKnots.get(0).y += 1;
          case "D" -> ropeKnots.get(0).y -= 1;
          case "L" -> ropeKnots.get(0).x -= 1;
          case "R" -> ropeKnots.get(0).x += 1;
        }

        RopeEnd prev = ropeKnots.get(0);
        for(int j = 1; j<ropeKnots.size(); j++){
          RopeEnd currentKnot = ropeKnots.get(j);
          currentKnot.follow(prev);
          prev = currentKnot;
        }

        visitedCoordinates.add(ropeKnots.get(9).x + "-" + ropeKnots.get(9).y);
      }
    }

    log.info(visitedCoordinates.size() + "");
  }

  @ToString
  @AllArgsConstructor
  private static class RopeEnd {

    int x;
    int y;

    public boolean touches(RopeEnd otherEnd) {
      return Math.abs(x - otherEnd.x) <= 1 && Math.abs(y - otherEnd.y) <= 1;
    }

    public void follow(RopeEnd head) {
      if (this.touches(head)) {
        return;
      }

      if (x != head.x && y != head.y) {
        if (x < head.x && y < head.y) {
          x += 1;
          y += 1;
        } else if (x < head.x) {
          x += 1;
          y -= 1;
        } else if (y > head.y) {
          x -= 1;
          y -= 1;
        } else {
          x -= 1;
          y += 1;
        }


      } else if (x == head.x && y != head.y) {
        y += (head.y - y) / 2;
      } else if (x != head.x) {
        x += (head.x - x) / 2;
      }
    }


  }

}
