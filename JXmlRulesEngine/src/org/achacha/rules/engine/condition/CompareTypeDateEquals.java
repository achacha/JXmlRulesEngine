package org.achacha.rules.engine.condition;

import java.util.Date;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * Date equals
 */
public class CompareTypeDateEquals extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "DateEquals";

  /** Meta type */
  public static final String META = META_DATE;

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
    Date lhs = mValues.get(0).asDate(ruleContext);
    Date rhs = mValues.get(1).asDate(ruleContext);

    return lhs.equals(rhs) ^ mNot;
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
