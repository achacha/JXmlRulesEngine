package org.achacha.rules.engine.factory;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.achacha.rules.engine.base.Base;
import org.achacha.rules.engine.base.DateParsingFormats;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.value.ValueCollectionStringInputModel;
import org.achacha.rules.engine.value.ValueCollectionStringOutputModel;
import org.achacha.rules.engine.value.ValueConstant;
import org.achacha.rules.engine.value.ValueInputModel;
import org.achacha.rules.engine.value.ValueOutputModel;
import org.apache.commons.lang.time.DateUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Value
 */
public abstract class Value implements Base
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element value */
  public static final String ELEMENT = "Value";

  /** Source of the value */
  public static final String ATTR_SOURCE = "Source";

  /** Map action type to implementation */
  private static HashMap<String, String> mValueClasses = new HashMap<String, String>();

  static
  {
    mValueClasses.put(ValueConstant.TYPE, ValueConstant.class.getName());
    mValueClasses.put(ValueInputModel.TYPE, ValueInputModel.class.getName());
    mValueClasses.put(ValueOutputModel.TYPE, ValueOutputModel.class.getName());

    mValueClasses.put(ValueCollectionStringInputModel.TYPE, ValueCollectionStringInputModel.class.getName());
    mValueClasses.put(ValueCollectionStringOutputModel.TYPE, ValueCollectionStringOutputModel.class.getName());
  }

  /**
   * Default ctor
   */
  public Value()
  {
  }

  /**
   * Accessed by the factory during initialization
   * 
   * @return map of type to class path string
   */
  static Map<String, String> getClassMap()
  {
    return mValueClasses;
  }

  /**
   * {@inheritDoc}
   */
  public Value clone(RulesEngineFactory factory)
  {
    Value clone = factory.createValue(toElement());
    return clone;
  }

  /**
   * String representation
   * 
   * @param ruleContext RuleContext
   * @return String of the value
   */
  public abstract String asString(RuleContext ruleContext);

  /**
   * Convert asString to int
   * 
   * @param ruleContext RuleContext
   * @return int version of the string value
   */
  public int asInt(RuleContext ruleContext)
  {
    return Integer.parseInt(asString(ruleContext));
  }

  /**
   * Convert asString to Date
   * 
   * @param ruleContext RuleContext
   * @return Parsed Date object from string representation
   */
  public Date asDate(RuleContext ruleContext)
  {
    String str = asString(ruleContext);
    try
    {
      return DateUtils.parseDate(str, DateParsingFormats.SUPPORTED_FORMATS);
    }
    catch (ParseException e)
    {
      throw new RuntimeException("Unable to parse Date format (" + DateFormat.DEFAULT + "): " + str, e);
    }
  }

  /**
   * Checks if the value is valid
   * 
   * @param ruleContext RuleContext
   * @return true if exists and valid
   */
  public abstract boolean isValid(RuleContext ruleContext);

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element root = DocumentHelper.createElement(elementName);

    root.addAttribute(ATTR_SOURCE, getType());

    return root;
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
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();

    obj.put(ATTR_SOURCE, getType());

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    assert source.getString(ATTR_SOURCE).equals(getType()) : "Type mismatch for Value " + ATTR_SOURCE + "=" + source.getString(ATTR_SOURCE) + " expected " + getType();
  }
}
