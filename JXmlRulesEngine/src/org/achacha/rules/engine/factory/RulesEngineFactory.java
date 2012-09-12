package org.achacha.rules.engine.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.achacha.rules.cache.RuleCache;
import org.achacha.rules.engine.action.ActionTypeUnknown;
import org.achacha.rules.engine.base.Base;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.achacha.rules.engine.processor.RulesEngineProcessorFactory;
import org.apache.commons.lang.ClassUtils;
import org.dom4j.Element;

/**
 * Factory for creating rule parts
 */
public class RulesEngineFactory
{
  /** Region name for the rule cache */
  public static final String CACHE_NAME = "rule";

  /** Cache for Rule objects */
  protected RuleCache mRuleCache = new RuleCache();

  /** Processor factory, init with default */
  protected RulesEngineProcessorFactory mProcessorFactory = new RulesEngineProcessorFactory();

  /** Name of the bean in spring */
  public static final String BEAN = "rulesengine.factory";

  /** Map CompareIf to implementation */
  private static Map<String, Class<Base>> mCompareIfClasses = new HashMap<String, Class<Base>>();

  /** Map CompareType to implementation */
  private static Map<String, Class<Base>> mCompareTypeClasses = new HashMap<String, Class<Base>>();

  /** Map Value to implementation */
  private static Map<String, Class<Base>> mValueClasses = new HashMap<String, Class<Base>>();

  /** Map ActionType to implementation */
  private static Map<String, Class<Base>> mActionTypeClasses = new HashMap<String, Class<Base>>();

  /**
   * default ctor will initialize built-in set only
   */
  public RulesEngineFactory()
  {
    // Create containers
    init(null, mCompareIfClasses, CompareIf.getClassMap());
    init(null, mCompareTypeClasses, CompareType.getClassMap());
    init(null, mValueClasses, Value.getClassMap());
    init(null, mActionTypeClasses, ActionType.getClassMap());
  }

  /**
   * default ctor will initialize built-in set only
   * 
   * @param factoryExtension object
   */
  public RulesEngineFactory(RulesEngineFactoryExtension factoryExtension)
  {
    // Create containers
    init(null, mCompareIfClasses, CompareIf.getClassMap());
    init(null, mCompareTypeClasses, CompareType.getClassMap());
    init(null, mValueClasses, Value.getClassMap());
    init(null, mActionTypeClasses, ActionType.getClassMap());

    extend(factoryExtension);
  }

  /**
   * ctor usale by spring
   * 
   * @param mapCompareIf map of String to class path String
   * @param mapCompareType map of String to class path String
   * @param mapValue map of String to class path String
   * @param mapActionType map of String to class path String
   */
  public RulesEngineFactory(Map<String, String> mapCompareIf, Map<String, String> mapCompareType, Map<String, String> mapValue, Map<String, String> mapActionType)
  {
    init(mapCompareIf, mCompareIfClasses, CompareIf.getClassMap());
    init(mapCompareType, mCompareTypeClasses, CompareType.getClassMap());
    init(mapValue, mValueClasses, Value.getClassMap());
    init(mapActionType, mActionTypeClasses, ActionType.getClassMap());
  }

  /**
   * ctor used by spring
   * 
   * @param mapCompareIf map of String to class path String
   * @param mapCompareType map of String to class path String
   * @param mapValue map of String to class path String
   * @param mapActionType map of String to class path String
   * @param factoryExtension object
   */
  public RulesEngineFactory(Map<String, String> mapCompareIf, Map<String, String> mapCompareType, Map<String, String> mapValue, Map<String, String> mapActionType,
      RulesEngineFactoryExtension factoryExtension)
  {
    init(mapCompareIf, mCompareIfClasses, CompareIf.getClassMap());
    init(mapCompareType, mCompareTypeClasses, CompareType.getClassMap());
    init(mapValue, mValueClasses, Value.getClassMap());
    init(mapActionType, mActionTypeClasses, ActionType.getClassMap());

    extend(factoryExtension);
  }

  /**
   * Construct CompareIf map and use built-in if error occurs
   * 
   * @param source map of type name to class path string, if null build in is used
   * @param target map of type name to Class
   * @param fallback map to use if source is null or if source has an error (like missing class)
   */
  @SuppressWarnings(value = { "unchecked" })
  private void init(Map<String, String> source, Map<String, Class<Base>> target, Map<String, String> fallback)
  {
    boolean isValid = false;
    if (null != source)
    {
      try
      {
        for (Entry<String, String> entry : source.entrySet())
        {
          Class<Base> clazz = ClassUtils.getClass(entry.getValue());
          target.put(entry.getKey(), clazz);
        }
        isValid = true;
      }
      catch (Exception e)
      {
        target.clear();
      }
    }
    if (!isValid)
    {
      // a_Do not catch any exceptions at this point, propagate up
      for (Entry<String, String> entry : fallback.entrySet())
      {
        Class<Base> clazz = RulesEngineHelper.getClass(entry.getValue());
        target.put(entry.getKey(), clazz);
      }
    }
  }

