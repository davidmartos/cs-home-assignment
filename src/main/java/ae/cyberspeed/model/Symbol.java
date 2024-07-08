package ae.cyberspeed.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Symbol {

  @JsonProperty("reward_multiplier")
  private Double rewardMultiplier;

  private String type;

  private String impact;

  private Integer extra;
}
