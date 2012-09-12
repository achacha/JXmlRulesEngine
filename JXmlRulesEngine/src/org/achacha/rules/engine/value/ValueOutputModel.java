package org.achacha.rules.engine.value;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RequestContext parameter
 */
public class ValueOutputModel extends Value
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Value type */
  public static final String TYPE = "OutputModel";

  /** Datum name */
  public static final String PATH = "Path";

  /** Xpath into the input model */
  protected String mPath;

  /**
   * {@inheritDoc}
   */
  public String getType()
  {
    return TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String asString(RuleContext ruleContext)
  {
    Node node = ruleContext.getOutputModel().selectSingleNode(mPath);
    String value = "";
    if (null != node)
    {
      value = StringUtils.defaultString(node.getText());
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("   " + TYPE + ".asString: name=" + mPath + " value=" + value);
      }
    }
    else
    {
      ruleContext.getEvents().error("XPath not found in input model: " + mPath);
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(RuleContext ruleContext)
  {
    if (null == mPath)
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", Name is null (not found): " + toElement());
      return false;
    }

    Node node = ruleContext.getOutputModel().selectSingleNode(mPath);
    if (ruleContext.getEvents().isDebugEnabled())
    {
      String result = "null";
      if (null != node)
      {
        result = node.getText();
      }
      ruleContext.getEvents().debug("   " + TYPE + ".isValid: xpath=" + mPath + " result=" + result);
    }
    return null != node;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    mPath = root.element(PATH).getText();
    if (null == mPath)
    {
      throw new RuntimeException("Value of type '" + TYPE + "' must have '" + PATH + "' attribute to lookup in input model.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addAttribute(ATTR_OPERATOR, TYPE);
    e.addElement(PATH).setText(mPath);

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("   ");
    buf.append(TYPE);
    buf.append("{ ");
    buf.append(PATH);
    buf.append('=');
    buf.append(null != mPath ? mPath : "null");
    buf.append(" }");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(PATH, mPath);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);

    mPath = source.getString(PATH);
  }
}
