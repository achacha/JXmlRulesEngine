package org.achacha.rules.engine.condition;

import java.util.Date;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * String equals
 */
public class CompareTypeDateBetween extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "DateBetween";

  /** Meta type */
  public static final String META = META_DATE;

  /** Operand count */
  public static final int COUNT = 3;

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
    Date lhs = mValues.get(0).asDate(ruleContext);
    Date lower = mValues.get(1).asDate(ruleContext);
    Date upper = mValues.get(2).asDate(ruleContext);

    boolean result = lhs.after(lower) && lhs.before(upper);

    return result ^ mNot;
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
