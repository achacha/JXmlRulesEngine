package org.achacha.rules.engine;

import java.io.InputStream;

import org.achacha.rules.cache.RuleCache;
import org.achacha.rules.engine.base.Rule;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.base.RuleExecDef;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.achacha.rules.engine.factory.CompareIf;
import org.achacha.rules.engine.statement.Statements;
import org.achacha.rules.engine.statement.StatementsElse;
import org.achacha.rules.engine.statement.StatementsThen;
import org.achacha.rules.engine.value.ValueBeanBacked;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Rules engine that contains rules by name
 */
public class RulesEngine
{
  /** Rules loader */
  protected RulesEngineLoader mLoader;

  /** Rule cache reference */
  protected RuleCache mRuleCache;

  /**
   * Create rules engine with specific loader
   * 
   * @param loader RulesEngineLoader
   */
  public RulesEngine(RulesEngineLoader loader)
  {
    mLoader = loader;
    mRuleCache = mLoader.getFactory().getRuleCache();
  }

  /** No default ctor */
  protected RulesEngine()
  {
  }

  /**
   * Get loader associated with this rules engine
   * 
   * @return RulesEngineLoader
   */
  public RulesEngineLoader getLoader()
  {
    return mLoader;
  }

  /**
   * Create a new <tt>RuleContext</tt> given a request model EventVisitor logging disabled by default
   * 
   * @see RuleContext for sample of request model
   * @param requestModel Element
   * @return <tt>RuleContext</tt>
   */
  public RuleContext createContext(Element requestModel)
  {
    return new RuleContext(mLoader, requestModel);
  }

  /**
   * Create a new <tt>RuleContext</tt> given a request model and <tt>EventModel</tt> logging level
   * 
   * @see RuleContext for sample of request model
   * @see EventVisitor for logging levels
   * @param requestModel <tt>Element</tt>
   * @param loggingLevel to use for events
   * @return <tt>RuleContext</tt>
   */
  public RuleContext createContext(Element requestModel, int loggingLevel)
  {
    RuleContext ruleContext = new RuleContext(mLoader, requestModel);
    if (-1 != loggingLevel)
    {
      // a_Manual override from query string
      ruleContext.getEvents().setLoggingLevel(loggingLevel);
    }
    return ruleContext;
  }

  /**
   * Will try to load the rule given a name and resolve dependencies, then execute the rule
   * 
   * @param ruleContext RuleContext
   * @param ruleName to execute
   */
  public void execute(RuleContext ruleContext, String ruleName)
  {
    Rule rule = getRule(ruleName);
    if (null == rule)
    {
      throw new RuntimeException("RulesEngine.execute: Rule named '" + ruleName + "' was not found in loader: " + ruleContext.getLoader());
    }
    // a_Execute rule
    ruleContext.setActiveRule(rule);
    executeInternal(ruleContext);
  }

  /**
   * Execute a rule by name
   * 
   * @param ruleContext RuleContext
   */
  public void execute(RuleContext ruleContext)
  {
    ruleContext.getLoader().getFactory().getProcessorFactory().executePreProcessors(ruleContext);
    for (RuleExecDef ruledef : ruleContext.getRulesToExecute())
    {
      if (ruleContext.getEvents().isInfoEnabled())
      {
        ruleContext.getTimer().set("RulesEngine.execute: " + ruledef + ": Initialize");
      }

      // a_Load rule
      Rule rule = getRule(ruledef.getName());
      if (null == rule)
      {
        throw new RuntimeException("RulesEngine.execute: Rule named '" + ruledef + "' was not found in loader: " + ruleContext.getLoader());
      }

      // a_Execute rule
      ruleContext.setActiveRule(rule);
      if (!executeInternal(ruleContext))
      {
        break;
      }
    }
    ruleContext.getLoader().getFactory().getProcessorFactory().executePostProcessors(ruleContext);
  }

