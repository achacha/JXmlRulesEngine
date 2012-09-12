package org.achacha.rules.engine.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.Compare;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.condition.CompareTypeBooleanEquals;
import org.achacha.rules.engine.condition.CompareTypeBooleanFalse;
import org.achacha.rules.engine.condition.CompareTypeBooleanNotEquals;
import org.achacha.rules.engine.condition.CompareTypeBooleanTrue;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringContainedIn;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringContains;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringNotContainedIn;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringNotContains;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringNotRegExp;
import org.achacha.rules.engine.condition.CompareTypeCollectionStringRegExp;
import org.achacha.rules.engine.condition.CompareTypeDateAfter;
import org.achacha.rules.engine.condition.CompareTypeDateBefore;
import org.achacha.rules.engine.condition.CompareTypeDateBetween;
import org.achacha.rules.engine.condition.CompareTypeDateEquals;
import org.achacha.rules.engine.condition.CompareTypeDateNotAfter;
import org.achacha.rules.engine.condition.CompareTypeDateNotBefore;
import org.achacha.rules.engine.condition.CompareTypeDateNotBetween;
import org.achacha.rules.engine.condition.CompareTypeDateNotEquals;
import org.achacha.rules.engine.condition.CompareTypeExists;
import org.achacha.rules.engine.condition.CompareTypeInclude;
import org.achacha.rules.engine.condition.CompareTypeNotExists;
import org.achacha.rules.engine.condition.CompareTypeNumberBetweenExclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberBetweenInclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberDialExclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberDialInclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberEquals;
import org.achacha.rules.engine.condition.CompareTypeNumberGreaterThan;
import org.achacha.rules.engine.condition.CompareTypeNumberGreaterThanOrEquals;
import org.achacha.rules.engine.condition.CompareTypeNumberLessThan;
import org.achacha.rules.engine.condition.CompareTypeNumberLessThanOrEquals;
import org.achacha.rules.engine.condition.CompareTypeNumberNotBetweenExclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberNotBetweenInclusive;
import org.achacha.rules.engine.condition.CompareTypeNumberNotEquals;
import org.achacha.rules.engine.condition.CompareTypeNumberNotGreaterThan;
import org.achacha.rules.engine.condition.CompareTypeNumberNotGreaterThanOrEquals;
import org.achacha.rules.engine.condition.CompareTypeNumberNotLessThan;
import org.achacha.rules.engine.condition.CompareTypeNumberNotLessThanOrEquals;
import org.achacha.rules.engine.condition.CompareTypeStringContains;
import org.achacha.rules.engine.condition.CompareTypeStringEndsWith;
import org.achacha.rules.engine.condition.CompareTypeStringEquals;
import org.achacha.rules.engine.condition.CompareTypeStringGreaterThan;
import org.achacha.rules.engine.condition.CompareTypeStringLessThan;
import org.achacha.rules.engine.condition.CompareTypeStringNotContains;
import org.achacha.rules.engine.condition.CompareTypeStringNotEndsWith;
import org.achacha.rules.engine.condition.CompareTypeStringNotEquals;
import org.achacha.rules.engine.condition.CompareTypeStringNotGreaterThan;
import org.achacha.rules.engine.condition.CompareTypeStringNotLessThan;
import org.achacha.rules.engine.condition.CompareTypeStringNotRegExp;
import org.achacha.rules.engine.condition.CompareTypeStringNotStartsWith;
import org.achacha.rules.engine.condition.CompareTypeStringRegExp;
import org.achacha.rules.engine.condition.CompareTypeStringStartsWith;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Item
 */
