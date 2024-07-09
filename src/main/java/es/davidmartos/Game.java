package es.davidmartos;

import es.davidmartos.exception.NotFoundException;
import es.davidmartos.model.GameConfig;
import es.davidmartos.model.Result;
import es.davidmartos.model.RewardResult;
import es.davidmartos.model.StandardSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {

  private GameConfig gameConfig;

  public Game(GameConfig gameConfig) {
    this.gameConfig = gameConfig;
  }

  public Result playGame(Double betAmount) throws NotFoundException {

    var matrix = generateMatrix();
    var winningCombinations = checkWinningCombinations(matrix);
    var rewardResult = calculateAndApplyBonus(matrix, betAmount, winningCombinations);

    return new Result(
        matrix,
        rewardResult.getReward(),
        winningCombinations,
        rewardResult.getAppliedBonusSymbols());
  }

  private String[][] generateMatrix() throws NotFoundException {
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

  private String getRandomSymbol(Map<String, Integer> symbols, Random random) {
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

  private Map<String, List<String>> checkWinningCombinations(String[][] matrix) {

    var winningCombinations = new HashMap<String, List<String>>();
    var symbolCounts = new HashMap<String, Integer>();
    var highestSameSymbolCombination = new HashMap<String, String>();

    var standardSymbols = gameConfig.getStandardSymbols();

    // Track symbol counts and highest same-symbol combination
    for (int row = 0; row < gameConfig.getRows(); row++) {
      for (int col = 0; col < gameConfig.getColumns(); col++) {
        var symbol = matrix[row][col];
        if (standardSymbols.contains(symbol)) { // Only consider standard symbols
          symbolCounts.put(symbol, symbolCounts.getOrDefault(symbol, 0) + 1);
        }
      }
    }

    // Determine the highest applicable same-symbol combination for each symbol
    for (var entry : gameConfig.getWinCombinations().entrySet()) {
      var combinationName = entry.getKey();
      var winCombination = entry.getValue();

      if ("same_symbols".equals(winCombination.getWhen())) {
        for (var countEntry : symbolCounts.entrySet()) {
          var symbol = countEntry.getKey();
          var count = countEntry.getValue();

          if (count >= winCombination.getCount()) {
            var currentHighestCombination = highestSameSymbolCombination.get(symbol);
            if (currentHighestCombination == null
                || gameConfig.getWinCombinations().get(currentHighestCombination).getCount()
                    < winCombination.getCount()) {
              highestSameSymbolCombination.put(symbol, combinationName);
            }
          }
        }
      }
    }

    // Add highest same-symbol combinations to winningCombinations
    for (var entry : highestSameSymbolCombination.entrySet()) {
      var symbol = entry.getKey();
      var combinationName = entry.getValue();
      var list = new ArrayList<String>();
      list.add(combinationName);
      winningCombinations.put(symbol, list);
    }

    // Check for linear symbols combinations
    for (var entry : gameConfig.getWinCombinations().entrySet()) {
      var combinationName = entry.getKey();
      var winCombination = entry.getValue();

      if ("linear_symbols".equals(winCombination.getWhen())) {
        for (var coveredArea : winCombination.getCoveredAreas()) {
          String firstSymbol = null;
          var isWinningCombination = true;
          for (var position : coveredArea) {
            var parts = position.split(":");
            var row = Integer.parseInt(parts[0]);
            var col = Integer.parseInt(parts[1]);
            var symbol = matrix[row][col];

            if (firstSymbol == null) {
              firstSymbol = symbol;
            } else if (!firstSymbol.equals(symbol)) {
              isWinningCombination = false;
              break;
            }
          }

          if (isWinningCombination) {
            winningCombinations
                .computeIfAbsent(firstSymbol, k -> new ArrayList<>())
                .add(combinationName);
          }
        }
      }
    }

    return winningCombinations;
  }

  public RewardResult calculateAndApplyBonus(
      String[][] matrix, Double betAmount, Map<String, List<String>> winningCombinations) {

    var reward = 0.0;

    for (var entry : winningCombinations.entrySet()) {
      var symbol = entry.getKey();
      var combinations = entry.getValue();
      var symbolRewardMultiplier = gameConfig.getSymbols().get(symbol).getRewardMultiplier();
      var combinedMultiplier = 1.0;

      for (var combination : combinations) {
        var winCombination = gameConfig.getWinCombinations().get(combination);
        combinedMultiplier *= winCombination.getRewardMultiplier();
      }
      // Calculate the reward contribution for this symbol and add to total reward
      reward += betAmount * symbolRewardMultiplier * combinedMultiplier;
    }

    if (reward <= 0.0) {
      return new RewardResult(reward, null);
    }

    // Apply bonus symbols
    var appliedBonusSymbols = new HashSet<String>();

    for (int row = 0; row < gameConfig.getRows(); row++) {
      for (int col = 0; col < gameConfig.getColumns(); col++) {
        var symbol = matrix[row][col];
        var configSymbol = gameConfig.getSymbols().get(symbol);
        if ("bonus".equals(configSymbol.getType())) {
          switch (configSymbol.getImpact()) {
            case "multiply_reward": // Multiply final reward to 5 or 10
              reward *= configSymbol.getRewardMultiplier();
              break;
            case "extra_bonus": // Add 500 or 1000 to the final reward
              reward += configSymbol.getExtra();
              break;
            case "miss": // None
              break;
          }
          appliedBonusSymbols.add(symbol);
        }
      }
    }

    return new RewardResult(reward, appliedBonusSymbols);
  }
}
