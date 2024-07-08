package ae.cyberspeed.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RewardResult {

  private Double reward;

  private Set<String> appliedBonusSymbols;
}