  /**
   * Extend the factory with extension objects
   * 
   * @param factoryExtension object
   */
  public void extend(RulesEngineFactoryExtension factoryExtension)
  {
    if (null != factoryExtension)
    {
      init(factoryExtension.getCustomCompareIf(), mCompareIfClasses, CompareIf.getClassMap());
      init(factoryExtension.getCustomCompareType(), mCompareTypeClasses, CompareType.getClassMap());
      init(factoryExtension.getCustomValue(), mValueClasses, Value.getClassMap());
      init(factoryExtension.getCustomActionType(), mActionTypeClasses, ActionType.getClassMap());
    }
  }

  /**
   * Rule cache
   * 
   * @return Cache
   */
  public RuleCache getRuleCache()
  {
    return mRuleCache;
  }

  /**
   * One create to create them all Create and parse CompareIf, CompareType or ActionType from Element
   * 
   * @param root Element
   * @return Base parsed object or null if to be ignored
   */
  public Base create(Element root)
  {
    if (null == root)
    {
      return null;
    }

    if (root.getName().equals(CompareIf.ELEMENT))
    {
      // a_CompareIf
      return createCompareIf(root);
    }
    else if (root.getName().equals(CompareType.ELEMENT))
    {
      // a_CompareType
      return createCompareType(root);
    }
    else if (root.getName().equals(ActionType.ELEMENT))
    {
      // a_ActionType
      return createActionType(root);
    }
    else if (root.getName().equals(Value.ELEMENT))
    {
      // a_Value
      return createValue(root);
    }
    else if (root.getName().equals(Base.ELEMENT_EDITOR))
    {
      // a_Editor is to be ignored
      return null;
    }
    else
    {
      throw new RuntimeException("Unable to create rule object for given element: " + root.asXML());
    }
  }

  /**
   * One create to create them all (from just the name) Create CompareIf, CompareType or ActionType from Element
   * 
   * @param object type
   * @param type to create
   * @return Base parsed object or null if to be ignored
   */
  public Base create(String object, String type)
  {
    if (object.equals(CompareIf.ELEMENT))
    {
      // a_CompareIf
      return createCompareIf(type);
    }
    else if (object.equals(CompareType.ELEMENT))
    {
      // a_CompareType
      return createCompareType(type);
    }
    else if (object.equals(ActionType.ELEMENT))
    {
      // a_ActionType
      return createActionType(type);
    }
    else if (object.equals(Value.ELEMENT))
    {
      // a_Value
      return createValue(type);
    }
    else if (object.equals(Base.ELEMENT_EDITOR))
    {
      // a_Editor is to be ignored
      return null;
    }
    else
    {
      throw new RuntimeException("Unable to create rule base object (" + object + ") for given type (" + type + ")");
    }
  }

  /**
   * Create and parse CompareIf from Element
   * 
   * @param root Element
   * @return CompareIf parsed object
   */
  public CompareIf createCompareIf(Element root)
  {
    if (null == root)
    {
      return null;
    }
    if (!root.getName().equals(CompareIf.ELEMENT))
    {
      throw new RuntimeException("Unable to create " + CompareIf.ELEMENT + ", invalid element: " + root.asXML());
    }

    String type = root.attributeValue(CompareIf.ATTR_OPERATOR);
    if (null == type)
    {
      throw new RuntimeException("Unable to create " + CompareIf.ELEMENT + ", missing " + CompareIf.ATTR_OPERATOR + " attribute: " + root.asXML());
    }

    CompareIf base = createCompareIf(type);
    base.parse(root, this);
    return base;
  }

  /**
   * Create CompareIf of given type
   * 
   * @param type to create
   * @return CompareIf
   */
  public CompareIf createCompareIf(String type)
  {
    // a_Type specified
    Class<Base> className = mCompareIfClasses.get(type);
    if (null == className)
    {
      throw new RuntimeException(CompareIf.ELEMENT + " handler not registered via spring for type: " + type);
    }

    // a_classpath now points to handler
    return (CompareIf) RulesEngineHelper.newInstance(className);
  }

