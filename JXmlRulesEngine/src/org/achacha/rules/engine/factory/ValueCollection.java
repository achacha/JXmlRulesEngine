package org.achacha.rules.engine.factory;

import java.util.List;

import org.achacha.rules.engine.base.RuleContext;

/**
 * Value type to a collection
 */
public interface ValueCollection
{
  /**
   * Get collection from the provided value data
   * 
   * @param ruleContext RuleContext
   * @return String array of the collection pointed to by the provided value
   */
  List<String> asStringArray(RuleContext ruleContext);
}
