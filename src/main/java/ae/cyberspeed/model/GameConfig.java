package ae.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Data;

@Data
public class GameConfig {

  private Integer columns;

  private Integer rows;

  private Map<String, Symbol> symbols;

  private Probabilities probabilities;

  @JsonProperty("win_combinations")
  private Map<String, WinCombination> winCombinations;
}
