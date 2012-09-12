package org.achacha.rules.engine.condition;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareType;

/**
 * String matches regular expression
 */
public class CompareTypeStringRegExp extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "StringRegExp";

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
      // a_Only LHS is lower cased, RHS is a RegExp
      lhs = lhs.toLowerCase();
    }

    Pattern pattern = Pattern.compile(rhs);
    Matcher matcher = pattern.matcher(lhs);

    if (ruleContext.getEvents().isDebugEnabled())
    {
      if (!matcher.matches())
      {
        ruleContext.getEvents().debug(TYPE + ": Cannot find '" + rhs + "' inside '" + lhs + "'");
      }
    }

    return matcher.matches() ^ mNot;
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
