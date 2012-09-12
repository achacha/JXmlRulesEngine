package org.achacha.rules.engine.condition;

import java.math.BigDecimal;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;
import org.achacha.rules.engine.factory.Value;

/**
 * Number equals
 */
public class CompareTypeNumberDialExclusive extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "NumberDialExclusive";

  /** Meta type */
  public static final String META = META_NUMBER;

  /** Operand count */
  public static final int COUNT = 5;

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
    BigDecimal minimum = new BigDecimal(mValues.get(3).asString(ruleContext));
    BigDecimal maximum = new BigDecimal(mValues.get(4).asString(ruleContext));

    if (ruleContext.getEvents().isDebugEnabled())
    {
      // a_Check limits
      boolean isValid = maximum.compareTo(minimum) > 0;
      isValid &= (lower.compareTo(maximum) <= 0) && (lower.compareTo(minimum) >= 0);
      isValid &= (upper.compareTo(maximum) <= 0) && (upper.compareTo(minimum) >= 0);
      if (!isValid)
      {
        String message = TYPE + ": Failed basic limits validation: " + toString();
        ruleContext.getEvents().debug(message);
      }
    }
    boolean result = false;
    switch (upper.compareTo(lower))
    {
    case -1:
      // a_upper < lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": upper < lhs < lower (minimum,maximum): " + upper + " < " + lhs + " < " + lower + "(" + minimum + "," + maximum + ")");
      }
      result = (lhs.compareTo(lower) > 0) && (lhs.compareTo(maximum) <= 0);
      result |= (lhs.compareTo(minimum) >= 0) && (lhs.compareTo(upper) < 0);
      break;

    case 0:
      // a_upper == lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": lower == upper, always false: " + lower + " == " + upper);
      }
      result = false;
      break;

    case 1:
      // a_upper > lower
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": lower < lhs < upper: " + lower + " < " + lhs + " < " + upper);
      }
      result = (upper.compareTo(lhs) > 0) && (lower.compareTo(lhs) < 0);
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
    buf.append(getType());
    if (mNot)
    {
      buf.append(",NOT");
    }
    if (mIgnoreCase)
    {
      buf.append(",NOCASE");
    }
    buf.append(")  { \r\n  ");
    for (Value value : mValues)
    {
      buf.append("  ");
      buf.append(value.toString());
      buf.append("\r\n");
    }
    buf.append("\r\n  }");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    return toString();
  }
}
