package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;

/**
 * String not matching regular expression
 */
public class CompareTypeStringNotRegExp extends CompareTypeStringRegExp
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "StringNotRegExp";

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
  public boolean evaluate(RuleContext ruleContext)
  {
    return !super.evaluate(ruleContext);
  }
}
