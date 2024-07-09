package es.davidmartos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WinCombination {

  @JsonProperty("reward_multiplier")
  private Double rewardMultiplier;

  private String when;

  private Integer count;

  private String group;

  @JsonProperty("covered_areas")
  private List<List<String>> coveredAreas;
}
