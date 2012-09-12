package org.achacha.rules.base;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON emittable object
 */
public interface JsonEmittable
{
  /**
   * JSON representation
   * 
   * @throws JSONException on error
   * @return JSON string
   */
  JSONObject toJson() throws JSONException;
}
