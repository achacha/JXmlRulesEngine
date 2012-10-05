package org.achacha.rules.tutorial;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.impl.RulesEngineLoaderImplMappedString;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author this
 *
 */
public class GettingStarted
{  
  /**
   * Main method that creates a rules engine and executes a simple rule
   * See the bottom of this file for the simple rule used 
   * @param args
   */
  public static void main(String[] args)
  {
    /*
     * Create a new loader with default factory.
     * 
     * A factory will create all rule components when loader requires them
     * Default engine is used that creates all the default constructs in:
     *   org.achacha.rules.compare
     *   org.achacha.rules.condition
     *   org.achacha.rules.action
     * 
     * RulesEngineFactoryExtension can be used to extend a default factory while keeping existing constructs
     *   for basic rules the default factory provides a wide variety of comparators and action elements
     * 
     * The loader uses the factory to resolve where the rule parts are loaded from
     * There are several load types (file system, in-memory string, in-memory XML)
     * Here we will configure a simple in-memory string based loader and add the
     * conditions and actions from constants
     */
    RulesEngineLoaderImplMappedString loader = new RulesEngineLoaderImplMappedString(new RulesEngineFactory());
    
    // Add a simple rule and call it 'alwaysTrue', we will execute this rule using this name
    loader.addRule("alwaysTrue", SIMPLE_RULE);
    
    /*
     * Create a rules engine that uses the loader and factory we just created
     * Normally these 3 objects would be someone retained so we don't have to re-parse the rules every execution
     * However this is a tutorial example
     */
    RulesEngine engine = new RulesEngine(loader);
    
    /////////////////////////// Everything above is initialization ///////////////////////////////////////
    
    /////////////////////////// Everything below is rule execution ///////////////////////////////////////
    /*
     * Now we need to have some input for the rule to process but since were are using constants in comparison we can just pass
     * an empty input model
     * 
     * We use dom4j XML Element for input
     * 
     * <request>
     *   <input />
     * </request>
     */
    Element request = DocumentHelper.createElement("request");
    request.addElement("input");
    
    /*
     * RuleContext is an object that holds the input, output, event log, etc for a given rule execution
     * The engine can create a new context for us if we specify the input for the rule
     */
    RuleContext ruleContext = engine.createContext(request);
    
    /*
     * Now that we have set up our context we can execute the rule
     * Since we used addRule above and called this rule 'alwaysTrue', this is how we invoke this rule
     * 
     * NOTE:
     *   you can run multiple rules sequentially against the same context
     *   some actions can modify input context and some conditions can check output context
     *   so rules can depend on other rules
     */
    engine.execute(ruleContext, "alwaysTrue");
    
    /*
     * Display the output model
     * 
     * Since the action we used is:
     * 
     *  <Action Operator='OutputValueSet'>"
     *     <Path>/result</Path>"
     *    <Value Source='Constant'><Data>yes</Data></Value>"
     * </Action>"
     * 
     * We get the following output model:
     * <output><result>yes</result></output>
     * 
     * NOTE: The path on the output action is always relative to the output element
     */
    System.out.println("Output Model\n------------");
    System.out.println(ruleContext.getOutputModel().asXML());
  }

  /*
   * Simple rule
   * 
   * Tests if two boolean constants of true are equal (which they always are)
   * Then if true (and it is true) sets a value on the output XML /result to 'true'
   * 
   * This is equivalent to (in pseudo code):
   *   if (true == true) {
   *     /result = 'true';
   *   }
   * 
   */
  
  private static final String SIMPLE_RULE = "" 
    + "<Rule>"
    + "  <Case>"
    + "    <If Operator='AND'>"
    + "      <Condition Operator='BooleanEquals'>"
    + "        <Value Source='Constant'>"
    + "          <Data>true</Data>"
    + "        </Value>"
    + "        <Value Source='Constant'>"
    + "          <Data>true</Data>"
    + "        </Value>"
    + "      </Condition>"
    + "    </If>"
    + "    <Then>"
    + "      <Action Operator='OutputValueSet'>"
    + "        <Path>/result</Path>"
    + "        <Value Source='Constant'><Data>yes</Data></Value>"
    + "      </Action>"
    + "    </Then>"
    + "  </Case>"
    + "</Rule>";
}
