import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import ae.cyberspeed.Game;
import ae.cyberspeed.exception.NotFoundException;
import ae.cyberspeed.model.GameConfig;
import ae.cyberspeed.model.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class GameTest {

  private Game game;

  @Before
  public void setUp() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    GameConfig gameConfig =
        mapper.readValue(new File("src/main/resources/config.json"), GameConfig.class);
    game = new Game(gameConfig);
  }

  @Test
  public void testPlayGame() throws NotFoundException {
    Double betAmount = 100.00;
    Result result = game.playGame(betAmount);

    assertNotNull(result);
    assertNotNull(result.getMatrix());
    assertTrue(result.getReward() >= 0);
    assertNotNull(result.getAppliedWinningCombinations());
  }

  //  String[][] matrix = {
  //      {"A", "B", "C"},
  //      {"E", "B", "5x"},
  //      {"F", "D", "C"}
  //  };
}
