package es.davidmartos.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.davidmartos.config.BigDecimalWithoutTrailingZerosSerializer;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {

  private String[][] matrix;

  @JsonSerialize(using = BigDecimalWithoutTrailingZerosSerializer.class)
  private BigDecimal reward;

  @JsonProperty("applied_winning_combinations")
  private Map<String, List<String>> appliedWinningCombinations;

  @JsonProperty("applied_bonus_symbol")
  private List<String> appliedBonusSymbol;
}
