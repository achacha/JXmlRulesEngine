package org.achacha.rules.engine;

import java.io.InputStream;

import org.achacha.rules.base.XmlEmittable;
import org.achacha.rules.engine.base.Rule;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.achacha.rules.engine.factory.CompareIf;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.statement.Statements;
import org.achacha.rules.util.EventTimer;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Rule object loader
 */
public abstract class RulesEngineLoader implements XmlEmittable
{
  /** XML element */
  public static final String ELEMENT = "Loader";

  /** Factory for creating rules and rule parts */
  protected RulesEngineFactory mFactory;

  /** Event visitor */
  protected EventVisitor mEventVisitor = new EventVisitor(EventVisitor.ERROR);

  /** Event timer */
  protected EventTimer mEventTimer = new EventTimer();

  /**
   * Construct loader with factory
   * 
   * @param factory RuleEngineFactory
   */
  public RulesEngineLoader(RulesEngineFactory factory)
  {
    this();
    mFactory = factory;
  }

  /**
   * Get the factory
   * 
   * @return RulesEngineFactory
   */
  public RulesEngineFactory getFactory()
  {
    return mFactory;
  }

  /**
   * Get event visitor for logging
   * 
   * @return EventVisitor
   */
  public EventVisitor getEventVisitor()
  {
    return mEventVisitor;
  }

  /**
   * Get event timer for performance logging
   * 
   * @return EventTimer
   */
  public EventTimer getEventTimer()
  {
    return mEventTimer;
  }

  /** No default ctor */
  private RulesEngineLoader()
  {
  }

  /**
   * Get rule object by name e.g. Rule rule = getRule("somepath/somesubpath/myrulename");
   * 
   * @param name (or relative path) of the rule
   * @return Rule associated with name or null if not found
   */
  public Rule getRule(String name)
  {
    assert (null != mFactory);

    if (mEventVisitor.isDebugEnabled())
    {
      mEventVisitor.debug("RulesEngineLoader: Trying to load rule: " + name);
    }

    Rule rule = null;
    InputStream is = getRuleStream(name);
    if (null != is)
    {
      Document doc = RulesEngineHelper.loadXmlDocument(is);
      rule = new Rule(name);
      rule.parse(doc.getRootElement(), mFactory);
    }
    else
    {
      mEventVisitor.error("RulesEngineLoader: Unable to load rule: " + name);
    }
    return rule;
  }

  /**
   * Get conditions
   * 
   * @param name (or relative path) of the conditions
   * @return CompareIf or null if not found
   */
  public CompareIf getConditions(String name)
  {
    assert (null != mFactory);

    if (mEventVisitor.isDebugEnabled())
    {
      mEventVisitor.debug("RulesEngineLoader: Trying to load condition: " + name);
    }

    CompareIf compareIf = null;
    InputStream is = getConditionsStream(name);
    if (null != is)
    {
      Document doc = RulesEngineHelper.loadXmlDocument(is);
      compareIf = mFactory.createCompareIf(doc.getRootElement());
    }
    else
    {
      mEventVisitor.error("RulesEngineLoader: Unable to load condition: " + name);
    }
    return compareIf;
  }

  /**
   * Get action statements
   * 
   * @param name (or relative path) of the actions
   * @return Statements or null if not found
   */
  public Statements getActions(String name)
  {
    assert (null != mFactory);

    if (mEventVisitor.isDebugEnabled())
    {
      mEventVisitor.debug("RulesEngineLoader: Trying to load action: " + name);
    }

    Statements statements = null;
    InputStream is = getActionsStream(name);
    if (null != is)
    {
      Document doc = RulesEngineHelper.loadXmlDocument(is);
      statements = new Statements();
      statements.parse(doc.getRootElement(), mFactory);
    }
    else
    {
      mEventVisitor.error("RulesEngineLoader: Unable to load action: " + name);
    }
    return statements;
  }

  /**
   * Get the InputStream for reading the rule directory This method will append the extension
   * 
   * @param name (or relative path) of the rule to load
   * @return InputStream to read the rule, null if not found
   */
  public abstract InputStream getRuleStream(String name);

  /**
   * Get the InputStream for reading the included comparisons directory This method will append the extension
   * 
   * @param name (or relative path) of conditions without extension
   * @return InputStream to read the rule, null if not found
   */
  public abstract InputStream getConditionsStream(String name);

  /**
   * Get the InputStream for reading the included actions directory This method will append the extension
   * 
   * @param name (or relative path) of actions without extension
   * @return InputStream to read the rule, null if not found
   */
  public abstract InputStream getActionsStream(String name);

  /**
   * Builds correct key for a given name Default is to use the name, loader specific 
   * 
   * NOTE: Returning null means that the loader does not want its data cached
   * 
   * @param name of the rule to generate the key for
   * @return String key for the cache or null if loader should be used every time to load data
   */
  public String getRuleCacheKey(String name)
  {
    return null;
  }

  /**
   * Gets the associated project scope for this loader
   * 
   * @return project scope
   */
  public String getProjectScope()
  {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return toElement(ELEMENT);
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element root = DocumentHelper.createElement(ELEMENT);

    root.addAttribute(ATTR_CLASS, getClass().getSimpleName());

    return root;
  }
}
