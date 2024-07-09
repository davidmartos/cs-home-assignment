package es.davidmartos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {

  private String[][] matrix;

  private Double reward;

  @JsonProperty("applied_winning_combinations")
  private Map<String, List<String>> appliedWinningCombinations;

  @JsonProperty("applied_bonus_symbol")
  private Set<String> appliedBonusSymbol;
}
