package org.achacha.rules.engine.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.Rule;
import org.achacha.rules.engine.factory.CompareIf;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.statement.Statements;
import org.dom4j.Element;

/**
 * Mapped loader Stores parsed XML
 */
public class RulesEngineLoaderImplMappedXml extends RulesEngineLoader
{
  /** Allows to manually insert files */
  public static final String ELEMENT_FILE = "File";

  /** File type to insert */
  public static final String ATTR_TYPE = "Type";

  /** Type of the file that goes into the rules area */
  public static final String TYPE_RULES = "rules";

  /** Type of the file that goes into the actions area */
  public static final String TYPE_ACTIONS = "actions";

  /** Type of the file that goes into the conditions area */
  public static final String TYPE_CONDITIONS = "conditions";

  /** rules base prefix */
  public static final String RULES_BASE = "rules/";

  /** actions base prefix */
  public static final String ACTIONS_BASE = "actions/";

  /** conditions base prefix */
  public static final String CONDITIONS_BASE = "conditions/";

  /** Mapped storage */
  private Map<String, Element> mData = new HashMap<String, Element>();

  /**
   * Create loader with factory
   * 
   * @param factory RuleEngineFactory
   */
  public RulesEngineLoaderImplMappedXml(RulesEngineFactory factory)
  {
    super(factory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Statements getActions(String name)
  {
    Element root = mData.get(ACTIONS_BASE + name);
    Statements statements = null;
    if (null != root)
    {
      statements = new Statements();
      statements.parse(root, mFactory);
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load rule: " + ACTIONS_BASE + name);
    }
    return statements;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompareIf getConditions(String name)
  {
    Element root = mData.get(CONDITIONS_BASE + name);
    CompareIf compareIf = null;
    if (null != root)
    {
      compareIf = mFactory.createCompareIf(root);
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load conditions: " + CONDITIONS_BASE + name);
    }
    return compareIf;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Rule getRule(String name)
  {
    Rule rule = null;
    Element root = mData.get(RULES_BASE + name);
    if (null != root)
    {
      rule = new Rule(name);
      rule.parse(root, mFactory);
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load rule: " + name);
    }
    return rule;
  }

  /**
   * Add rule to data mapping
   * 
   * @param name (or relative path) of rule without extension
   * @param data Element of rule
   */
  public void addRule(String name, Element data)
  {
    mData.put(RULES_BASE + name, data);
  }

  /**
   * Add conditions to data mapping Will add appropriate extension
   * 
   * @param name (or relative path) of conditions without extension
   * @param data Element of conditions
   */
  public void addConditions(String name, Element data)
  {
    mData.put(CONDITIONS_BASE + name, data);
  }

  /**
   * Add actions to data mapping Will add appropriate extension
   * 
   * @param name (or relative path) of actions without extension
   * @param data Element of actions
   */
  public void addActions(String name, Element data)
  {
    mData.put(ACTIONS_BASE + name, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getRuleStream(String name)
  {
    Element root = mData.get(RULES_BASE + name);
    ByteArrayInputStream is = null;
    if (null != root)
    {
      String xml = root.asXML();
      is = new ByteArrayInputStream(xml.getBytes());
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load rule: " + RULES_BASE + name);
    }
    return is;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getConditionsStream(String name)
  {
    Element root = mData.get(CONDITIONS_BASE + name);
    ByteArrayInputStream is = null;
    if (null != root)
    {
      String xml = root.asXML();
      is = new ByteArrayInputStream(xml.getBytes());
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load condition: " + CONDITIONS_BASE + name);
    }
    return is;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getActionsStream(String name)
  {
    Element root = mData.get(ACTIONS_BASE + name);
    ByteArrayInputStream is = null;
    if (null != root)
    {
      String xml = root.asXML();
      is = new ByteArrayInputStream(xml.getBytes());
    }
    else
    {
      mEventVisitor.error("RulesEngineLoaderImplMappedXml: Unable to load action: " + ACTIONS_BASE + name);
    }
    return is;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement()
  {
    return toElement(ELEMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element root = super.toElement(elementName);
    for (String key : mData.keySet())
    {
      root.addElement("Data").addAttribute("Key", key);
    }

    return root;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append("{");
    for (String key : mData.keySet())
    {
      builder.append(key);
      builder.append(",");
    }
    builder.append("}");
    return builder.toString();
  }
}
