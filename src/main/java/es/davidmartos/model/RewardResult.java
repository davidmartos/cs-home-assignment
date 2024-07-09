package es.davidmartos.model;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardResult {

  private BigDecimal reward;

  private List<String> appliedBonusSymbols;
}
