package org.achacha.rules.engine.processor;

import java.lang.reflect.Method;

import org.achacha.rules.engine.base.RuleContext;
import org.dom4j.Element;

/**
 * POJO processor Signature: void myMethod(RuleContext context);
 */
public class RulesEnginePojoProcessor
{
  /** default method parameter types */
  public static final Class<?>[] DEFAULT_PARAMETER_TYPES = new Class[] { RuleContext.class, Element.class };

  /** default method return type */
  public static final Class<?> DEFAULT_RETURN_TYPE = Boolean.TYPE;

  /** Object */
  protected String mObject;

  /** Method */
  protected String mMethod;

  /**
   * Ctor
   * 
   * @param object Package.Object path
   * @param method Method name
   */
  public RulesEnginePojoProcessor(String object, String method)
  {
    if (null == object || null == method || 0 == object.length() || 0 == method.length())
    {
      throw new RuntimeException("PojoProcessor is missing object or method");
    }
    mObject = object;
    mMethod = method;
  }

  /**
   * Ctor
   * 
   * @param objectMethod Package.Object.Method path
   */
  public RulesEnginePojoProcessor(String objectMethod)
  {
    if (null == objectMethod || 0 == objectMethod.length())
    {
      throw new RuntimeException("PojoProcessor is missing object.method");
    }

    int methodPos = objectMethod.lastIndexOf('.');
    if (-1 == methodPos)
    {
      throw new RuntimeException("PojoProcessor detected invalid object.method: " + objectMethod);
    }

    mObject = objectMethod.substring(0, methodPos);
    mMethod = objectMethod.substring(methodPos + 1);
  }

  /**
   * Must have object and method
   */
  protected RulesEnginePojoProcessor()
  {
  }

  /**
   * Execute POJO method
   * 
   * @param context RuleContext
   * @param processorXml XML from the request
   */
  public void execute(RuleContext context, Element processorXml)
  {
    Object object = newInstance();
    try
    {
      Method method = object.getClass().getMethod(mMethod, DEFAULT_PARAMETER_TYPES);
      Object[] params = new Object[] { context, processorXml };
      method.invoke(object, params);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create a new object
   * 
   * @return Object
   */
  protected Object newInstance()
  {
    ClassLoader loader = this.getClass().getClassLoader();
    try
    {
      Class<?> clazz = Class.forName(mObject, true, loader);
      return clazz.newInstance();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public String toString()
  {
    return "" + mObject + "." + mMethod;
  }
}
