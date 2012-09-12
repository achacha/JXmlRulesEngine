package org.achacha.rules.base;

import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parse JSONObject data
 */
public interface JsonParseable
{
  /**
   * Parse JSON format
   * 
   * @throws JSONException on error
   * @param source JSONObject
   * @param factory to use for creating rule parts
   */
  void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException;
}
