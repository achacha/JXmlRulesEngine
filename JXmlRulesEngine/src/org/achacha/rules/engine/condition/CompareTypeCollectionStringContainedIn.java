package org.achacha.rules.engine.condition;

import java.util.List;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.value.ValueCollectionStringInputModel;
import org.dom4j.Element;

/**
 * Value 0 contained in collection at Value 1
 */
public class CompareTypeCollectionStringContainedIn extends CompareTypeCollectionStringContains
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "CollectionStringContainedIn";

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
  @SuppressWarnings("unchecked")
  @Override
  public boolean evaluate(RuleContext ruleContext)
  {
    String rhs = mValues.get(0).asString(ruleContext);
    if (mIgnoreCase)
    {
      rhs = rhs.toLowerCase();
    }

    String xpath = mValues.get(1).asString(ruleContext);
    if (0 == xpath.length())
    {
      String message = TYPE + ": Xpath is empty (did you mean to use " + ValueCollectionStringInputModel.TYPE + "?)";
      ruleContext.getEvents().error(message);
      throw new RuntimeException(message);
    }

    // a_Get collection root
    Element collectionRoot = (Element) ruleContext.getInputModel().selectSingleNode(xpath);
    if (null == collectionRoot)
    {
      String message = TYPE + ": Input model xpath does not exist: " + xpath;
      ruleContext.getEvents().error(message);
      throw new RuntimeException(message);
    }

    // a_Get list of entries
    List<Element> entries = collectionRoot.elements(ELEMENT_ENTRY);
    if (0 == entries.size())
    {
      // a_Empty set, negative result affected by mNot flag
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": There were no entries in the specified container: " + xpath);
      }

      return mNot;
    }

    // a_Calculate effective indices
    int effectiveIndexStart = mIndexStart;
    if (mIndexStart < 0)
    {
      // a_Compare last N items, shift the index
      effectiveIndexStart = entries.size() + mIndexStart;
    }
    int effectiveIndexEnd = mIndexEnd;
    if (mIndexEnd == Integer.MAX_VALUE)
    {
      effectiveIndexEnd = entries.size() - 1;
    }
    else if (effectiveIndexEnd < 0)
    {
      // a_Compare last N items, shift the index
      effectiveIndexStart = entries.size() + mIndexStart;
    }

    if (effectiveIndexStart < 0 || effectiveIndexEnd >= entries.size() || effectiveIndexStart > effectiveIndexEnd)
    {
      String message = TYPE + ": Effective index out of bounds, start=" + effectiveIndexStart + ", end=" + effectiveIndexEnd + " for entry array size=" + entries.size() + ":" + entries;
      ruleContext.getEvents().error(message);
      throw new RuntimeException(message);
    }

    // a_Iterate and compare
    for (int i = effectiveIndexStart; i <= effectiveIndexEnd; ++i)
    {
      Element entry = entries.get(i);
      String lhs = entry.getText();
      if (mIgnoreCase)
      {
        lhs = lhs.toLowerCase();
      }

      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug(TYPE + ": Entry#" + i + " testing '" + rhs + "' inside '" + lhs);
      }
      if (lhs.indexOf(rhs) >= 0)
      {
        if (ruleContext.getEvents().isDebugEnabled())
        {
          ruleContext.getEvents().debug(TYPE + ": Found '" + rhs + "' inside '" + lhs);
        }
        return !mNot;
      }
    }
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug(TYPE + ": Did not find '" + rhs + "' in collection at '" + xpath);
    }
    return mNot;
  }
}
