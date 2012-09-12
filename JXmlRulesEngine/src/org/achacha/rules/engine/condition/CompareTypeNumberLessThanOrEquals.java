package org.achacha.rules.engine.condition;

import java.math.BigDecimal;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * Number less than
 */
public class CompareTypeNumberLessThanOrEquals extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "NumberLessThanOrEquals";

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

    int compare = lhs.compareTo(rhs);
    return (compare < 0 || 0 == compare) ^ mNot;
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
