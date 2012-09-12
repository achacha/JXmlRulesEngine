
package org.achacha.rules.cache;

import java.util.HashMap;
import java.util.Map;

import org.achacha.rules.engine.base.Rule;

/**
 * Simple cache that keeps all parsed rules in memory
 */
public class RuleCache
{
  // Simple in-memory rule cache
  private Map<String, Rule> mCache = new HashMap<String, Rule>();
  
  public RuleCache()
  {
  }
  
  synchronized public void put(String key, Rule rule)
  {
    mCache.put(key,  rule);
  }
  
  synchronized public Rule get(String key)
  {
    return mCache.get(key);
  }
}
