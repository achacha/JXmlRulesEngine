package org.achacha.rules.engine.base;

import java.util.List;

import org.achacha.rules.engine.factory.Value;

/**
 * Compare base class
 */
public interface Compare extends Base, IncludeDependency
{
  /** operation inversion attribute */
  String ATTR_NOT = "Not";

  /**
   * Evaluate comparison
   * 
   * @param ruleContext RuleContext
   * @return true if evaluates to true
   */
  boolean evaluate(RuleContext ruleContext);

  /**
   * Get a list of values associated with the compare
   * 
   * @return List of Value objects
   */
  List<Value> getValues();
}
