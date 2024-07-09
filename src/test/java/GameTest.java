import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.davidmartos.Game;
import es.davidmartos.exception.NotFoundException;
import es.davidmartos.model.GameConfig;
import es.davidmartos.model.Result;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

  private Game game;

  @Before
  public void setUp() throws Exception {
    var mapper = new ObjectMapper();
    var gameConfig = mapper.readValue(new File("src/main/resources/config.json"), GameConfig.class);
    game = new Game(gameConfig);
  }

  @Test
  public void testPlayGame_AllWinningCombinations() throws NotFoundException {
    String[][] matrix = {
      {"A", "A", "A"},
      {"A", "A", "A"},
      {"A", "A", "A"}
    };

    var result = game.playGame(10.0, matrix);

    assertNotNull(result);
    assertRewardEquals("16000000.000", result.getReward());
    assertContainsWinningCombination(result, "A", "same_symbol_9_times", 1);
    assertContainsWinningCombination(result, "A", "same_symbols_horizontally", 3);
    assertContainsWinningCombination(result, "A", "same_symbols_vertically", 3);
    assertContainsWinningCombination(result, "A", "same_symbols_diagonally_left_to_right", 1);
    assertContainsWinningCombination(result, "A", "same_symbols_diagonally_right_to_left", 1);
  }

  @Test
  public void testPlayGame_NoWinningCombinations() throws NotFoundException {
    String[][] matrix = {
      {"A", "B", "C"},
      {"D", "E", "F"},
      {"A", "B", "C"}
    };

    var result = game.playGame(10.0, matrix);

    assertNotNull(result);
    assertRewardEquals("0.000", result.getReward());
    assertEquals(0, result.getAppliedWinningCombinations().size());
    assertNull(result.getAppliedBonusSymbol());
  }

  @Test
  public void testPlayGame_ThreeWinningCombinationAndBonus() throws NotFoundException {
    String[][] matrix = {
      {"A", "B", "C"},
      {"E", "B", "10x"},
      {"F", "D", "B"}
    };

    var result = game.playGame(10.0, matrix);

    assertNotNull(result);
    assertRewardEquals("2500.000", result.getReward());
    assertContainsWinningCombination(result, "B", "same_symbol_3_times", 1);
    assertTrue(result.getAppliedBonusSymbol().contains("10x"));
  }

  @Test
  public void testPlayGame_FourWinningCombinationAndAllBonus() throws NotFoundException {
    String[][] matrix = {
      {"+500", "A", "10x"},
      {"A", "+1000", "A"},
      {"A", "MISS", "5x"}
    };

    var result = game.playGame(10.00, matrix);

    assertNotNull(result);
    assertRewardEquals("67500.000", result.getReward());
    assertContainsWinningCombination(result, "A", "same_symbol_4_times", 1);
    assertContainsBonus(result, "+500");
    assertContainsBonus(result, "10x");
    assertContainsBonus(result, "+1000");
    assertContainsBonus(result, "MISS");
    assertContainsBonus(result, "5x");
  }

  @Test
  public void testPlayGame_NoWinningCombinationAndAllBonus_IsZero() throws NotFoundException {
    String[][] matrix = {
      {"+500", "A", "10x"},
      {"B", "+1000", "C"},
      {"D", "MISS", "5x"}
    };

    var result = game.playGame(10.00, matrix);

    assertNotNull(result);
    assertRewardEquals("0.000", result.getReward());
    assertEquals(0, result.getAppliedWinningCombinations().size());
    assertNull(result.getAppliedBonusSymbol());
  }

  private void assertRewardEquals(String expected, BigDecimal actual) {
    assertEquals(new BigDecimal(expected), actual.setScale(3, RoundingMode.HALF_UP));
  }

  private void assertContainsBonus(Result result, String bonus) {
    assertTrue(result.getAppliedBonusSymbol().contains(bonus));
  }

  private void assertContainsWinningCombination(
      Result result, String symbol, String combination, int expectedCount) {
    assertEquals(
        expectedCount,
        result.getAppliedWinningCombinations().get(symbol).stream()
            .filter(winningComb -> winningComb.equals(combination))
            .count());
  }
}
