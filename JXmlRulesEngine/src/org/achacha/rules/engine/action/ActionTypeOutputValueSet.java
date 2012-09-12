package org.achacha.rules.engine.action;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.dom4j.Element;

/**
 * Output model action
 */
public class ActionTypeOutputValueSet extends ActionTypeOutputValue
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Type */
  public static final String TYPE = "OutputValueSet";

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
  protected Element getPathElement(RuleContext ruleContext)
  {
    return RulesEngineHelper.overwriteElement(ruleContext.getOutputModel(), mPath);
  }
}
