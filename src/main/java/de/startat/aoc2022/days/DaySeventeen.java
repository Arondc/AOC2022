package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DaySeventeen implements RunnableDay {

  int jetCounter = 0;

  long block = 0;

  public void run() {
    try {
      log.info("=== Day 17 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day17.txt"));
      log.info("=== Day 17 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 17 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    List<String> jets = List.of(lines.get(0).split(""));
    Set<Coordinate> settledBlocks = new HashSet<>();

    for (int i = 0; i < 2022; i++) {
      BlockType blockType = BlockType.values()[i % 5];
      long spawnLine = settledBlocks.stream().mapToLong(p -> p.y)
          .max()
          .orElse(-1)+4;
      final Block b = blockFactory(spawnLine, blockType);
      moveBlock(b, jets, settledBlocks);


      b.pixel.forEach(p -> p.setRepresentation(b.getType().getR()));
      settledBlocks.addAll(b.pixel);
      block++;
    }

    long highestLine = settledBlocks.stream().mapToLong(p -> p.y)
        .max().orElseThrow() + 1;
    log.info("Highest line after 2022 blocks " + highestLine);

    print(settledBlocks);
  }

  private void print(Set<Coordinate> settledBlocks) {
    StringBuilder sb = new StringBuilder();

    List<Coordinate> sortedBlocks = settledBlocks.stream().sorted(
        Comparator.comparing(Coordinate::getY).reversed()
            .thenComparing(Coordinate::getX)).toList();


    int lineIndex = 0;
    long lastLine = 4000;
    for(Coordinate b : sortedBlocks){
      if(b.getY()<lastLine){
        if(lineIndex<7){
          sb.append(StringUtils.repeat(".", (7 - lineIndex)));
        }

        sb.append(System.lineSeparator());
        lineIndex=0;
      }
      lastLine = b.getY();

      if(b.getX() > lineIndex) {
        sb.append(StringUtils.repeat(".", (int) (b.getX() - lineIndex)));
      }
      sb.append(b.representation);
      lineIndex = (int) b.getX()+1;
    }

    try {
      Files.writeString(Path.of("tetris_tower.txt"), sb.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }


  }

  private void dayOneSecondStar(List<String> lines) {
    // Did not do this
  }

  public void moveBlock(Block block, List<String> jets, Set<Coordinate> settledBlocks) {
    while(true){
      //log.info(jets.get(jetCounter) +" ");
      switch (jets.get(jetCounter++)) {
        case "<" -> {
          if (checkLeftMovement(block, settledBlocks)) {
            block.moveLeft();
          }
        }
        case ">" -> {
          if (checkRightMovement(block, settledBlocks)) {
            block.moveRight();
          }
        }
      }

      if(jetCounter % jets.size() == 0){
        jetCounter %= jets.size();
      }

      if(checkDownMovement(block,settledBlocks)){
        //log.info("down");
        block.moveDown();
      } else {
        return;
      }



    }
  }

  private boolean checkLeftMovement(Block block, Set<Coordinate> settledBlocks) {
    if(block.pixel.stream().anyMatch(p -> p.x == 0)){
      return false;
    }

    return block.pixel.stream().noneMatch(p -> settledBlocks.stream()
        .anyMatch(sp -> p.y == sp.y && p.x - 1 == sp.x));
  }

  private boolean checkRightMovement(Block block, Set<Coordinate> settledBlocks) {
    if(block.pixel.stream().anyMatch(p -> p.x == 6)){
      return false;
    }

    return block.pixel.stream().noneMatch(p -> settledBlocks.stream()
        .anyMatch(sp -> p.y == sp.y && p.x + 1 == sp.x));
  }

  private boolean checkDownMovement(Block block, Set<Coordinate> settledBlocks) {
    if(block.pixel.stream().anyMatch(p -> p.y == 0)){
      return false;
    }

    return block.pixel.stream().noneMatch(p -> settledBlocks.stream()
        .anyMatch(sp -> p.x == sp.x && p.y - 1 == sp.y));
  }


  private static class Block{

    private final BlockType type;

    public BlockType getType() {
      return type;
    }

    public Block(Set<Coordinate> pixel, BlockType blockType) {
      this.pixel = pixel;
      this.type = blockType;
    }
    Set<Coordinate> pixel;

    public void moveDown(){
      pixel.forEach(Coordinate::moveDown);
    }

    public void moveLeft(){
      pixel.forEach(Coordinate::moveLeft);
    }

    public void moveRight(){
      pixel.forEach(Coordinate::moveRight);
    }

    @Override
    public String toString() {
      return pixel.stream().map(p -> p.y + ":" + p.x).collect(Collectors.joining(","));
    }
  }

  @Data
  @Builder
  private static class Coordinate{
    long x;
    long y;

    @Builder.Default
    String representation  = "#";

    Coordinate(long x, long y) {
      this.x = x;
      this.y = y;
    }

    public void moveDown(){
      y -=1;
    }

    public void moveLeft(){
      x -=1;
    }

    public void moveRight(){
      x +=1;
    }

  }

  enum BlockType {
    HORIZONTAL("H"),PLUS("P"),BACKWARDS_L("L"),CAPITAL_I("I"),SQUARE("S");

    BlockType(String r){
      this.r = r;
    }

    final String r;

    public String getR() {
      return r;
    }
  }

  private static Block blockFactory(long lineToSpawnOn, BlockType blockType){
    //log.info("Building " + blockType.name() + " block on line " + lineToSpawnOn);
    Set<Coordinate> pixels = switch (blockType){
      // ####
      case HORIZONTAL -> new HashSet<>(
          Arrays.asList(
              Coordinate.builder().x(2).y(lineToSpawnOn).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn).build(),
              Coordinate.builder().x(4).y(lineToSpawnOn).build(),
              Coordinate.builder().x(5).y(lineToSpawnOn).build()
          ));

      //   .#.
      //   ###
      //   .#.
      case PLUS -> new HashSet<>(
          Arrays.asList(
              Coordinate.builder().x(3).y(lineToSpawnOn).build(),
              Coordinate.builder().x(2).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(4).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn+2).build()
          ));
       // ..#
       // ..#
       // ###
      case BACKWARDS_L -> new HashSet<>(
          Arrays.asList(
              Coordinate.builder().x(2).y(lineToSpawnOn).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn).build(),
              Coordinate.builder().x(4).y(lineToSpawnOn).build(),
              Coordinate.builder().x(4).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(4).y(lineToSpawnOn+2).build()
          ));
      // #
      // #
      // #
      // #
      case CAPITAL_I -> new HashSet<>(
          Arrays.asList(
              Coordinate.builder().x(2).y(lineToSpawnOn).build(),
              Coordinate.builder().x(2).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(2).y(lineToSpawnOn+2).build(),
              Coordinate.builder().x(2).y(lineToSpawnOn+3).build()
          ));
      // ##
      // ##

      case SQUARE -> new HashSet<>(
          Arrays.asList(
              Coordinate.builder().x(2).y(lineToSpawnOn).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn).build(),
              Coordinate.builder().x(2).y(lineToSpawnOn+1).build(),
              Coordinate.builder().x(3).y(lineToSpawnOn+1).build()
          ));
    };
    return new Block(pixels, blockType);
  }
}
