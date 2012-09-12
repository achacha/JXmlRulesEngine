package org.achacha.rules.engine.action;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.ActionType;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Output model action
 */
public abstract class ActionTypeOutputValue extends ActionType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Path element */
  public static final String PATH = "Path";

  /** Path */
  protected String mPath;

  /** Path */
  protected Value mValue;

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate()
  {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(RuleContext ruleContext)
  {
    Element e = getPathElement(ruleContext);
    e.setText(mValue.asString(ruleContext));
  }

  /**
   * Get element to add content to based on mode
   * 
   * @param ruleContext RuleContext
   * @return Element to add content to
   */
  protected abstract Element getPathElement(RuleContext ruleContext);

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);

    JSONObject obj = source.getJSONObject(Value.ELEMENT);
    mValue = factory.createValue(obj.getString(Value.ATTR_OPERATOR));
    mValue.fromJson(obj, factory);
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    mPath = root.element(PATH).getTextTrim();
    mValue = factory.createValue(root.element(Value.ELEMENT));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addElement(PATH).setText(mPath);

    assert null != mValue : "Must have Value object associated with action: " + toString();
    e.add(mValue.toElement());

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(PATH, mPath);
    obj.put(Value.ELEMENT, mValue.toJson());

    return obj;
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
    buf.append(")");

    if (null != mPath)
    {
      buf.append("  path: ");
      buf.append(mPath);
    }
    if (null != mValue)
    {
      buf.append("  value: ");
      buf.append(mValue);
    }

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    return toString();
  }
}
