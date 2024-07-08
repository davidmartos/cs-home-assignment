package ae.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class Probabilities {

  @JsonProperty("standard_symbols")
  private List<StandardSymbol> standardSymbols;

  @JsonProperty("bonus_symbols")
  private BonusSymbols bonusSymbols;
}