  /**
   * Publish constant content specified in rules Prepares input model for local execution
   * 
   * @param ruleContext RuleContext
   */
  public void publishContentToInputModel(RuleContext ruleContext)
  {
    for (RuleExecDef ruledef : ruleContext.getRulesToExecute())
    {
      if (ruleContext.getEvents().isInfoEnabled())
      {
        ruleContext.getTimer().set("RulesEngine.publishContent: " + ruledef + ": Publishing content");
      }

      // a_Load rule
      Rule rule = getRule(ruledef.getName());
      if (null == rule)
      {
        throw new RuntimeException("RulesEngine.publishContent: Rule named '" + ruledef + "' was not found in loader: " + ruleContext.getLoader());
      }

      for (ValueBeanBacked value : rule.getConstantContentInputModelValues())
      {
        try
        {
          value.publish(null, ruleContext.getInputModel());
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }
    }
  }

  /**
   * Execute rule loaded into RuleContext
   * 
   * @param ruleContext RuleContext with already loaded Rule
   * @return true if to keep executing false to stop execution
   */
  protected boolean executeInternal(RuleContext ruleContext)
  {
    Rule rule = ruleContext.getActiveRule();
    if (null == rule)
    {
      throw new RuntimeException("RulesEngine.execute: Active Rule object has not been loaded on the RuleContext");
    }

    // a_Load dependencies
    String ruleName = rule.getName();
    rule.loadDependencies(ruleContext.getLoader());

    // a_Execute rule
    if (ruleContext.getEvents().isInfoEnabled())
    {
      ruleContext.getTimer().set("Execute: " + ruleName + ": execute");
    }
    boolean result = true;
    try
    {
      // a_Execute the rule
      ruleContext.getEvents().event("RulesEngine.execute: Executing rule START: " + rule.getName());
      if (rule.execute(ruleContext) && ruleContext.isStopOnFirstTrue())
      {
        ruleContext.getEvents().event("RulesEngine.execute: First true detected and request to stop was requested: " + rule.getName());
        result = false;

      }
      ruleContext.getEvents().event("RulesEngine.execute: Executing rule END: " + rule.getName());
    }
    catch (Throwable e)
    {
      ruleContext.getEvents().error(e);
      result = false;
    }
    if (ruleContext.getEvents().isInfoEnabled())
    {
      ruleContext.getTimer().reset();
    }
    return result;
  }

  /**
   * Load the rule which then MUST be initialized by the caller with RuleContext After calling this method, caller MUST call loadDependencies
   * 
   * @param ruleName which may contain partial path based on rule base
   * @return null if rule not found
   */
  public Rule loadRule(String ruleName)
  {
    Rule rule = null;
    InputStream fs = mLoader.getRuleStream(ruleName);
    if (null != fs)
    {
      Document docRule = RulesEngineHelper.loadXmlDocument(fs);
      rule = new Rule(ruleName);
      rule.parse(docRule.getRootElement(), mLoader.getFactory());
    }
    return rule;
  }

  /**
   * Load rule conditionals After calling this method, caller MUST call loadDependencies
   * 
   * @param conditionsPath relative path
   * @return null if conditions not found
   */
  public CompareIf loadConditions(String conditionsPath)
  {
    CompareIf compare = null;
    InputStream fs = mLoader.getConditionsStream(conditionsPath);
    if (null != fs)
    {
      Document docRule = RulesEngineHelper.loadXmlDocument(fs);
      compare = mLoader.getFactory().createCompareIf(docRule.getRootElement());
    }
    return compare;
  }

  /**
   * Load rule actions After calling this method, caller MUST call loadDependencies
   * 
   * @param actionsPath relative path
   * @return null if actions not found
   */
  public Statements loadActions(String actionsPath)
  {
    Statements actions = null;
    InputStream fs = mLoader.getActionsStream(actionsPath);
    if (null != fs)
    {
      Document docRule = RulesEngineHelper.loadXmlDocument(fs);
      Element acts = docRule.getRootElement();
      if (acts.getName().equals(StatementsThen.ELEMENT))
      {
        // Then
        actions = new StatementsThen();
        actions.parse(docRule.getRootElement(), mLoader.getFactory());
      }
      else if (acts.getName().equals(StatementsElse.ELEMENT))
      {
        // Else
        actions = new StatementsElse();
        actions.parse(docRule.getRootElement(), mLoader.getFactory());
      }
      else
      {
        // Unknown?
        throw new RuntimeException("Unknown actions type: " + acts.asXML());
      }
    }
    return actions;
  }

  /**
   * Load Rule object given the specified loader
   * 
   * @param name of the Rule
   * @return Rule
   */
  public synchronized Rule getRule(String name)
  {
    Rule rule = null;
    // a_'name' is the key
    String key = mLoader.getRuleCacheKey(name);
    if (null == key)
    {
      // a_Key is null, loader requires no caching
      rule = mLoader.getRule(name);
      if (null == rule)
      {
        throw new RuntimeException("Unable to find: " + name);
      }
      rule.loadDependencies(mLoader);
    }
    else
    {
      // a_Loader provided a key so check cache
      rule = (Rule) mRuleCache.get(key);

      // a_If not cached load and resolve dependencies
      if (null == rule)
      {
        rule = mLoader.getRule(name);
        if (null == rule)
        {
          throw new RuntimeException("Unable to find: " + name);
        }
        rule.loadDependencies(mLoader);
        mRuleCache.put(key, rule);
      }
    }

    return rule;
  }
}
