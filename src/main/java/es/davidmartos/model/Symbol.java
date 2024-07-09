package es.davidmartos.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Symbol {

  @JsonProperty("reward_multiplier")
  private BigDecimal rewardMultiplier;

  private String type;

  private String impact;

  private BigDecimal extra;
}
