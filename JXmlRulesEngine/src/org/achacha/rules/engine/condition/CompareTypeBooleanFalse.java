package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * Number equals
 */
public class CompareTypeBooleanFalse extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "BooleanTrue";

  /** Meta type */
  public static final String META = META_BOOLEAN;

  /** Operand count */
  public static final int COUNT = 1;

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
    boolean lhs = Boolean.parseBoolean(mValues.get(0).asString(ruleContext));

    return lhs ^ mNot;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getExpectedValueCount()
  {
    return COUNT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExpectedValueType()
  {
    return META;
  }
}
