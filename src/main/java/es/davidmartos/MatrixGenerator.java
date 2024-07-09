package es.davidmartos;

import es.davidmartos.exception.NotFoundException;
import es.davidmartos.model.GameConfig;
import es.davidmartos.model.StandardSymbol;
import java.util.Map;
import java.util.Random;

public class MatrixGenerator {

  public static String[][] generateMatrix(GameConfig gameConfig) throws NotFoundException {
    Random random = new Random();
    String[][] matrix = new String[gameConfig.getRows()][gameConfig.getColumns()];

    // Fill the matrix with standard symbols
    for (int row = 0; row < gameConfig.getRows(); row++) {
      for (int col = 0; col < gameConfig.getColumns(); col++) {
        StandardSymbol standardSymbol = null;
        for (var cp : gameConfig.getProbabilities().getStandardSymbols()) {
          if (cp.getRow() == row && cp.getColumn() == col) {
            standardSymbol = cp;
            break;
          }
        }
        if (standardSymbol == null) {
          throw new NotFoundException(
              "Standard Symbol Not Found at row " + row + " and col " + col);
        }
        matrix[row][col] = getRandomSymbol(standardSymbol.getSymbols(), random);
      }
    }

    // Add bonus symbols at random positions
    for (int i = 0; i < gameConfig.getProbabilities().getBonusSymbols().getSymbols().size(); i++) {
      var row = random.nextInt(gameConfig.getRows());
      var col = random.nextInt(gameConfig.getColumns());
      matrix[row][col] =
          getRandomSymbol(gameConfig.getProbabilities().getBonusSymbols().getSymbols(), random);
    }

    return matrix;
  }

  private static String getRandomSymbol(Map<String, Integer> symbols, Random random) {
    int totalProbability = symbols.values().stream().mapToInt(Integer::intValue).sum();
    int randomValue = random.nextInt(totalProbability) + 1;
    int cumulativeProbability = 0;

    for (Map.Entry<String, Integer> entry : symbols.entrySet()) {
      cumulativeProbability += entry.getValue();
      if (randomValue <= cumulativeProbability) {
        return entry.getKey();
      }
    }
    return null; // should never reach here
  }
}
