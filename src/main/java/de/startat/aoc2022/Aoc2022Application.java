package de.startat.aoc2022;

import de.startat.aoc2022.days.DayOne;
import de.startat.aoc2022.days.DayTwo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class Aoc2022Application implements CommandLineRunner {

  private final DayOne dayOne;
  private final DayTwo dayTwo;

  public static void main(String[] args) {
    SpringApplication.run(Aoc2022Application.class, args);
  }

  @Override
  public void run(String... args) {
    dayOne.run();
    dayTwo.run();
  }
}
