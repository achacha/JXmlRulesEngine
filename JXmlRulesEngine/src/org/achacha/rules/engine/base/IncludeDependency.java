package org.achacha.rules.engine.base;

import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;

/**
 * Interface for an object that also loads dependencies
 */
public interface IncludeDependency
{
  /**
   * Load dependencies
   * 
   * @param loader RulesEngineLoader
   * @param loaded items to detect cycles
   */
  void loadDependencies(RulesEngineLoader loader, Set<String> loaded);

  /**
   * Full toString() with resolved content instead of include directives
   * 
   * @return String
   */
  String toStringResolved();
}
