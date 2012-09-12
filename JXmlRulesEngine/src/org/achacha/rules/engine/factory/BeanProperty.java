package org.achacha.rules.engine.factory;

import org.achacha.rules.engine.base.Base;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Bean property <xmp> <Bean Object="obj"> <Path>foo</Path> <Method>bar</Method> <MethodParam>baz</MethodParam> </Bean> </xmp>
 */
public class BeanProperty implements Base
{
  /** serial id */
  private static final long serialVersionUID = 1L;

  /** Bean used to get value */
  public static final String ELEMENT = "Bean";

  /** Bean object */
  public static final String ATTR_OBJECT = "Object";

  /** Method to execute */
  public static final String METHOD = "Method";

  /** Method to execute */
  public static final String METHOD_PARAM = "MethodParam";

  /** Path */
  public static final String PATH = "Path";

  /** Bean object */
  protected String mObject;

  /** Bean path */
  protected String mPath;

  /** Optional bean method */
  protected String mMethod;

  /** Optional bean method parameter */
  protected String mMethodParam;

  /**
   * Default ctor
   */
  public BeanProperty()
  {
  }

  /**
   * Ctor
   * 
   * @param object to use
   * @param beanpath to use
   */
  public BeanProperty(String object, String beanpath)
  {
    mObject = object;
    mPath = beanpath;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    assert root.getName().equals(ELEMENT) : "Root element must be '" + ELEMENT + "'";

    mObject = root.attributeValue(ATTR_OBJECT);
    if (null == mObject)
    {
      throw new RuntimeException("BeanProperty.parse: '" + ELEMENT + "'must have a '" + ATTR_OBJECT + "' attribute: " + root.asXML());
    }
    Element e = root.element(PATH);
    if (null != e)
    {
      mPath = e.getTextTrim();
      if (null == mPath)
      {
        throw new RuntimeException("BeanProperty.parse: '" + ELEMENT + "/" + PATH + "' element text must not be empty: " + root.asXML());
      }
    }
    else
    {
      throw new RuntimeException("BeanProperty.parse: '" + ELEMENT + "' must have a '" + PATH + "' element: " + root.asXML());
    }
    e = root.element(METHOD);
    if (null != e)
    {
      mMethod = e.getTextTrim();

      e = root.element(METHOD_PARAM);
      if (null != e)
      {
        mMethodParam = e.getTextTrim();
      }
      else
      {
        mMethodParam = StringUtils.EMPTY;
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return toElement(ELEMENT);
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element e = DocumentHelper.createElement(ELEMENT);

    e.addAttribute(ATTR_OBJECT, mObject);
    e.addElement(PATH).setText(mPath);
    if (null != mMethod)
    {
      e.addElement(METHOD).setText(mMethod);
      e.addElement(METHOD_PARAM).setText(mMethodParam);
    }

    return e;
  }

  /**
   * @return the Object
   */
  public String getObject()
  {
    return mObject;
  }

  /**
   * @return the Path
   */
  public String getPath()
  {
    return mPath;
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();

    obj.put(ATTR_OBJECT, mObject);
    if (null == mMethod)
    {
      obj.put(METHOD, mMethod);
      obj.put(METHOD_PARAM, mMethodParam);
    }
    obj.put(PATH, mPath);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mObject = source.getString(ATTR_OBJECT);
    mPath = source.getString(PATH);
    mMethod = source.getString(METHOD);
    if (null != mMethod)
    {
      mMethodParam = source.getString(METHOD_PARAM);
    }
    else
    {
      mMethodParam = StringUtils.EMPTY;
    }
  }

  /**
   * {@inheritDoc}
   */
  public String getType()
  {
    return ELEMENT;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("object=");
    buf.append(mObject);
    if (null != mMethod)
    {
      buf.append("  method=");
      buf.append(mMethod);
      buf.append(" methodparam=");
      buf.append(mMethodParam);
    }
    buf.append("  path=");
    buf.append(mPath);

    return buf.toString();
  }

  /**
   * Check if the method needs to be called, if not the object is the result
   * 
   * @return the method, if null there is no method and method param is undefined
   */
  public String getMethod()
  {
    return mMethod;
  }

  /**
   * @return Method param to use IF method is not null
   */
  public String getMethodParam()
  {
    return mMethodParam;
  }
}
