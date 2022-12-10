package de.startat.aoc2022;

import de.startat.aoc2022.days.DayEight;
import de.startat.aoc2022.days.DayFive;
import de.startat.aoc2022.days.DayFour;
import de.startat.aoc2022.days.DayNine;
import de.startat.aoc2022.days.DayOne;
import de.startat.aoc2022.days.DaySeven;
import de.startat.aoc2022.days.DaySix;
import de.startat.aoc2022.days.DayTen;
import de.startat.aoc2022.days.DayThree;
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
  private final DayThree dayThree;
  private final DayFour dayFour;
  private final DayFive dayFive;
  private final DaySix daySix;
  private final DaySeven daySeven;
  private final DayEight dayEight;
  private final DayNine dayNine;
  private final DayTen dayTen;

  public static void main(String[] args) {
    SpringApplication.run(Aoc2022Application.class, args);
  }

  @Override
  public void run(String... args) {
    /*
    dayOne.run();
    dayTwo.run();
    dayThree.run();
    dayFour.run();
    dayFive.run();
    daySix.run();
    daySeven.run();
    dayEight.run();
    dayNine.run();
     */

    dayNine.run();
    //dayTen.run();

  }
}
