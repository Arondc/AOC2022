package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayFourteen implements RunnableDay {

  private Integer deepestRock;
  private Integer settledSandCounter = 0;

  public void run() {
    try {
      log.info("=== Day 14 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day14.txt"));
      log.info("=== Day 14 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 14 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    Set<Coordinates> rocks = getRocks(lines);
    deepestRock = rocks.stream().mapToInt(r -> r.depth).max().orElseThrow();

    log.info(rocks.toString());
    int rocksBefore;
    do {
      rocksBefore = rocks.size();
      simulateSand(rocks);
    } while (rocksBefore != rocks.size());
    log.info(settledSandCounter + "");
  }

  private void dayOneSecondStar(List<String> lines) {
    settledSandCounter = 0;
    Set<Coordinates> rocks = getRocks(lines);
    deepestRock = rocks.stream().mapToInt(r -> r.depth).max().orElseThrow();
    int lineLevel = deepestRock+2;
    deepestRock = lineLevel;
    rocks.addAll(buildCoordline(new Coordinates(-2000, lineLevel), new Coordinates(2000, lineLevel)));

    Coordinates sandStart = new Coordinates(500, 0);
    do {
      simulateSand(rocks);
    }while(!rocks.contains(sandStart));
    log.info(settledSandCounter + "");

  }

  private void simulateSand(Set<Coordinates> rocks) {
    Coordinates sand = new Coordinates( 500,0);
    Coordinates checker = new Coordinates(500,1);
    Coordinates checkerLeft = new Coordinates(499,1);
    Coordinates checkerRight = new Coordinates(501,1);

    boolean hasMoved;
    do {
      hasMoved = false;
      if (!rocks.contains(checker)) {
        sand.moveDown();
        checker.moveDown();
        checkerLeft.moveDown();
        checkerRight.moveDown();
        hasMoved = true;
      } else if(!rocks.contains(checkerLeft)){
        sand.moveDownLeft();
        checker.moveDownLeft();
        checkerLeft.moveDownLeft();
        checkerRight.moveDownLeft();
        hasMoved = true;
      } else if(!rocks.contains(checkerRight)){
        sand.moveDownRight();
        checker.moveDownRight();
        checkerLeft.moveDownRight();
        checkerRight.moveDownRight();
        hasMoved = true;
      }
      if(!hasMoved){
        rocks.add(sand);
        settledSandCounter++;
      }
    }while(hasMoved && sand.depth < deepestRock);

  }

  private Set<Coordinates> getRocks(List<String> lines) {
    Set<Coordinates> rocks = new HashSet<>();
    for (String line : lines) {
      List<Coordinates> coordinates = Arrays.stream(line.split(" -> ")).map(Coordinates::new)
          .toList();
      for (int i = 0; i < coordinates.size() - 1; i++) {
        rocks.addAll(buildCoordline(coordinates.get(i), coordinates.get(i + 1)));
      }
    }
    return rocks;
  }

  private Set<Coordinates> buildCoordline(Coordinates c1, Coordinates c2) {
    Set<Coordinates> line = new HashSet<>();
    if(c1.x.equals(c2.x)){
      int smaller = c1.depth >c2.depth ?c2.depth : c1.depth;
      int bigger = c1.depth >c2.depth ?c1.depth : c2.depth;
      for(int i = smaller; i <= bigger; i++ ){
        line.add(new Coordinates(c1.x,i));
      }
    }

    if(c1.depth.equals(c2.depth)){
      int smaller = c1.x>c2.x?c2.x: c1.x;
      int bigger = c1.x>c2.x?c1.x: c2.x;
      for(int i = smaller; i <= bigger; i++ ){
        line.add(new Coordinates(i,c1.depth));
      }
    }
    return line;
  }

  @AllArgsConstructor
  @EqualsAndHashCode
  public static class Coordinates {
    public Coordinates(String coordinateString){
      String[] tmp = coordinateString.split(",");
      x= Integer.valueOf(tmp[0]);
      depth = Integer.valueOf(tmp[1]);
    }

    Integer x;
    Integer depth;

    public void moveDown(){
      this.depth++;
    }
    public void moveDownLeft(){
      this.depth++;
      this.x--;
    }
    public void moveDownRight(){
      this.depth++;
      this.x++;
    }

  }


}

