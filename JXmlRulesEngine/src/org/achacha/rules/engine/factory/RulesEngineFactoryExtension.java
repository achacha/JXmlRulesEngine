package org.achacha.rules.engine.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to extend RulesEngineFactory
 */
public class RulesEngineFactoryExtension
{
  /** User defined */
  protected Map<String, String> mCustomCompareIf;

  /** User defined */
  protected Map<String, String> mCustomCompareType;

  /** User defined */
  protected Map<String, String> mCustomValue;

  /** User defined */
  protected Map<String, String> mCustomActionType;

  /**
   * Default ctor
   */
  protected RulesEngineFactoryExtension()
  {
    mCustomCompareIf = new HashMap<String, String>();
    mCustomCompareType = new HashMap<String, String>();
    mCustomValue = new HashMap<String, String>();
    mCustomActionType = new HashMap<String, String>();
  }

  /**
   * ctor used by spring
   * 
   * @param mapCompareIf map of String to class path String
   * @param mapCompareType map of String to class path String
   * @param mapValue map of String to class path String
   * @param mapActionType map of String to class path String
   */
  public RulesEngineFactoryExtension(Map<String, String> mapCompareIf, Map<String, String> mapCompareType, Map<String, String> mapValue, Map<String, String> mapActionType)
  {
    mCustomCompareIf = mapCompareIf;
    mCustomCompareType = mapCompareType;
    mCustomValue = mapValue;
    mCustomActionType = mapActionType;
  }

  /**
   * @return Custom CompareIf map
   */
  public Map<String, String> getCustomCompareIf()
  {
    return mCustomCompareIf;
  }

  /**
   * @return Custom CompareType map
   */
  public Map<String, String> getCustomCompareType()
  {
    return mCustomCompareType;
  }

  /**
   * @return Custom Value map
   */
  public Map<String, String> getCustomValue()
  {
    return mCustomValue;
  }

  /**
   * @return Custom ActionType map
   */
  public Map<String, String> getCustomActionType()
  {
    return mCustomActionType;
  }
}
