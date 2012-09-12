package org.achacha.rules.engine.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.dom4j.Element;

/**
 * Mapped loader Maps name to string data that is parsed into XML when requested
 */
public class RulesEngineLoaderImplMappedString extends RulesEngineLoader
{
  /** rules base prefix */
  public static final String RULES_BASE = "rules/";

  /** Mapped storage */
  private Map<String, String> mData = new HashMap<String, String>();

  /**
   * Create loader with factory
   * 
   * @param factory RuleEngineFactory
   */
  public RulesEngineLoaderImplMappedString(RulesEngineFactory factory)
  {
    super(factory);
  }

  /**
   * Add rule to data mapping Will add appropriate extension
   * 
   * @param name (or relative path) of rule without extension
   * @param data of rule
   */
  public void addRule(String name, String data)
  {
    mData.put(RULES_BASE + name, data);
  }

  /**
   * Add conditions to data mapping Will add appropriate extension
   * 
   * @param name (or relative path) of conditions without extension
   * @param data of conditions
   */
  public void addConditions(String name, String data)
  {
    mData.put(name, data);
  }

  /**
   * Add actions to data mapping Will add appropriate extension
   * 
   * @param name (or relative path) of actions without extension
   * @param data of actions
   */
  public void addActions(String name, String data)
  {
    mData.put(name, data);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getRuleStream(String name)
  {
    InputStream is = null;

    String xml = mData.get(RULES_BASE + name);
    if (null != xml)
    {
      is = new ByteArrayInputStream(xml.getBytes());
    }

    return is;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getConditionsStream(String name)
  {
    InputStream is = null;

    String xml = mData.get(name);
    if (null != xml)
    {
      is = new ByteArrayInputStream(xml.getBytes());
    }

    return is;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getActionsStream(String name)
  {
    InputStream is = null;

    String xml = mData.get(name);
    if (null != xml)
    {
      is = new ByteArrayInputStream(xml.getBytes());
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
