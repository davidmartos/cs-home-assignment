package es.davidmartos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.davidmartos.exception.NotFoundException;
import es.davidmartos.model.GameConfig;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main {

  public static void main(String[] args) {
    var cmd = readArgs(args);
    var configFilePath = cmd.getOptionValue("config");
    var bettingAmount = Double.parseDouble(cmd.getOptionValue("betting-amount"));

    System.out.println("Config file path: " + configFilePath);
    System.out.println("Betting amount: " + bettingAmount);

    try {
      var mapper = new ObjectMapper();
      var classloader = Thread.currentThread().getContextClassLoader();
      var is = classloader.getResourceAsStream(configFilePath);
      var gameConfig = mapper.readValue(is, GameConfig.class);
      System.out.println("GameConfig loaded correctly.");

      var game = new Game(gameConfig);
      var result = game.playGame(bettingAmount);
      var output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
      System.out.println(output);

    } catch (NotFoundException e) {
      System.out.println("AttributeNotFound: " + e.getMessage());
    } catch (JsonProcessingException e) {
      System.out.println("JsonProcessingException " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IOException " + e.getMessage());
    }
  }

  private static CommandLine readArgs(String[] args) {
    var options = new Options();

    var configOption = new Option("c", "config", true, "Path to config file");
    configOption.setRequired(true);
    options.addOption(configOption);

    var bettingAmountOption = new Option("b", "betting-amount", true, "Betting amount");
    bettingAmountOption.setRequired(true);
    options.addOption(bettingAmountOption);

    var parser = new DefaultParser();
    var formatter = new HelpFormatter();
    CommandLine cmd = null;

    try {
      cmd = parser.parse(options, args);
    } catch (ParseException e) {
      System.out.println(e.getMessage());
      formatter.printHelp(
          "java -jar <your-jar-file> --config <config-file-path> --betting-amount <betting-amount>",
          options);
      System.exit(1);
    }

    return cmd;
  }
}
