package org.achacha.rules.engine.condition;

import java.math.BigDecimal;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * Number equals
 */
public class CompareTypeNumberBetweenExclusive extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "NumberBetweenExclusive";

  /** Meta type */
  public static final String META = META_NUMBER;

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
    BigDecimal lhs = new BigDecimal(mValues.get(0).asString(ruleContext));
    BigDecimal lower = new BigDecimal(mValues.get(1).asString(ruleContext));
    BigDecimal upper = new BigDecimal(mValues.get(2).asString(ruleContext));

    boolean result = false;
    switch (upper.compareTo(lower))
    {
    case -1:
      // a_upper < lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": upper < lhs < lower: " + upper + " < " + lhs + " < " + lower);
      }
      result = ((lhs.compareTo(lower) < 0) && (lhs.compareTo(upper) > 0));
      break;

    case 0:
      // a_upper == lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": upper == lower, always false.");
      }
      result = false;
      break;

    case 1:
      // a_upper > lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": lower < lhs < upper: " + lower + " < " + lhs + " < " + upper);
      }
      result = ((lhs.compareTo(lower) > 0) && (lhs.compareTo(upper) < 0));
      break;

    default:
      throw new RuntimeException(TYPE + ": Unexpected comparisson result of lower='" + lower + "' and upper='" + upper + "'");
    }

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
