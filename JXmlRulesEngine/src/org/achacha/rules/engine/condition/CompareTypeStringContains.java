package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * String contains
 */
public class CompareTypeStringContains extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "StringContains";

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

    if (mIgnoreCase)
    {
      lhs = lhs.toLowerCase();
      rhs = rhs.toLowerCase();
    }

    return (lhs.indexOf(rhs) >= 0 ? true : false) ^ mNot;
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
