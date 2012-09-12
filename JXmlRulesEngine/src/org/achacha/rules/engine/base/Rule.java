package org.achacha.rules.engine.base;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.achacha.rules.base.JsonEmittable;
import org.achacha.rules.base.JsonParseable;
import org.achacha.rules.base.XmlSerializable;
import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.Case;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.achacha.rules.engine.statement.Statements;
import org.achacha.rules.engine.statement.StatementsElse;
import org.achacha.rules.engine.value.ValueBeanBacked;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Rule
 */
public class Rule implements XmlSerializable, Serializable, JsonEmittable, JsonParseable
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Current version */
  public static final String VERSION = String.valueOf(serialVersionUID);

  /** Element name */
  public static final String ELEMENT = "Rule";

  /** Name attribute */
  public static final String ATTR_NAME = "Name";

  /** Label attribute */
  public static final String ATTR_LABEL = "Label";

  /** Extension that the rule will have */
  public static final String RULE_EXTENSION = ".rule";

  /** Label */
  protected String mLabel;

  /** Path of the rule */
  protected String mRuleName;

  /** Alternative to use if rule name is not set */
  protected String mRuleFilename = "";

  /** Cases */
  protected List<Case> mCases = new LinkedList<Case>();

  /** Else actions if no Case is true */
  protected Statements mElse = new StatementsElse();

  /**
   * Ctor
   * 
   * @param filename of the rule to use if the rule doesn't have an explicit name
   */
  public Rule(String filename)
  {
    mRuleFilename = filename;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void parse(Element root, RulesEngineFactory factory)
  {
    assert root.getName().equals(ELEMENT) : "Element name must be '" + ELEMENT + "': " + root.asXML();

    // a_Make sure the version being executed is current
    if (!root.attributeValue(ATTR_VERSION).equals(VERSION))
    {
      throw new RuntimeException("Version attribute mismatch, expected '" + VERSION + "' in: " + root.asXML());
    }

    // Name
    mRuleName = root.attributeValue(ATTR_NAME);

    // Label
    mLabel = StringUtils.defaultString(root.attributeValue(ATTR_LABEL), StringUtils.EMPTY);

    // Cases
    List<Element> cases = root.elements(Case.ELEMENT);
    for (Element e : cases)
    {
      Case c = new Case();
      c.parse(e, factory);
      mCases.add(c);
    }

    // Else
    Element e = root.element(StatementsElse.ELEMENT);
    if (null != e)
    {
      mElse.parse(e, factory);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader)
  {
    for (Case c : mCases)
    {
      c.loadDependencies(loader, new HashSet<String>());
    }
    mElse.loadDependencies(loader, new HashSet<String>());
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
    Element e = DocumentHelper.createElement(elementName);

    if (null != mRuleName && mRuleName.length() > 0)
    {
      e.addAttribute(ATTR_NAME, mRuleName);
    }
    if (null != mLabel && mLabel.length() > 0)
    {
      e.addAttribute(ATTR_LABEL, mLabel);
    }

    for (Case c : mCases)
    {
      e.add(c.toElement());
    }
    e.add(mElse.toElement());

    return e;
  }

  /**
   * Execute the rule and process THEN/ELSE as specified
   * 
   * @param ruleContext RuleContext
   * @return true if THEN false if ELSE
   */
  public boolean execute(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("Rule(" + getName() + ") executing");
    }

    // a_Check all cases and if all fail execute the else
    int i = 0;
    for (Case c : mCases)
    {
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("Rule(" + getName() + "), executing case #" + i);
      }
      if (c.execute(ruleContext))
      {
        // a_Case handled
        return true;
      }
      ++i;
    }

    // a_If we are here all cases evaluated to false
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("Rule(" + getName() + "), all cases were false, processing ELSE actions");
    }
    mElse.execute(ruleContext);
    return false;
  }

  /**
   * Rule name (or path)
   * 
   * @return Rule name
   */
  public String getName()
  {
    if (null == mRuleName)
    {
      return mRuleFilename;
    }

    return mRuleName;
  }

  /**
   * Rule label (human readable and descriptive)
   * 
   * @return Label of the rule
   */
  public String getLabel()
  {
    return mLabel;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("Rule(");
    buf.append(getName());
    buf.append(',');
    buf.append(StringUtils.defaultString(mLabel, "none"));
    buf.append(") {\r\n");
    for (Case c : mCases)
    {
      buf.append(c);
      buf.append("\r\n");
    }
    buf.append(mElse);
    buf.append("\r\n}");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("Rule( name=");
    buf.append(null == mRuleName ? "null" : mRuleName);
    buf.append(" label=");
    buf.append(null == mLabel ? "null" : mLabel);
    buf.append(" filename=");
    buf.append(null == mRuleFilename ? "null" : mRuleFilename);

    buf.append(") {\r\n");
    for (Case c : mCases)
    {
      buf.append(c.toStringResolved());
      buf.append("\r\n");
    }
    buf.append(mElse.toStringResolved());
    buf.append("\r\n}");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();

    obj.put(ATTR_NAME, mRuleName);
    obj.put(ATTR_LABEL, mLabel);

    JSONArray array = new JSONArray();
    for (Case c : mCases)
    {
      array.put(c.toJson());
    }
    obj.put(Case.ELEMENT, array);

    obj.put(StatementsElse.ELEMENT, mElse.toJson());

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mRuleName = StringUtils.defaultString(source.getString(ATTR_NAME), StringUtils.EMPTY);
    mLabel = StringUtils.defaultString(source.getString(ATTR_LABEL), StringUtils.EMPTY);

    mCases.clear();
    JSONArray array = source.getJSONArray(Case.ELEMENT);
    for (int i = 0; i < array.length(); ++i)
    {
      Case c = new Case();
      c.fromJson(array.getJSONObject(i), factory);
      mCases.add(c);
    }

    mElse.fromJson(source.getJSONObject(StatementsElse.ELEMENT), factory);
  }

  /**
   * Get all input values
   * 
   * @return List of input values
   */
  public Collection<ValueBeanBacked> getBeanBackedInputModelValues()
  {
    Map<String, ValueBeanBacked> values = new HashMap<String, ValueBeanBacked>();
    for (Case c : mCases)
    {
      for (Value value : c.getIf().getValues())
      {
        if (value instanceof ValueBeanBacked)
        {
          ValueBeanBacked bbValue = (ValueBeanBacked) value;
          values.put(bbValue.getInputModelXpath(), bbValue);
        }
      }
    }

    return values.values();
  }

  /**
   * Get all input values
   * 
   * @return List of input values
   */
  public Collection<ValueBeanBacked> getConstantContentInputModelValues()
  {
    Map<String, ValueBeanBacked> values = new HashMap<String, ValueBeanBacked>();
    for (Case c : mCases)
    {
      for (Value value : c.getIf().getValues())
      {
        if (value instanceof ValueBeanBacked)
        {
          ValueBeanBacked bbValue = (ValueBeanBacked) value;
          if (null != bbValue.getContent())
          {
            // Add only values that have constant content
            values.put(bbValue.getInputModelXpath(), bbValue);
          }
        }
      }
    }

    return values.values();
  }
}
