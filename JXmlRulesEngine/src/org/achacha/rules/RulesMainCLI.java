package org.achacha.rules;

import java.io.File;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.dom4j.Document;

/**
 * Main colsole application
 */
public class RulesMainCLI
{
  /*
   * Instance based logger Initialized only when CLI is created to avoid wasting space if CLI is not used
   */
  private Logger logger = Logger.getLogger(RulesMainCLI.class);

  // CLI help strings
  private String HEADER = "JXmlRulesEngine";
  private String FOOTER = "Usage: do.sh -p /path/to/my_rulesset/ -r my_request.xml";

  // Logging level
  private int loggingLevel = 1;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    RulesEngineGlobal.initLog4j();
    new RulesMainCLI().doit(args);
  }

  private void doit(String[] args)
  {
    if (args.length < 1)
    {
      System.out.println(HEADER);
      System.out.println(FOOTER);
      System.out.println("Use -h or --help for command line options.");
      return;
    }

    CommandLine cl = null;
    Options options = new Options();
    options.addOption("p", "path", true, "Base path for the rule set");
    options.addOption("r", "request", true, "Request to process against the rules");
    options.addOption("h", "help", false, "Help");
    options.addOption("l", "logging-level", true, "Logging level (0-disabled, 1-errors, 2-events, 3-warnings, 4-info, 5-debug)");
    CommandLineParser parser = new GnuParser();
    try
    {
      cl = parser.parse(options, args);
    }
    catch(Exception e)
    {
      logger.error("Failed to parse command line.", e);
      System.err.println("Failed to parse command line." + e);
      return;
    }

    // Display help if requested
    if (cl.hasOption('h'))
    {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("do.sh", HEADER, options, FOOTER);
      return;
    }

    // Process command line input
    loggingLevel = Integer.parseInt(cl.getOptionValue("l","1"));
    if (loggingLevel < 0 || loggingLevel > 5)
    {
      System.out.println("Logging level of ("+loggingLevel+") is not valid, defaulting to 1");
      loggingLevel = 1;
    }
    String[] basePaths = cl.getOptionValues('p');
    if (null == basePaths)
    {
      System.err.println("Base path (-p) is required");
      return;
    }
    
    String[] requestPaths = cl.getOptionValues('r');
    if (null == requestPaths)
    {
      System.err.println("Request (-r) is required");
      return;
    }

    // Init rules engine
    RulesEngineGlobal.create(basePaths[0]);

    // Read the request XML
    File requestFile = new File(requestPaths[0]);
    Document requestDoc = RulesEngineHelper.loadXmlDocument(requestFile);
    
    // Execute rule
    RuleContext ruleContext = RulesEngineGlobal.getInstance().getRulesEngine().createContext(requestDoc.getRootElement(), loggingLevel);
//    Rule rule = RulesEngineGlobal.getInstance().getRulesEngine().getRule("ColorRed.rule.xml");
    RulesEngineGlobal.getInstance().getRulesEngine().execute(ruleContext);
    
    try
    {
      System.out.println(ruleContext);
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }
}
