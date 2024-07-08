package ae.cyberspeed.model;

import java.util.Map;
import lombok.Data;

@Data
public class StandardSymbol {

  private Integer column;

  private Integer row;

  private Map<String, Integer> symbols;
}
