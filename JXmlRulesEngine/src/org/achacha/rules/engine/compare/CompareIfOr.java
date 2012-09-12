package org.achacha.rules.engine.compare;

import java.util.Iterator;

import org.achacha.rules.engine.base.Compare;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareIf;

/**
 * Condition group OR
 */
public class CompareIfOr extends CompareIf
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Group type */
  public static final String TYPE = "OR";

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
    if (it.hasNext())
    {
      boolean result = false;
      while (it.hasNext())
      {
        Compare compare = it.next();
        if (ruleContext.getEvents().isDebugEnabled())
        {
          ruleContext.getEvents().debug("?OR evaluating condition:\r\n" + compare);
        }
        try
        {
          result |= compare.evaluate(ruleContext);
        }
        catch (Exception e)
        {
          ruleContext.getEvents().error("Evaluate failed: " + e + "\r\n" + toElement().asXML());
          throw new RuntimeException("Evaluate failed: " + e + "\r\n" + toElement().asXML(), e);
        }
      }
      return result ^ mNot;
    }
    // No compares results in true
    return !mNot;
  }
}