  /**
   * Create and parse CompareType from Element
   * 
   * @param root Element
   * @return CompareIf parsed object
   */
  public CompareType createCompareType(Element root)
  {
    if (null == root)
    {
      return null;
    }
    if (!root.getName().equals(CompareType.ELEMENT))
    {
      throw new RuntimeException("Unable to create " + CompareType.ELEMENT + ", invalid element: " + root.asXML());
    }

    String type = root.attributeValue(CompareType.ATTR_OPERATOR);
    if (null == type)
    {
      throw new RuntimeException("Unable to create " + CompareType.ELEMENT + ", missing " + CompareType.ATTR_OPERATOR + " attribute: " + root.asXML());
    }

    CompareType base = createCompareType(type);
    base.parse(root, this);
    return base;
  }

  /**
   * Create CompareType
   * 
   * @param type to create
   * @return CompareType
   */
  public CompareType createCompareType(String type)
  {
    // a_Type specified
    Class<Base> className = mCompareTypeClasses.get(type);
    if (null == className)
    {
      throw new RuntimeException(CompareType.ELEMENT + " handler not registered via spring for type: " + type);
    }

    // a_classpath now points to handler
    return (CompareType) RulesEngineHelper.newInstance(className);
  }

  /**
   * Create and parse ActionType from Element
   * 
   * @param root Element
   * @return ActionType parsed object OR null if type not found (probably requiring ActionTypeUnknown)
   */
  public ActionType createActionType(Element root)
  {
    if (null == root)
    {
      return null;
    }
    if (!root.getName().equals(ActionType.ELEMENT))
    {
      throw new RuntimeException("Unable to create " + ActionType.ELEMENT + ", invalid element: " + root.asXML());
    }

    String type = root.attributeValue(Base.ATTR_OPERATOR);
    ActionType base = null;
    if (mActionTypeClasses.containsKey(type))
    {
      base = createActionType(type);
    }
    else
    {
      base = new ActionTypeUnknown();
    }
    base.parse(root, this);
    return base;

  }

  /**
   * Create ActionType
   * 
   * @param type to create
   * @return ActionType
   */
  public ActionType createActionType(String type)
  {
    // a_Type specified
    Class<Base> className = mActionTypeClasses.get(type);
    if (null == className)
    {
      throw new RuntimeException(ActionType.ELEMENT + " handler not registered via spring for type: " + type);
    }

    // a_classpath now points to handler
    return (ActionType) RulesEngineHelper.newInstance(className);
  }

  /**
   * Create and parse Value from Element
   * 
   * @param root Element
   * @return Value parsed object
   */
  public Value createValue(Element root)
  {
    if (null == root)
    {
      return null;
    }
    if (!root.getName().equals(Value.ELEMENT))
    {
      throw new RuntimeException("Unable to create " + Value.ELEMENT + ", invalid element: " + root.asXML());
    }

    String type = root.attributeValue(Value.ATTR_SOURCE);
    if (null == type)
    {
      throw new RuntimeException("Unable to create " + Value.ELEMENT + ", missing " + Value.ATTR_SOURCE + " attribute: " + root.asXML());
    }

    Value base = createValue(type);
    base.parse(root, this);
    return base;
  }

  /**
   * Create Value
   * 
   * @param type to create
   * @return Value
   */
  public Value createValue(String type)
  {
    // a_Type specified
    Class<Base> className = mValueClasses.get(type);
    if (null == className)
    {
      throw new RuntimeException(Value.ELEMENT + " handler not registered via spring for type: " + type);
    }

    // a_classpath now points to handler
    return (Value) RulesEngineHelper.newInstance(className);
  }

  /**
   * CompareIf classes
   * 
   * @return Map
   */
  public static Map<String, Class<Base>> getCompareIfClasses()
  {
    return mCompareIfClasses;
  }

  /**
   * CompareType classes
   * 
   * @return Map
   */
  public static Map<String, Class<Base>> getCompareTypeClasses()
  {
    return mCompareTypeClasses;
  }

  /**
   * Value classes
   * 
   * @return Map
   */
  public static Map<String, Class<Base>> getValueClasses()
  {
    return mValueClasses;
  }

  /**
   * ActionType classes
   * 
   * @return Map
   */
  public static Map<String, Class<Base>> getActionTypeClasses()
  {
    return mActionTypeClasses;
  }

  /**
   * @return RulesEngineProcessorFactory
   */
  public RulesEngineProcessorFactory getProcessorFactory()
  {
    return mProcessorFactory;
  }

  /**
   * @param processorFactory RulesEngineProcessorFactory
   */
  public void setProcessorFactory(RulesEngineProcessorFactory processorFactory)
  {
    mProcessorFactory = processorFactory;
  }
}
