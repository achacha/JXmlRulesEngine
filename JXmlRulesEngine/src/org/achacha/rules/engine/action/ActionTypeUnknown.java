package org.achacha.rules.engine.action;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.ActionType;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.dom4j.Element;
import org.json.JSONObject;

/**
 * The unknown action when rule action is not defined
 * By default it just adds itself to the output as an UNKNOWN action
 */
public class ActionTypeUnknown extends ActionType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Type */
  public static final String TYPE = "Unknown";

  /** Actions included */
  protected Element mAction;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType()
  {
    return TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(RuleContext ruleContext)
  {
    assert null != mAction : "Action Element cannot be null during execute";

    ruleContext.getOutputModel().add(mAction.createCopy());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void parse(Element root, RulesEngineFactory factory)
  {
    super.parse(root, factory);
    mAction = root.createCopy();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate()
  {
    if (null == mAction)
    {
      throw new IllegalArgumentException("Missing action element");
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
    e.add(mAction.createCopy());

    return e;
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
    buf.append(TYPE);
    buf.append(")  Action=");
    buf.append(mAction.asXML());

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
  @Override
  public JSONObject toJson()
  {
    throw new RuntimeException("Unknown action is not intended to have a JSON representation");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory)
  {
    throw new RuntimeException("Unknown action is not intended to have a JSON representation");
  }
}
