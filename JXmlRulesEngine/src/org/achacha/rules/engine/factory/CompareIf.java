package org.achacha.rules.engine.factory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.Compare;
import org.achacha.rules.engine.compare.CompareIfAnd;
import org.achacha.rules.engine.compare.CompareIfOr;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Condition group
 */
public abstract class CompareIf implements Compare
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Current version */
  public static final String VERSION = String.valueOf(serialVersionUID);

  /** Element name */
  public static final String ELEMENT = "If";

  /** Optional name */
  protected String mName;

  /** Map CompareIf type to implementation */
  private static HashMap<String, String> mConditionGroupClasses = new HashMap<String, String>();

  static
  {
    mConditionGroupClasses.put(CompareIfAnd.TYPE, CompareIfAnd.class.getName());
    mConditionGroupClasses.put(CompareIfOr.TYPE, CompareIfOr.class.getName());
  }

  /** List of comparisons */
  protected List<Compare> mComparisons = new LinkedList<Compare>();

  /** Invert the result */
  protected boolean mNot = false;

  /**
   * Accessed by the factory during initialization
   * 
   * @return map of type to class path string
   */
  static Map<String, String> getClassMap()
  {
    return mConditionGroupClasses;
  }

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
    for (Compare compare : mComparisons)
    {
      compare.loadDependencies(loader, loaded);
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings(value = { "unchecked" })
  public void parse(Element root, RulesEngineFactory factory)
  {
    mComparisons.clear();
    if (null == root)
    {
      mName = StringUtils.EMPTY;
      mNot = Boolean.FALSE.booleanValue();
      return;
    }
    mName = StringUtils.defaultString(root.attributeValue(ATTR_NAME), StringUtils.EMPTY);
    mNot = Boolean.parseBoolean(StringUtils.defaultString(root.attributeValue(ATTR_NOT), Boolean.FALSE.toString()));

    List<Element> ifs = root.elements(CompareIf.ELEMENT);
    for (Element anif : ifs)
    {
      Compare c = (Compare) factory.create(anif);
      if (null != c)
      {
        mComparisons.add(c);
      }
    }

    List<Element> compares = root.elements(CompareType.ELEMENT);
    for (Element comp : compares)
    {
      Compare c = (Compare) factory.create(comp);
      if (null != c)
      {
        mComparisons.add(c);
      }
    }
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
    e.addAttribute(ATTR_NAME, mName);
    e.addAttribute(ATTR_OPERATOR, getType());

    // a_Comparissons contained
    for (Compare compare : mComparisons)
    {
      e.add(compare.toElement());
    }

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("If(");
    buf.append(getType());
    buf.append(" '");
    buf.append(mName);
    buf.append("'");
    if (mNot)
    {
      buf.append(",");
      buf.append("NOT");
    }
    buf.append("){\r\n");
    for (Compare compare : mComparisons)
    {
      buf.append(" ");
      buf.append(compare);
      buf.append("\r\n");
    }
    buf.append("}");

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("If(");
    buf.append(getType());
    buf.append(" '");
    buf.append(mName);
    buf.append("'");
    if (mNot)
    {
      buf.append(",");
      buf.append("NOT");
    }
    buf.append("){\r\n");
    for (Compare compare : mComparisons)
    {
      buf.append(" ");
      buf.append(compare.toStringResolved());
      buf.append("\r\n");
    }
    buf.append("}");

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();
    obj.put(ATTR_OBJECT, ELEMENT);
    obj.put(ATTR_NAME, mName);
    obj.put(ATTR_OPERATOR, getType());
    obj.put(ATTR_NOT, mNot);

    JSONArray array = new JSONArray();
    for (Compare compare : mComparisons)
    {
      array.put(compare.toJson());
    }
    obj.put(CompareType.ELEMENT, array);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mComparisons.clear();
    if (null == source)
    {
      mName = StringUtils.EMPTY;
      mNot = Boolean.FALSE.booleanValue();
      return;
    }

    assert source.getString(ATTR_OPERATOR).equals(getType()) : "Invalid type " + ATTR_OPERATOR + "=" + source.getString(ATTR_OPERATOR) + " expected " + getType();

    mName = source.getString(ATTR_NAME);
    mNot = source.getBoolean(ATTR_NOT);

    JSONArray array = source.getJSONArray(CompareType.ELEMENT);
    for (int i = 0; i < array.length(); ++i)
    {
      JSONObject obj = array.getJSONObject(i);
      Compare compare = (Compare) factory.create(obj.getString(ATTR_OBJECT), obj.getString(ATTR_OPERATOR));
      if (null != compare)
      {
        compare.fromJson(obj, factory);
        mComparisons.add(compare);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public List<Value> getValues()
  {
    List<Value> values = new LinkedList<Value>();

    for (Compare compare : mComparisons)
    {
      values.addAll(compare.getValues());
    }
    return values;
  }
}
