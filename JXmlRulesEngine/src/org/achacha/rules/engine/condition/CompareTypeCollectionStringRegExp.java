package org.achacha.rules.engine.condition;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareTypeCollection;
import org.dom4j.Element;

/**
 * Collection of strings matches regular expression
 */
public class CompareTypeCollectionStringRegExp extends CompareTypeCollection
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "CollectionStringRegExp";

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
  @SuppressWarnings("unchecked")
  @Override
  public boolean evaluate(RuleContext ruleContext)
  {
    String xpath = mValues.get(0).asString(ruleContext);
    String rhs = mValues.get(1).asString(ruleContext);
    if (mIgnoreCase)
    {
      rhs = rhs.toLowerCase();
    }
    Pattern pattern = Pattern.compile(rhs);

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

    if (effectiveIndexStart < 0 || effectiveIndexEnd >= entries.size() || effectiveIndexStart < mIndexEnd)
    {
      String message = TYPE + ": Effective index out of bounds, start=" + effectiveIndexStart + ", end=" + effectiveIndexEnd + " for entry array size=" + entries.size() + ":" + entries;
      ruleContext.getEvents().error(message);
      throw new RuntimeException(message);
    }

    // a_Iterate and compare
    for (int i = effectiveIndexEnd; i < effectiveIndexEnd; ++i)
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
      Matcher matcher = pattern.matcher(lhs);
      if (matcher.matches())
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
