package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayTwelve implements RunnableDay{

  private static final Map<String, Integer> heightMap = new HashMap<>();

  static {
    for (int i = 0; i < 26; i++) {
      heightMap.put(String.valueOf((char) ('a' + i)), i);
    }
    heightMap.put("S", 0);
    heightMap.put("E", 25);
  }

  public void run() {
    try {
      log.info("=== Day 12 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day12.txt"));
      log.info("=== Day 12 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 12 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    Map<String, Node> nodeMap = buildNodeMap(lines);

    Node startNode = nodeMap.values().stream().filter(Node::isStart).findFirst().orElseThrow();
    Queue<Node> processingQueue = new LinkedList<>();
    processingQueue.add(startNode);

    processPaths(processingQueue);

    Node endNode = nodeMap.values().stream().filter(Node::isEnd).findFirst().orElseThrow();
    log.info(endNode.shortestPathLength + "");
  }

  private void dayOneSecondStar(List<String> lines) {
    Map<String, Node> nodeMap = buildNodeMap(lines);

    //For the second star we just start everywhere on the lowest elevation at the same time
    Queue<Node> processingQueue = new LinkedList<>();
    nodeMap.values().stream()
        .filter(n -> n.height == 0).forEach(n -> {
          n.shortestPathLength = 0;
          processingQueue.add(n);
        });

    processPaths(processingQueue);

    Node endNode = nodeMap.values().stream().filter(Node::isEnd).findFirst().orElseThrow();
    log.info(endNode.shortestPathLength + "");
  }

  private static Map<String, Node> buildNodeMap(List<String> lines) {
    Map<String, Node> nodeMap = new HashMap<>();
    for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
      String line = lines.get(lineNumber);
      String[] split = line.split("");
      for (int columnNumber = 0; columnNumber < split.length; columnNumber++) {
        String s = split[columnNumber];
        Node node = new Node(s, lineNumber, columnNumber);
        nodeMap.put(lineNumber + "-" + columnNumber, node);
      }
    }

    for (Node n : nodeMap.values()) {
      Node northNeighbour = nodeMap.get((n.line - 1) + "-" + (n.column));
      n.addNeighbour(northNeighbour);
      Node southNeighbour = nodeMap.get((n.line + 1) + "-" + (n.column));
      n.addNeighbour(southNeighbour);
      Node westNeighbour = nodeMap.get((n.line) + "-" + (n.column - 1));
      n.addNeighbour(westNeighbour);
      Node eastNeighbour = nodeMap.get((n.line) + "-" + (n.column + 1));
      n.addNeighbour(eastNeighbour);
    }

    return nodeMap;
  }


  private static void processPaths(Queue<Node> processingQueue) {
    while (!processingQueue.isEmpty()) {
      final Node currentNode = processingQueue.poll();
      currentNode.neighbours.forEach(n -> {
        int newShortestPathLength = currentNode.shortestPathLength + 1;
        if (newShortestPathLength < n.shortestPathLength) {
          log.info("New shortest path length to " + n.getLine() + "-" + n.getColumn() + " -> "
              + newShortestPathLength);
          n.shortestPathLength = newShortestPathLength;
          if(n.isEnd){
            return;
          }
          log.info("Adding Node " + n.getLine() + "-" + n.getColumn() + " to processing");
          processingQueue.add(n);
        }
      });
    }
  }

  @EqualsAndHashCode(onlyExplicitlyIncluded = true)
  @Getter
  @Setter
  private static class Node {

    @EqualsAndHashCode.Include
    Integer line;
    @EqualsAndHashCode.Include
    Integer column;

    int height;
    boolean isStart;
    boolean isEnd;
    int shortestPathLength = Integer.MAX_VALUE;

    Set<Node> neighbours = new HashSet<>();

    public void addNeighbour(Node n) {
      //We just store neighbours we actually can reach
      if (n != null && this.height + 1 >= n.height) {
        neighbours.add(n);
      }
    }

    public Node(String height, int line, int column) {
      this.height = heightMap.get(height);
      this.line = line;
      this.column = column;
      isStart = height.equals("S");
      isEnd = height.equals("E");

      if (isStart) {
        shortestPathLength = 0;
      }
    }


  }
}
