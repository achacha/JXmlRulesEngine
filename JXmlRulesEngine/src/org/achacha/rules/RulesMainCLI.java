package org.achacha.rules;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
  private String basePath;
  private String requestPath;
  private String outputPath;
  private int loggingLevel = 1;

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    RulesEngineGlobal.initLog4j();
    RulesMainCLI me = new RulesMainCLI(args);
    me.executeRule();
  }
  
  public RulesMainCLI(String[] args)
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
    options.addOption("o", "output", true, "File to write output XML");
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
    basePath = basePaths[0];
    
    String[] requestPaths = cl.getOptionValues('r');
    if (null == requestPaths)
    {
      System.err.println("Request (-r) is required");
      return;
    }
    requestPath = requestPaths[0];
    
    String[] outputPaths = cl.getOptionValues('o');
    if (null != outputPaths)
    {
      outputPath = outputPaths[0];
    }
  }
  
  private void executeRule()
  {
    // Init rules engine
    RulesEngineGlobal.create(basePath);

    // Read the request XML
    File requestFile = new File(requestPath);
    Document requestDoc = RulesEngineHelper.loadXmlDocument(requestFile);
    
    // Execute rule
    RuleContext ruleContext = RulesEngineGlobal.getInstance().getRulesEngine().createContext(requestDoc.getRootElement(), loggingLevel);
    RulesEngineGlobal.getInstance().getRulesEngine().execute(ruleContext);
    
    if (null != outputPath)
    {
      File outfile = new File(outputPath);
      try
      {
        System.out.println("Writing output to: "+outputPath);
        FileWriter writer = new FileWriter(outfile);
        writer.write(ruleContext.getOutputModel().asXML());
        writer.close();
      }
      catch(IOException e)
      {
        
        e.printStackTrace();
      }
    }
    else
    {
      System.out.println(ruleContext.getOutputModel().asXML());
    }
  }
}
