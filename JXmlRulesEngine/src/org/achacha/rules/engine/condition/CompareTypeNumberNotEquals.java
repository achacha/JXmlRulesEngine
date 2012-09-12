package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;

/**
 * Number not equals
 */
public class CompareTypeNumberNotEquals extends CompareTypeNumberEquals
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "NumberNotEquals";

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
