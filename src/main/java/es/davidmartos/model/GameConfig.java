package es.davidmartos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class GameConfig {

  private Integer columns;

  private Integer rows;

  private Map<String, Symbol> symbols;

  private Probabilities probabilities;

  @JsonProperty("win_combinations")
  private Map<String, WinCombination> winCombinations;

  public Set<String> getStandardSymbols() {
    return symbols.entrySet().stream()
        .filter(symbol -> symbol.getValue().getType().equals("standard"))
        .map(Map.Entry::getKey)
        .collect(Collectors.toSet());
  }
}
