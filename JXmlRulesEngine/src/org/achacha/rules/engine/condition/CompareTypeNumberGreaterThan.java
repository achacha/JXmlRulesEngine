package org.achacha.rules.engine.condition;

import java.math.BigDecimal;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * Number greater than
 */
public class CompareTypeNumberGreaterThan extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "NumberGreaterThan";

  /** Meta type */
  public static final String META = META_NUMBER;

  /** Operand count */
  public static final int COUNT = 2;

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
    BigDecimal lhs = new BigDecimal(mValues.get(0).asString(ruleContext));
    BigDecimal rhs = new BigDecimal(mValues.get(1).asString(ruleContext));

    return (lhs.compareTo(rhs) > 0) ^ mNot;
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
