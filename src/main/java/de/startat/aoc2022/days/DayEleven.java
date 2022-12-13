package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayEleven {

  private static List<Long> divisors = new ArrayList<>();
  private static Long kgvOfDivisors;

  public void run() {
    try {
      log.info("=== Day 11 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day11.txt"));
      log.info("=== Day 11 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 11 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    calculateMonkeyBusiness(lines, 20, true);
  }

  private void dayOneSecondStar(List<String> lines) {
    divisors = new ArrayList<>();
    calculateMonkeyBusiness(lines, 10000, false);
  }

  private static void calculateMonkeyBusiness(List<String> lines, int numberOfRounds, boolean firstStar) {
    List<String> currentMonkeyLines = new ArrayList<>();
    MonkeyParser mp = new MonkeyParser();

    Map<Long, Monkey> monkeys = buildMonkeyMap(lines,
        currentMonkeyLines, mp);

    kgvOfDivisors = divisors.stream().mapToLong(l->l).reduce(1L, (l1,l2) -> l1*l2);

    log.info(monkeys.toString());

    for(int i = 0; i<numberOfRounds; i++){
      if(i%1000==0){
        log.info("Round " + i);
        monkeys.values().forEach(m -> log.info("\tMonkey " + m.getNumber() + " inspected " + m.getInspectCounter()));

      }
      calculateRound(monkeys, firstStar);
    }

    log.info(monkeys.toString());

    List<Long> inspections = monkeys.values().stream().map(Monkey::getInspectCounter).sorted(Comparator.reverseOrder()).toList();
    long monkeyBusiness = inspections.get(0) * inspections.get(1);
    log.info("Monkey business done: " + monkeyBusiness);
  }



  private static Map<Long, Monkey> buildMonkeyMap(List<String> lines,
      List<String> currentMonkeyLines, MonkeyParser mp) {
    Map<Long,Monkey> monkeys = new HashMap<>();

    for(String l : lines){
      if(l.startsWith("Monkey")){
        currentMonkeyLines = new ArrayList<>();
        currentMonkeyLines.add(l);
      } else if (l.isBlank()) {
        Monkey m = mp.parse(currentMonkeyLines);
        monkeys.put(m.getNumber(), m);
      } else {
        currentMonkeyLines.add(l);
      }
    }
    if(!currentMonkeyLines.isEmpty()){
      Monkey m = mp.parse(currentMonkeyLines);
      monkeys.put(m.getNumber(), m);
    }

    return monkeys;
  }

  private static void calculateRound(Map<Long,Monkey> monkeys, boolean firstStar){
    for(long i = 0L; i<monkeys.size(); i++){
      Monkey currentMonkey = monkeys.get(i);
      //log.info("Monkey " + i + ":");
      while(!currentMonkey.items.isEmpty()) {
        Item item = currentMonkey.items.poll();
        //log.info("\tMonkey inspects an item with a worry level of " + item.getWorryLevel());
        currentMonkey.addInspection();
        currentMonkey.operation.accept(item);
        //log.info("\t\tWorry level changes to " + item.getWorryLevel());
        if(firstStar){
          item.divideWorryLevelByThree();
          //log.info("\t\tMonkey gets bored with item. Worry level is divided by 3 to " + item.getWorryLevel());
        }

        Monkey targetMonkey = monkeys.get(currentMonkey.test.apply(item));
        item.setWorryLevel(item.getWorryLevel() % kgvOfDivisors);
        //log.info("\t\tItem is thrown to monkey " + targetMonkey.number);
        targetMonkey.items.offer(item);
      }
    }
  }

  private static class MonkeyParser{
    public Monkey parse(List<String> currentMonkeyLines) {
      Long number = null;
      Queue<Item> items = null;
      Consumer<Item> operation = null;
      Function<Item, Long> test;
      Long testDivisibleBy = null;
      Long testTargetTrue = null;
      Long testTargetFalse = null;

      for(String l : currentMonkeyLines){
        String trimmedLine = l.trim();
        if(trimmedLine.startsWith("Monkey")){
          number = Long.valueOf(StringUtils.substringBetween(trimmedLine," ", ":"));
        } else if (trimmedLine.startsWith("Starting")){
          items = Arrays.stream(
              StringUtils.substringAfter(trimmedLine, ": ").split(", ")
          ).map(Long::parseLong).map(Item::new).collect(Collectors.toCollection(LinkedList::new));
        } else if( trimmedLine.startsWith("Operation")) {
          String[] operationString = StringUtils.substringAfter(trimmedLine, "= ").split(" ");
          if(StringUtils.isNumeric(operationString[2])){
            operation = switch (operationString[1]) {
              case "+" -> item -> item.setWorryLevel(
                  item.getWorryLevel() + Long.parseLong(operationString[2]));
              case "-" -> item -> item.setWorryLevel(
                  item.getWorryLevel() - Long.parseLong(operationString[2]));
              case "*" -> item -> item.setWorryLevel(
                  item.getWorryLevel() * Long.parseLong(operationString[2]));
              case "/" -> item -> item.setWorryLevel(
                  item.getWorryLevel() / Long.parseLong(operationString[2]));
              default -> throw new RuntimeException("UNKNOWN OPERATION " + trimmedLine);
            };
          } else {
            operation = switch (operationString[1]) {
              case "+" -> item -> item.setWorryLevel(item.getWorryLevel() + item.getWorryLevel());
              case "-" -> item -> item.setWorryLevel(0L);
              case "*" -> item -> item.setWorryLevel(item.getWorryLevel() * item.getWorryLevel());
              case "/" -> item -> item.setWorryLevel(1L);
              default -> throw new RuntimeException("UNKNOWN OPERATION " + trimmedLine);
            };
          }
        } else if(trimmedLine.startsWith("Test")){
          testDivisibleBy = Long.valueOf(StringUtils.substringAfterLast(trimmedLine," "));
        } else if(trimmedLine.startsWith("If true")){
          testTargetTrue = Long.valueOf(StringUtils.substringAfterLast(trimmedLine," "));
        } else if(trimmedLine.startsWith("If false")){
          testTargetFalse = Long.valueOf(StringUtils.substringAfterLast(trimmedLine," "));
        } else {
          throw new RuntimeException("ERROR: UNKOWN LINE - " + l);
        }
      }
      Long finalTestDivisibleBy = testDivisibleBy;
      Long finalTestTargetTrue = testTargetTrue;
      Long finalTestTargetFalse = testTargetFalse;
      divisors.add(finalTestDivisibleBy);
      test = i -> i.getWorryLevel() % finalTestDivisibleBy == 0 ? finalTestTargetTrue : finalTestTargetFalse;
      return new Monkey(number, items==null?new LinkedList<>():items, operation, test, 0L);
    }
  }




  @AllArgsConstructor
  @Data
  private static class Monkey{
    private Long number;
    private Queue<Item> items;
    private Consumer<Item> operation;
    private Function<Item, Long> test;

    private Long inspectCounter;

    public void addInspection(){
      inspectCounter++;
    }
  }

  @AllArgsConstructor
  @Data
  private static class Item{
    private Long worryLevel;

    public void divideWorryLevelByThree(){
      worryLevel/=3;
    }
  }

}
