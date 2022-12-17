package de.startat.aoc2022.days;

import static org.apache.commons.lang3.Range.between;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.function.TriFunction;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayFifteen implements RunnableDay {

  public static final Pattern INPUT_LINE_PATTERN = Pattern.compile(
      "Sensor at x=([-\\d]+), y=([-\\d]+): closest beacon is at x=([-\\d]+), y=([-\\d]+)");

  /** just kept to keep the slow solution for the first star runnable*/
  private static final TriFunction<Set<Sensor>,Long,Long,Boolean> IN_RANGE = (sm,c,l) -> sm.parallelStream().anyMatch(s -> s.inRange(c, l));
  /** just kept to keep the slow solution for the first star runnable*/
  private static final TriFunction<Set<Sensor>,Long,Long,Boolean> IS_BEACON = (sm,c,l) -> sm.parallelStream().anyMatch(s -> s.isBeacon(c, l));

  public void run() {
    try {
      log.info("=== Day 15 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day15.txt"));
      log.info("=== Day 15 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 15 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    final Set<Sensor> tm = inputToSensors(lines);

    long lineToCheck = 2000000;

    long coveredFields =
        // Get all covered ranges by sensors
        tm.parallelStream().map(s -> s.sensorRangeOnLine(lineToCheck))
        .filter(Objects::nonNull)
        // Count unique columns in these ranges
        .flatMap(r -> LongStream.rangeClosed(r.getMinimum(), r.getMaximum()).boxed()
        ).distinct().count();

    //We need to see if a known beacon is on the line, as they don't count as covered fields
    long beaconsOnLine = tm.parallelStream().filter(s -> s.getClosestBeaconLine() == lineToCheck).map(
            Sensor::getClosestBeaconColumn).distinct()
        .count();

    log.info("There are " + beaconsOnLine + " beacons and " + (coveredFields-beaconsOnLine) + " covered fields in line " + lineToCheck );

    //old and slower solution
    /*
    long biggestDistance  = tm.parallelStream().mapToLong(Sensor::getDistance).max().orElseThrow();
    long leftmost = tm.parallelStream().mapToLong(Sensor::getColumn).min().orElseThrow();
    long rightmost = tm.parallelStream().mapToLong(Sensor::getColumn).max().orElseThrow();

    log.info("from " + leftmost + " to " + rightmost);
    log.info("biggest Sensor distance: " + biggestDistance );

    long countOfCoveredFieldsInLine = LongStream.range(leftmost-(biggestDistance+10),rightmost+biggestDistance+10).parallel()
        .map(i -> IN_RANGE.apply(tm,i,lineToCheck) && !IS_BEACON.apply(tm,i,lineToCheck)? 1L : 0L)
        .sum();
    log.info(countOfCoveredFieldsInLine + "");
    */
  }

  private void dayOneSecondStar(List<String> lines) {
    final Set<Sensor> tm = inputToSensors(lines);

    //Just a try to reduce our search area a bit further
    long northLine = tm.parallelStream().mapToLong(Sensor::getLine).min().orElseThrow();
    long southLine = tm.parallelStream().mapToLong(Sensor::getLine).max().orElseThrow();
    log.info("northLine " + northLine + ", southLine " + southLine);
    if(northLine < 0)
      northLine = 0;
    if(southLine > 4000000)
      southLine = 4000000;
    log.info("cropped northLine " + northLine + ", cropped southLine " + southLine);

    final Range<Long> interestingRange = Range.between(0L, 4000000L);


    LongStream.rangeClosed(northLine,southLine).parallel().forEach(line -> {
      // Determine the covered column ranges for the current line
      Set<Range<Long>> ranges = tm.parallelStream().map(s -> s.sensorRangeOnLine(line))
          .filter(Objects::nonNull)
          .filter(r -> r.isOverlappedBy(interestingRange))
          .collect(Collectors.toSet());

      // Early exit if we find a line where no sensor is covering anything
      if(!ranges.isEmpty()) {
        Set<Long> singleColumns =
            // Get all columns that are one right of a range in this line
            ranges.parallelStream().map(r -> r.getMaximum() + 1)
                // We're only interested in columns in that range
            .filter(r -> r >= 0 && r <= 4000000)
            .filter(
                column ->
                    // The field we're looking for has to be to the right of a range
                    ranges.parallelStream().noneMatch(r -> r.contains(column)) &&
                    // The field right to the one we're looking for has to be part one range
                    ranges.parallelStream().anyMatch(r -> r.contains(column + 1)))
            .collect(Collectors.toSet());

        singleColumns.forEach(column -> {
          // We already know that we have a single uncovered field that is surrounded left and right
          // by covered fields. Let's check up and down, to be sure.
          if (
              tm.parallelStream().anyMatch(s -> s.inRange(column, line - 1)) &&
                  tm.parallelStream().anyMatch(s -> s.inRange(column, line + 1))) {
            log.info(column + " " + line);
          }
        });
      }
    });
  }

  private static Set<Sensor> inputToSensors(List<String> lines) {
    Set<Sensor> sensors = new HashSet<>();
    lines.parallelStream().forEach(l -> {
          Matcher matcher = INPUT_LINE_PATTERN.matcher(l);
          if(matcher.matches()) {
            long sColumn = Long.parseLong(matcher.group(1));
            long sLine = Long.parseLong(matcher.group(2));
            long bColumn = Long.parseLong(matcher.group(3));
            long bLine = Long.parseLong(matcher.group(4));
            sensors.add(new Sensor(sColumn,sLine,bColumn, bLine, calcDistance(sColumn,sLine,bColumn,bLine)));
          } else {
            log.info("Could not RegEx line:" +l);
          }
          });
    return sensors;
  }

  private static long calcDistance(long col1, long line1, long col2, long line2) {
    return Math.abs(col1 - col2) + Math.abs(line1 - line2);
  }

  @AllArgsConstructor
  @Getter
  @EqualsAndHashCode
  @ToString
  public static class Sensor{
    Long column;
    Long line;
    Long closestBeaconColumn;
    Long closestBeaconLine;
    Long distance;

    public boolean inRange(long column, long line){
      long dist = calcDistance(this.column, this.line, column,line);
      return dist > 0 && this.distance >= dist;
    }

    public boolean isBeacon(long column, long line){
      return this.closestBeaconColumn.equals(column) && this.closestBeaconLine.equals(line);
    }

    public Range<Long> sensorRangeOnLine(long line){
      // The range covered gets shorter by 1 each line apart from the sensors line itself
      long delta = distance-Math.abs(this.line-line);
      if(delta<0){
        //this sensor is not capable of covering anything on the line
        return null;
      }
      return between(this.column-delta,this.column+delta);
    }

  }
}