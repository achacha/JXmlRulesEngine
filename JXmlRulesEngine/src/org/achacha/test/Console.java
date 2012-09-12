package org.achacha.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.impl.RulesEngineLoaderImplMappedString;
import org.achacha.rules.util.EventVisitor;
import org.apache.log4j.PropertyConfigurator;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class Console
{
  /**
   * Rules engine based on file system loader in the config Lazy init during call to getter
   */
  private RulesEngine mRulesEngine;

  private RulesEngineLoader mLoader;

  Console()
  {
    initLog4j();
    mFactory = new RulesEngineFactory();
    mLoader = getNewLocalLoader();
    mRulesEngine = new RulesEngine(mLoader);
  }

  /**
   * @param args
   */
  public static void main(String[] args)
  {
    Console me = new Console();
    me.doit();
  }

  public void doit()
  {
    RuleContext ruleContext = getNewRuleContext(mLoader);
    mRulesEngine.execute(ruleContext, SIMPLE_RULE_NAME);
    System.out.println(ruleContext);
  }

  /**
   * Initialize Log4j
   */
  private static void initLog4j()
  {
    File file = new File("etc/log4j.properties");
    if (!file.exists())
    {
      throw new RuntimeException("File not found: log4j.properties");
    }

    try
    {
      FileInputStream fis = new FileInputStream(file);
      Properties properties = new Properties();

      properties.load(fis);
      PropertyConfigurator.configure(properties);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to load log4j.properties", e);
    }
  }

  /** factory */
  protected RulesEngineFactory mFactory;

  /**
   * Build loader for testing
   * 
   * @return RulesEngineLoaderImplMapped
   */
  private RulesEngineLoaderImplMappedString getNewLocalLoader()
  {
    RulesEngineLoaderImplMappedString loader = new RulesEngineLoaderImplMappedString(mFactory);

    loader.addConditions(EXT_COND_NAME, EXT_COND_DATA);
    loader.addActions(EXT_ACT_NAME, EXT_ACT_DATA);
    loader.addRule(SIMPLE_RULE_NAME, SIMPLE_RULE);

    return loader;
  }

  /** input model */
  private static final String INPUT_MODEL = "<?xml version='1.0' encoding='utf-8'?><request><input><user><age>40</age></user></input></request>";

  /**
   * Get new context for executing the rule
   * 
   * @param loader to use
   * @return RuleContext
   */
  private RuleContext getNewRuleContext(RulesEngineLoader loader)
  {
    Document requestModel;
    RuleContext ruleContext = null;
    try
    {
      requestModel = DocumentHelper.parseText(INPUT_MODEL);
      ruleContext = new RuleContext(loader, requestModel.getRootElement());
      ruleContext.getEvents().setLoggingLevel(EventVisitor.DEBUG);
    }
    catch (DocumentException e)
    {
      throw new RuntimeException("Unable to parse XML: " + INPUT_MODEL, e);
    }

    return ruleContext;
  }

  /** Test local loader */
  public void testLoadLoader()
  {
    RulesEngineLoader loader = getNewLocalLoader();
    RuleContext ruleContext = getNewRuleContext(loader);
    RulesEngine engine = new RulesEngine(loader);

    engine.execute(ruleContext, SIMPLE_RULE_NAME);
    System.out.println("PASSED: Local loader execution");
  }

  // @formatter:off
  /** external condition */
  private static final String EXT_COND_NAME = "Conditions/cond1.conditions";

  /** external condition data */
  private static final String EXT_COND_DATA = 
"<?xml version='1.0' encoding='utf-8'?>" + 
"  <If Operator='AND' Version='1'>" + 
"    <Condition Operator='Exists'>" +
"      <Value Source='InputModel'>" + 
"        <Path>/input/user/age</Path>" + 
"        <Bean Object='context'><Path>session.user.age</Path></Bean>" + 
"      </Value>" + "    </Condition>" + 
"    <Condition Operator='NumberGreaterThan'>" + 
"      <Value Source='InputModel'>" + 
"        <Path>/input/user/age</Path>" + 
"        <Bean Object='context'><Path>session.user.age</Path></Bean>" + 
"      </Value>" + 
"      <Value Source='Constant'><Data>39</Data></Value>" + 
"    </Condition>" + 
"    <Condition Operator='NumberLessThan'>" + 
"      <Value Source='InputModel'>" + 
"        <Path>/input/user/age</Path>" + 
"        <Bean Object='context'><Path>session.user.age</Path></Bean>" + 
"      </Value>" + 
"      <Value Source='Constant'><Data>41</Data></Value>" + 
"    </Condition>" + 
"  </If>";

  /** external action */
  private static final String EXT_ACT_NAME = "Actions/ElseHappened.actions";

  /** external action data */
  private static final String EXT_ACT_DATA = 
"<?xml version='1.0' encoding='utf-8'?>" + 
"  <Actions Version='1'>" + 
"    <Action Operator='OutputValueAdd' Name='rezrov'>" + 
"      <Path>door</Path>" + 
"      <Value Source='Constant'><Data>open</Data></Value>" + 
"    </Action>" + 
"  </Actions>";

  /** simple rule name */
  private static final String SIMPLE_RULE_NAME = "SimpleRule.rule";

  /** simple rule */
  private static final String SIMPLE_RULE = 
"<?xml version='1.0' encoding='utf-8'?>" + 
"<Rule Label='Simple rule' Version='1'>" + 
"  <Case>" + 
"    <If Operator='AND' Name='Age/Test'>" + 
"      <Condition Operator='Exists'>" + 
"        <Value Source='InputModel'>" + 
"          <Path>/input/user/age</Path>" + 
"          <Bean Object='context'><Path>session.user.age</Path></Bean>" + 
"        </Value>" + 
"      </Condition>" + 
"      <Condition Operator='StringGreaterThan'>" + 
"        <Value Source='InputModel'>" + 
"          <Path>/input/user/age</Path>" + 
"          <Bean Object='context'><Path>session.user.age</Path></Bean>" + 
"        </Value>" + 
"        <Value Source='Constant'><Data>1</Data></Value>" + 
"      </Condition>" + 
"      <Condition Operator='Include'>" + 
"        <Path>Conditions/cond1.conditions</Path>" + 
"      </Condition>" + 
"    </If>" + 
"    <Then>" + 
"      <Action Operator='OutputValueAdd' Name='one'>" + 
"        <Path>user/agestate</Path>" + 
"       <Value Source='Constant'><Data>something old</Data></Value>" + 
"      </Action>" + 
"      <Action Operator='OutputValueSet' Name='two'>" + 
"        <Path>user/disposition</Path>" + 
"        <Value Source='Constant'><Data>something new</Data></Value>" + 
"      </Action>" + 
"    </Then>" + 
"  </Case>" + 
"  <Else>" + 
"    <Action Operator='Include'>" + 
"      <Path>Actions/ElseHappened.actions</Path>" + 
"    </Action>" + 
"  </Else>" + 
"</Rule>";
}
