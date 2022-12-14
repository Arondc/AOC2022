package de.startat.aoc2022.days;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DayThirteen implements RunnableDay {

  public static final int IS_ORDERED = -1;
  public static final int IS_NOT_ORDERED = 1;

  public void run() {
    try {
      log.info("=== Day 13 ===");
      List<String> lines = Files.readAllLines(Path.of("src/main/resources/input_day13.txt"));
      log.info("=== Day 13 - First star ===");
      dayOneFirstStar(lines);
      log.info("=== Day 13 - Second star ===");
      dayOneSecondStar(lines);
    } catch (IndexOutOfBoundsException | IOException ioe) {
      log.error(ioe.getMessage(), ioe);
    }
  }

  private void dayOneFirstStar(List<String> lines) throws IOException {
    List<Comparison> comparisons = readAsComparisons(lines);

    for(Comparison c : comparisons){
      log.info(c.index + " " + c.isOrdered());
      log.info("");
    }

    int sumOfOrderedIndices = comparisons.stream().filter(Comparison::isOrdered)
        .mapToInt(Comparison::getIndex).sum();

    log.info("Sum of ordered indices: " + sumOfOrderedIndices);
  }

  private void dayOneSecondStar(List<String> lines) throws IOException {
    Comparison t = buildComparison(1000, "[[2]]", "[[6]]");

    List<Element> sortedElements = Stream.concat(Stream.of(t), readAsComparisons(lines).stream())
        .flatMap(c -> Stream.of(c.left, c.right)).sorted().toList();

    log.info((sortedElements.indexOf(t.left)+1) + " " + (sortedElements.indexOf(t.right)+1));
    log.info(((sortedElements.indexOf(t.left)+1) * (sortedElements.indexOf(t.right)+1)) + "");
  }

  private static class Comparison{
    int index;
    Element left;
    Element right;

    public Comparison(int index, Element left, Element right){
      this.index = index;
      this.left = left;
      this.right = right;
    }

    public int getIndex() {
      return index;
    }

    public boolean isOrdered(){
      return left.compareTo(right) < 0;
    }
  }

  @NoArgsConstructor
  @Setter
  @Getter
  private static class Element implements Comparable<Element> {
    Integer value = null;
    List<Element> list = new ArrayList<>();

    Element parent;

    public Element(Element parent){
      this.parent = parent;
    }

    public void addValueElement(int value) {
      Element e = new Element();
      e.value = value;
      list.add(e);
    }

    public Element getParent() {
      return parent;
    }

    public void addChildElement(Element childElement) {
      this.list.add(childElement);
    }

    public boolean isValue(){
      return value != null;
    }

    public boolean isEmptyList(){
      return value == null && list.size() == 0;
    }

    public boolean isNonemptyList(){
      return list.size()>0;
    }

    @Override
    public int compareTo(Element e2) {
      if(this.isValue() && e2.isValue()){
        if(this.value > e2.value){
          log.info("Linker wert größer als rechter Wert");
          return IS_NOT_ORDERED;
        } else if(this.value < e2.value){
          log.info("Linker wert kleiner als rechter Wert");
          return IS_ORDERED;
        }
      }

      if(this.isValue() && (e2.isNonemptyList() || e2.isEmptyList())){
        this.addValueElement(this.value);
        this.value = null;
        int comparison = this.compareTo(e2);
        if(comparison != 0){
          return comparison;
        }
      }

      if((this.isNonemptyList() || this.isEmptyList()) && e2.isValue()){
        e2.addValueElement(e2.value);
        e2.value = null;
        int comparison = this.compareTo(e2);
        if(comparison != 0){
          return comparison;
        }
      }

      if(this.isEmptyList() && e2.isNonemptyList()){
        log.info("leere Liste vs. nichtleere Liste");
        return -1;
      } else if(this.isNonemptyList() && e2.isEmptyList()){
        log.info("nichtleere Liste vs. leere Liste");
        return 1;
      }

      if(this.isNonemptyList() && e2.isNonemptyList()){
        for(int i = 0; i<this.list.size(); i++){
          if(i>e2.list.size()-1){
            log.info("Linke Liste länger als rechte Liste");
            return 1;
          }

          int comparison = this.list.get(i).compareTo(e2.list.get(i));
          if(comparison != 0){
            return comparison;
          }
        }
        if(this.list.size()<e2.list.size()){
          log.info("Linke Liste kürzer als rechte Liste");
          return -1;
        }
      }
      return 0;
    }
  }

  private static List<Comparison> readAsComparisons(List<String> lines) throws IOException {
    int index = 1;
    List<Comparison> comparisons = new ArrayList<>();
    String left = null;
    String right = null;
    for(String line : lines){
      if(left == null && !line.isBlank()){
        left = line;
      } else if(right == null && !line.isBlank()){
        right = line;
      } else if(line.isBlank()){
        Comparison c = buildComparison(index,left,right);
        left = null;
        right = null;
        comparisons.add(c);
        index++;
      }
    }
    Comparison c = buildComparison(index,left,right);
    comparisons.add(c);
    return comparisons;
  }

  private static Comparison buildComparison(int index, String left, String right) throws IOException {
    Element leftElement = buildElementTree(left);
    Element rightElement = buildElementTree(right);
    return new Comparison(index, leftElement, rightElement);
  }

  private static Element buildElementTree(String input) throws IOException {
    JsonParser parser = new ObjectMapper().createParser(input);
    Element root = null;
    Element currentElement = null;

    JsonToken token;
    do {
      token = parser.nextToken();
      if(token != null) {

        log.info(token + " " +  parser.getText() );
        if(token.isStructStart()) {
          log.info("neues Element erzeugen");
          currentElement = new Element(currentElement);
          if(currentElement.parent != null) {
            currentElement.parent.addChildElement(currentElement);
          }
          if(root == null){
            root = currentElement;
          }
        } else if (token.isNumeric()) {
          log.info("Neuen Wert ins Element packen");
          assert currentElement != null;
          currentElement.addValueElement(Integer.parseInt(parser.getText()));
        }
        if(token.isStructEnd()){
          assert currentElement != null;
          currentElement = currentElement.getParent();
          log.info("Ein Element hoch rücken");
        }
      }
    }while(token != null);
    return root;
  }

}
