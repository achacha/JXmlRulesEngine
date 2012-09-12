package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;
import org.achacha.rules.engine.factory.Value;

/**
 * String equals
 */
public class CompareTypeExists extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "Exists";

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
    boolean result = true;
    for (Value value : mValues)
    {
      result &= value.isValid(ruleContext);
      if (!result)
      {
        if (ruleContext.getEvents().isDebugEnabled())
        {
          ruleContext.getEvents().debug("Value does not exist: " + value + "  " + ruleContext.getActiveRule());
        }
        return mNot;
      }
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
