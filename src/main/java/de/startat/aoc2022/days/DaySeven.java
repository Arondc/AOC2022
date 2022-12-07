package de.startat.aoc2022.days;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DaySeven {

  public static final int MAX_SIZE_FIRST_STAR = 100_000;
  public static final int FILE_SYSTEM_MAX_SIZE = 70000000;
  public static final int UPDATE_NEEDS_SIZE = 30000000;

  public static final Predicate<Directory> SIZE_SMALLER_THAN_HUNDRED_K = dir -> dir.getSize()
      < MAX_SIZE_FIRST_STAR;

  public void run() {
    try {
      log.info("=== Day 7 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day7.txt"));
      log.info("=== Day 7 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 7 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) {
    Traverser traverser = new Traverser();
    traverser.buildTree(lines);

    Set<Directory> foundDirectories = findDirectoriesWith(traverser.getRoot(),
        SIZE_SMALLER_THAN_HUNDRED_K);

    log.info("Found directories that are smaller than " + MAX_SIZE_FIRST_STAR);
    long totalSize = foundDirectories.stream().mapToLong(Directory::getSize).sum();
    log.info("Total size: " + totalSize);
  }

  private void dayOneSecondStar(List<String> lines) {
    Traverser traverser = new Traverser();
    traverser.buildTree(lines);

    final long currentFreeSpace = FILE_SYSTEM_MAX_SIZE - traverser.getRoot().getSize();
    final long additionalSpaceNeeded = UPDATE_NEEDS_SIZE - currentFreeSpace;
    log.info("Current Memory usage: " + currentFreeSpace);
    log.info("Additional Memory needed: " + additionalSpaceNeeded);

    Predicate<Directory> p = d -> d.getSize() > additionalSpaceNeeded;
    Set<Directory> foundDirectories = findDirectoriesWith(traverser.getRoot(), p);
    long directorySizeToDelete = foundDirectories.stream().mapToLong(Directory::getSize).min()
        .orElseThrow();
    log.info("Directory can be deleted for size: " + directorySizeToDelete);

  }

  private static Set<Directory> findDirectoriesWith(Directory start, Predicate<Directory> p) {
    Queue<Directory> directoriesToProcess = new LinkedList<>();
    directoriesToProcess.add(start);
    Set<Directory> foundDirectories = new HashSet<>();
    while (!directoriesToProcess.isEmpty()) {
      Directory d = directoriesToProcess.poll();
      if (p.test(d)) {
        foundDirectories.add(d);
      }
      directoriesToProcess.addAll(d.getSubDirectories());
    }
    return foundDirectories;
  }


  private static class Traverser {

    Directory root = new Directory("root");
    Directory currentDirectory = root;

    public void buildTree(List<String> inputs) {
      for (String input : inputs) {
        if (input.startsWith("$")) {
          parseCommand(input.substring(2));
        } else {
          parseOutput(input);
        }
      }
    }

    private void parseOutput(String input) {
      if (input.startsWith("dir")) {
        currentDirectory.addDir(input.split(" ")[1], currentDirectory);
      } else {
        String[] fileInfos = input.split(" ");
        currentDirectory.addFile(Long.parseLong(fileInfos[0]), fileInfos[1]);
      }
    }

    private void parseCommand(String substring) {
      if (substring.startsWith("cd")) {
        String targetFolder = substring.split(" ")[1];
        if (targetFolder.equals("/")) {
          currentDirectory = root;
        } else if (targetFolder.equals("..")) {
          currentDirectory = currentDirectory.parentDirectory;
        } else {
          currentDirectory = currentDirectory.addDir(targetFolder, currentDirectory);
        }
      }
    }

    public Directory getRoot() {
      return root;
    }
  }

  @Data
  private static class Directory {

    String name;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    Directory parentDirectory = null;
    @ToString.Exclude
    Set<Directory> subDirectories = new HashSet<>();
    Set<File> files = new HashSet<>();

    public Directory(String name) {
      this(name, null);
    }

    public Directory(String name, Directory parentDirectory) {
      this.name = name;
      this.parentDirectory = parentDirectory;
    }

    @ToString.Include
    long getSize() {
      return files.stream().mapToLong(File::getSize).sum() + subDirectories.stream()
          .mapToLong(Directory::getSize).sum();
    }

    Directory addDir(String dirName, Directory parentDirectory) {
      Directory directory = new Directory(dirName, parentDirectory);
      if (!subDirectories.contains(directory)) {
        subDirectories.add(directory);
      } else {
        directory = subDirectories.stream().filter(d -> d.getName().equals(dirName)).findFirst()
            .orElseThrow();
      }
      return directory;
    }

    public void addFile(long filesize, String filename) {
      files.add(new File(filename, filesize));
    }
  }

  @Data
  @AllArgsConstructor
  private static class File {

    long size;
    String name;

    public File(String filename, long filesize) {
      this.name = filename;
      this.size = filesize;
    }
  }
}
