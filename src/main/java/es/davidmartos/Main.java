package es.davidmartos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.davidmartos.exception.NotFoundException;
import es.davidmartos.model.GameConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

  public static void main(String[] args) {
    try {
      var cmd = readArgs(args);
      var configFilePath = cmd.getOptionValue("config");
      var bettingAmount = new BigDecimal(cmd.getOptionValue("betting-amount"));

      var mapper = new ObjectMapper();
      var inputStream = readInputFile(configFilePath);
      var gameConfig = mapper.readValue(inputStream, GameConfig.class);
      System.out.println("GameConfig loaded correctly.");

      var game = new Game(gameConfig);
      var matrix = MatrixGenerator.generateMatrix(gameConfig);
      var result = game.playGame(bettingAmount, matrix);
      var output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
      System.out.println(output);

    } catch (NotFoundException e) {
      System.out.println("AttributeNotFound: " + e.getMessage());
    } catch (JsonProcessingException e) {
      System.out.println("JsonProcessingException " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IOException " + e.getMessage());
    } catch (ParseException e) {
      System.out.println(e.getMessage());
    } finally {
      System.exit(1);
    }
  }

  private static CommandLine readArgs(String[] args) throws ParseException {
    var options = new Options();

    var configOption = new Option("c", "config", true, "Path to config file");
    configOption.setRequired(true);
    options.addOption(configOption);

    var bettingAmountOption = new Option("b", "betting-amount", true, "Betting amount");
    bettingAmountOption.setRequired(true);
    options.addOption(bettingAmountOption);

    var parser = new DefaultParser();

    return parser.parse(options, args);
  }

  private static InputStream readInputFile(String path) throws FileNotFoundException {
    var configFile = new File(path);

    if (configFile.isAbsolute()) {
      return new FileInputStream(configFile);
    }

    var classloader = Thread.currentThread().getContextClassLoader();
    return classloader.getResourceAsStream(path);
  }
}
