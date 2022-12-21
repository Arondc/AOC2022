package de.startat.aoc2022;

import de.startat.aoc2022.days.RunnableDay;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class Aoc2022Application implements CommandLineRunner {
  private final ApplicationContext applicationContext;
  public static void main(String[] args) {
    SpringApplication.run(Aoc2022Application.class, args);
  }
  @Override
  public void run(String... args) {
    ((RunnableDay)applicationContext.getBean("daySeventeen")).run();
  }
}
