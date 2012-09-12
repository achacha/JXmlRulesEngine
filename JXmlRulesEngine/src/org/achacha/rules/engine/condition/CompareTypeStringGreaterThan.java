package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * String greater than
 */
public class CompareTypeStringGreaterThan extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "StringGreaterThan";

  /** Meta type */
  public static final String META = META_STRING;

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
    String lhs = mValues.get(0).asString(ruleContext);
    String rhs = mValues.get(1).asString(ruleContext);

    int c = 0;
    if (mIgnoreCase)
    {
      c = lhs.compareToIgnoreCase(rhs);
    }
    else
    {
      c = lhs.compareTo(rhs);
    }
    return (c > 0) ^ mNot;
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
