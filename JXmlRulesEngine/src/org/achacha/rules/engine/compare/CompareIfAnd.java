package org.achacha.rules.engine.compare;

import java.util.Iterator;

import org.achacha.rules.engine.base.Compare;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareIf;

/**
 * Condition group AND
 */
public class CompareIfAnd extends CompareIf
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Group type */
  public static final String TYPE = "AND";

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
  public boolean evaluate(RuleContext ruleContext)
  {
    Iterator<Compare> it = mComparisons.iterator();
    boolean result = true;
    while (it.hasNext())
    {
      Compare compare = it.next();
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("?AND evaluating condition:\r\n " + compare);
      }
      try
      {
        result &= compare.evaluate(ruleContext);
        if (ruleContext.getEvents().isEventEnabled())
        {
          ruleContext.getEvents().event(compare.toString() + "=" + result);
        }
        if (!result)
        {
          // a_Compare is false
          return mNot;
        }
      }
      catch (Exception e)
      {
        ruleContext.getEvents().error("Evaluate failed: " + e + "\r\n" + toElement().asXML());
        throw new RuntimeException("Evaluate failed: " + e + "\r\n" + toElement().asXML(), e);
      }
    }
    return result ^ mNot;
  }
}
