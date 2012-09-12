package org.achacha.rules.engine.condition;

import org.achacha.rules.engine.base.RuleContext;

/**
 * Collection of string not contain
 */
public class CompareTypeCollectionStringNotContains extends CompareTypeCollectionStringContains
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "CollectionStringNotContains";

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
    return !super.evaluate(ruleContext);
  }
}
