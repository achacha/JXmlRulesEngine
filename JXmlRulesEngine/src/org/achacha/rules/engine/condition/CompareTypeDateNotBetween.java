package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;

/**
 * Date not between
 */
public class CompareTypeDateNotBetween extends CompareTypeDateBetween
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "DateNotBetween";

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