public abstract class CompareType implements Compare
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element name */
  public static final String ELEMENT = "Condition";

  /** operation attribute */
  public static final String ATTR_IGNORE_CASE = "IgnoreCase";

  /** values expected */
  public static final String ATTR_VALUE_COUNT = "ValueCount";

  /** values expected */
  public static final String ATTR_VALUE_TYPE = "ValueType";

  /** If case should be ignored */
  protected boolean mIgnoreCase = false;

  /** Invert the result */
  protected boolean mNot = false;

  /** List of values associated with this compare */
  protected ArrayList<Value> mValues = new ArrayList<Value>(2);

  /** Map action type to implementation */
  private static HashMap<String, String> mCompareClasses = new HashMap<String, String>();

  static
  {
    mCompareClasses.put(CompareTypeInclude.TYPE, CompareTypeInclude.class.getName());

    mCompareClasses.put(CompareTypeExists.TYPE, CompareTypeExists.class.getName());
    mCompareClasses.put(CompareTypeNotExists.TYPE, CompareTypeNotExists.class.getName());

    mCompareClasses.put(CompareTypeStringRegExp.TYPE, CompareTypeStringRegExp.class.getName());
    mCompareClasses.put(CompareTypeStringEquals.TYPE, CompareTypeStringEquals.class.getName());
    mCompareClasses.put(CompareTypeStringContains.TYPE, CompareTypeStringContains.class.getName());
    mCompareClasses.put(CompareTypeStringStartsWith.TYPE, CompareTypeStringStartsWith.class.getName());
    mCompareClasses.put(CompareTypeStringEndsWith.TYPE, CompareTypeStringEndsWith.class.getName());
    mCompareClasses.put(CompareTypeStringGreaterThan.TYPE, CompareTypeStringGreaterThan.class.getName());
    mCompareClasses.put(CompareTypeStringLessThan.TYPE, CompareTypeStringLessThan.class.getName());
    mCompareClasses.put(CompareTypeStringNotRegExp.TYPE, CompareTypeStringNotRegExp.class.getName());
    mCompareClasses.put(CompareTypeStringNotEquals.TYPE, CompareTypeStringNotEquals.class.getName());
    mCompareClasses.put(CompareTypeStringNotContains.TYPE, CompareTypeStringNotContains.class.getName());
    mCompareClasses.put(CompareTypeStringNotStartsWith.TYPE, CompareTypeStringNotStartsWith.class.getName());
    mCompareClasses.put(CompareTypeStringNotEndsWith.TYPE, CompareTypeStringNotEndsWith.class.getName());
    mCompareClasses.put(CompareTypeStringNotGreaterThan.TYPE, CompareTypeStringNotGreaterThan.class.getName());
    mCompareClasses.put(CompareTypeStringNotLessThan.TYPE, CompareTypeStringNotLessThan.class.getName());

    mCompareClasses.put(CompareTypeCollectionStringContains.TYPE, CompareTypeCollectionStringContains.class.getName());
    mCompareClasses.put(CompareTypeCollectionStringContainedIn.TYPE, CompareTypeCollectionStringContainedIn.class.getName());
    mCompareClasses.put(CompareTypeCollectionStringRegExp.TYPE, CompareTypeCollectionStringRegExp.class.getName());
    mCompareClasses.put(CompareTypeCollectionStringNotContains.TYPE, CompareTypeCollectionStringNotContains.class.getName());
    mCompareClasses.put(CompareTypeCollectionStringNotContainedIn.TYPE, CompareTypeCollectionStringNotContainedIn.class.getName());
    mCompareClasses.put(CompareTypeCollectionStringNotRegExp.TYPE, CompareTypeCollectionStringNotRegExp.class.getName());

    mCompareClasses.put(CompareTypeNumberEquals.TYPE, CompareTypeNumberEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberGreaterThan.TYPE, CompareTypeNumberGreaterThan.class.getName());
    mCompareClasses.put(CompareTypeNumberGreaterThanOrEquals.TYPE, CompareTypeNumberGreaterThanOrEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberLessThan.TYPE, CompareTypeNumberLessThan.class.getName());
    mCompareClasses.put(CompareTypeNumberLessThanOrEquals.TYPE, CompareTypeNumberLessThanOrEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberBetweenExclusive.TYPE, CompareTypeNumberBetweenExclusive.class.getName());
    mCompareClasses.put(CompareTypeNumberBetweenInclusive.TYPE, CompareTypeNumberBetweenInclusive.class.getName());
    mCompareClasses.put(CompareTypeNumberNotEquals.TYPE, CompareTypeNumberNotEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberNotGreaterThan.TYPE, CompareTypeNumberNotGreaterThan.class.getName());
    mCompareClasses.put(CompareTypeNumberNotGreaterThanOrEquals.TYPE, CompareTypeNumberNotGreaterThanOrEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberNotLessThan.TYPE, CompareTypeNumberNotLessThan.class.getName());
    mCompareClasses.put(CompareTypeNumberNotLessThanOrEquals.TYPE, CompareTypeNumberNotLessThanOrEquals.class.getName());
    mCompareClasses.put(CompareTypeNumberNotBetweenExclusive.TYPE, CompareTypeNumberNotBetweenExclusive.class.getName());
    mCompareClasses.put(CompareTypeNumberNotBetweenInclusive.TYPE, CompareTypeNumberNotBetweenInclusive.class.getName());

    mCompareClasses.put(CompareTypeNumberDialExclusive.TYPE, CompareTypeNumberDialExclusive.class.getName());
    mCompareClasses.put(CompareTypeNumberDialInclusive.TYPE, CompareTypeNumberDialInclusive.class.getName());

    mCompareClasses.put(CompareTypeBooleanEquals.TYPE, CompareTypeBooleanEquals.class.getName());
    mCompareClasses.put(CompareTypeBooleanNotEquals.TYPE, CompareTypeBooleanNotEquals.class.getName());
    mCompareClasses.put(CompareTypeBooleanTrue.TYPE, CompareTypeBooleanTrue.class.getName());
    mCompareClasses.put(CompareTypeBooleanFalse.TYPE, CompareTypeBooleanFalse.class.getName());

    mCompareClasses.put(CompareTypeDateAfter.TYPE, CompareTypeDateAfter.class.getName());
    mCompareClasses.put(CompareTypeDateBefore.TYPE, CompareTypeDateBefore.class.getName());
    mCompareClasses.put(CompareTypeDateBetween.TYPE, CompareTypeDateBetween.class.getName());
    mCompareClasses.put(CompareTypeDateEquals.TYPE, CompareTypeDateEquals.class.getName());
    mCompareClasses.put(CompareTypeDateNotAfter.TYPE, CompareTypeDateNotAfter.class.getName());
    mCompareClasses.put(CompareTypeDateNotBefore.TYPE, CompareTypeDateNotBefore.class.getName());
    mCompareClasses.put(CompareTypeDateNotBetween.TYPE, CompareTypeDateNotBetween.class.getName());
    mCompareClasses.put(CompareTypeDateNotEquals.TYPE, CompareTypeDateNotEquals.class.getName());
  }

  /**
   * Accessed by the factory during initialization
   * 
   * @return map of type to class path string
   */
  static Map<String, String> getClassMap()
  {
    return mCompareClasses;
  }

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
  }

  /**
   * Evaluate comparison
   * 
   * @param ruleContext RuleContext
   * @return true if evaluates to true
   */
  public abstract boolean evaluate(RuleContext ruleContext);

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings(value = { "unchecked" })
  public void parse(Element root, RulesEngineFactory factory)
  {
    // a_Attributes
    mIgnoreCase = Boolean.parseBoolean(StringUtils.defaultString(root.attributeValue(ATTR_IGNORE_CASE), Boolean.FALSE.toString()));
    mNot = Boolean.parseBoolean(StringUtils.defaultString(root.attributeValue(ATTR_NOT), Boolean.FALSE.toString()));

    // a_Values
    for (Element e : (List<Element>) root.elements(Value.ELEMENT))
    {
      mValues.add(factory.createValue(e));
    }
  }

  /**
   * Clone object
   * 
   * @param factory RulesEngineFactory
   * @return CompareType
   */
  public CompareType clone(RulesEngineFactory factory)
  {
    CompareType clone = factory.createCompareType(toElement());
    return clone;
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element e = DocumentHelper.createElement(elementName);

    e.addAttribute(CompareType.ATTR_OPERATOR, getType());
    if (mIgnoreCase)
    {
      e.addAttribute(ATTR_IGNORE_CASE, Boolean.TRUE.toString());
    }
    if (mNot)
    {
      e.addAttribute(ATTR_NOT, Boolean.TRUE.toString());
    }
    addValues(e);

    return e;
  }

  /**
   * Values added to the element root
   * 
   * @param root Element
   */
  protected void addValues(Element root)
  {
    for (Value value : mValues)
    {
      root.add(value.toElement());
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
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("  ");
    buf.append(ELEMENT);
    buf.append("(");
    buf.append(getType());
    if (mNot)
    {
      buf.append(",NOT");
    }
    if (mIgnoreCase)
    {
      buf.append(",NOCASE");
    }
    buf.append(")  { \r\n  ");
    for (Value value : mValues)
    {
      buf.append("  ");
      buf.append(value.toString());
      buf.append("\r\n");
    }
    buf.append("\r\n  }");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    return toString();
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();
    obj.put(ATTR_OBJECT, ELEMENT);
    obj.put(ATTR_OPERATOR, getType());
    obj.put(ATTR_NOT, mNot);
    obj.put(ATTR_IGNORE_CASE, mIgnoreCase);

    JSONArray array = new JSONArray();
    for (Value value : mValues)
    {
      array.put(value.toJson());
    }
    obj.put(Value.ELEMENT, array);

    // a_These are only used in toJson, we don't care about them in fromJson because they are always constant for a given class
    obj.put(ATTR_VALUE_COUNT, getExpectedValueCount());
    obj.put(ATTR_VALUE_TYPE, getExpectedValueType());

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    assert source.getString(ATTR_OPERATOR).equals(getType()) : "Invalid type " + ATTR_OPERATOR + "=" + source.getString(ATTR_OPERATOR) + " expected " + getType();

    mNot = source.getBoolean(ATTR_NOT);
    mIgnoreCase = source.getBoolean(ATTR_IGNORE_CASE);

    mValues.clear();
    JSONArray array = source.getJSONArray(Value.ELEMENT);
    for (int i = 0; i < array.length(); ++i)
    {
      JSONObject obj = array.getJSONObject(i);
      Value value = factory.createValue(obj.getString(ATTR_OPERATOR));
      value.fromJson(obj, factory);
      mValues.add(value);
    }
  }

  /**
   * Number of Value objects expected to operate on 1 = Unary operator 2 = Binary comparator ... -1 = One or more
   * 
   * @return Number specific to the actual comparator
   */
  public abstract int getExpectedValueCount();

  /**
   * Value type expected to operate on
   * 
   * @return Name of the type this operators works with
   */
  public abstract String getExpectedValueType();

  /**
   * {@inheritDoc}
   */
  public List<Value> getValues()
  {
    return mValues;
  }
}
