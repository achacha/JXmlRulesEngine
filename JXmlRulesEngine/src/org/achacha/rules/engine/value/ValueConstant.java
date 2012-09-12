package org.achacha.rules.engine.value;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Datum reference value
 */
public class ValueConstant extends Value
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Value type */
  public static final String TYPE = "Constant";

  /** Data of the value */
  public static final String DATA = "Data";

  /** Data */
  protected String mData;

  /**
   * ctor
   */
  public ValueConstant()
  {
    super();
  }

  /**
   * ctor
   * 
   * @param data of constant
   */
  public ValueConstant(String data)
  {
    mData = data;
  }

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
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".asString: data=" + mData);
    }
    return mData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(RuleContext ruleContext)
  {
    if (null == mData)
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", data is missing: " + toElement());
      return false;
    }

    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".isValid: result=true");
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    Element data = root.element(DATA);
    if (null == data)
    {
      throw new RuntimeException("Unable to find 'Data' element: " + root.asXML());
    }
    mData = data.getText();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addElement(DATA).addCDATA(mData);

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
    buf.append("{ data={");
    buf.append(null != mData ? mData : "null");
    buf.append("} }");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(DATA, mData);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);

    mData = source.getString(DATA);
  }
}
