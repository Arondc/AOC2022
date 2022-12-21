package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayTen implements RunnableDay{

  public void run() {
    try {
      log.info("=== Day 10 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day10.txt"));
      log.info("=== Day 10 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 10 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    List<Command> commands = lines.stream().map(Command::new).toList();
    List<Long> checkCycle = Arrays.asList(20L, 60L, 100L, 140L, 180L, 220L);

    long cycle = 1L;
    long x = 1L;
    long signalStrength= 0L;

    for (Command command : commands) {
      while (command.remainingCycles > 0) {
        command.remainingCycles--;
        if (checkCycle.contains(cycle)) {
          signalStrength += cycle * x;
        }
        cycle++;
      }
      x += command.argument;
    }

    log.info(signalStrength+"");
  }

  private void dayOneSecondStar(List<String> lines) {
    List<Command> commands = lines.stream().map(Command::new).toList();
    StringBuilder strBuf = new StringBuilder();

    long cycle = 1L;
    long x = 1L;

    for (Command command : commands) {
      while (command.remainingCycles > 0) {
        command.remainingCycles--;
        if((cycle-1)%40 == 0) {
          strBuf.append(System.lineSeparator());
        }

        strBuf.append(Math.abs(((cycle-1)%40)-x)<=1?"#":".");
        cycle++;
      }
      x += command.argument;
    }

    log.info(strBuf.toString());
  }



  public static class Command{
    int remainingCycles;
    int argument = 0;
    public Command(String command){
      if(command.equals("noop")){
        remainingCycles = 1;
      } else {
        remainingCycles = 2;
        argument = Integer.parseInt(command.split(" ")[1]);
      }
    }
  }



}
