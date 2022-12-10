package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayEight {

  public void run() {
    try {
      log.info("=== Day 8 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day8.txt"));
      log.info("=== Day 8 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 8 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    log.info("Zeilen " + lines.size() + " Spalten " + lines.get(0).length());

    //Jede Zeile der Eingabe zerlegen und die Zahlen in Trees "verpacken"
    List<List<Tree>> map = mapToTreeMap(lines);

    map.forEach(this::checkHorizontal);
    checkVertical(map);

    int visibleTrees = map.stream().flatMapToInt(l -> l.stream().mapToInt(t -> t.isVisible ? 1 : 0))
        .sum();

    log.info("Visible Trees on this map: " + visibleTrees);

  }

  private void dayOneSecondStar(List<String> lines) {
    List<List<Tree>> treeMap = mapToTreeMap(lines);

    int highestScenicScore = 0;
    for(int i = 0; i < treeMap.size(); i++){
      for(int j = 0; j < treeMap.get(0).size(); j++){
        int newScore = calculateScenicScore(i, j, treeMap);
        if(newScore > highestScenicScore){
          highestScenicScore = newScore;
        }
      }
    }
    log.info("Highest scenic score in that forest is " + highestScenicScore);
  }

  private int calculateScenicScore(int line, int column, List<List<Tree>> treeMap) {
    Tree tree = treeMap.get(line).get(column);
    return scenicScoreEast(line, column, treeMap, tree)*
    scenicScoreWest(line, column, treeMap, tree)*
    scenicScoreSouth(line, column, treeMap, tree)*
    scenicScoreNorth(line, column, treeMap, tree);
  }

  private static int scenicScoreNorth(int line, int column, List<List<Tree>> treeMap, Tree tree) {
    int scenicScore = 0;
    for(int i = line - 1; i >= 0; i--){
      scenicScore++;
      if(tree.height <= treeMap.get(i).get(column).height){
        break;
      }
    }
    return scenicScore;
  }

  private static int scenicScoreSouth(int line, int column, List<List<Tree>> treeMap, Tree tree) {
    int scenicScore = 0;
    for(int i = line + 1; i < treeMap.size(); i++){
      scenicScore++;
      if(tree.height <= treeMap.get(i).get(column).height){
        break;
      }
    }
    return scenicScore;
  }

  private static int scenicScoreWest(int line, int column, List<List<Tree>> treeMap, Tree tree) {
    int scenicScore = 0;
    for(int i = column - 1; i >= 0; i--){
      scenicScore++;
      if(tree.height <= treeMap.get(line).get(i).height){
        break;
      }
    }
    return scenicScore;
  }

  private static int scenicScoreEast(int line, int column, List<List<Tree>> treeMap, Tree tree) {
    int scenicScore = 0;
    for(int i = column +1; i < treeMap.get(line).size(); i++){
      scenicScore++;
      if(tree.height <= treeMap.get(line).get(i).height){
        break;
      }
    }
    return scenicScore;
  }

  private static List<List<Tree>> mapToTreeMap(List<String> lines) {
    return lines.stream().map(
        l -> Arrays.stream(l.split("")).map(v -> new Tree(Integer.parseInt(v))).toList()).toList();
  }



  private void checkVertical(List<List<Tree>> map) {
    for(int column = 0; column < map.get(0).size(); column++){
      int largestTop = -1;
      int largestBottom = -1;

      for(int i = 0; i < map.size(); i++){
        Tree treeTop = map.get(i).get(column);
        if(treeTop.height > largestTop){
          treeTop.setVisible(true);
          largestTop = treeTop.height;
        }

        Tree treeBottom = map.get(map.size()-(i+1)).get(column);
        if(treeBottom.height > largestBottom){
          treeBottom.setVisible(true);
          largestBottom = treeBottom.height;
        }
      }
    }

  }

  private void checkHorizontal(List<Tree> line) {
    int largestFront = -1;
    int largestBack = -1;

    for(int i = 0; i < line.size(); i++){
      Tree treeFront = line.get(i);
      if(treeFront.height > largestFront){
        treeFront.setVisible(true);
        largestFront = treeFront.height;
      }

      Tree treeBack = line.get(line.size()-(i+1));
      if(treeBack.height > largestBack){
        treeBack.setVisible(true);
        largestBack = treeBack.height;
      }
    }
  }



  @RequiredArgsConstructor
  private static class Tree {
    final int height;
    @Getter
    @Setter
    boolean isVisible;

    @Override
    public String toString() {
      return String.valueOf(height);
    }
  }

}
