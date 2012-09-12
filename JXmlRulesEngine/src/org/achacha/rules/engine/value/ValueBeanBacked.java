package org.achacha.rules.engine.value;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.Map;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.BeanProperty;
import org.achacha.rules.engine.factory.ConstantContent;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Bean property backed value
 */
public abstract class ValueBeanBacked extends Value
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element that contains the xpath */
  public static final String PATH = "Path";

  /** Element that contains the static content */
  public static final String CONTENT = "Content";

  /** Xpath of this value in the input model */
  protected String mPath;

  /** Bean property */
  protected BeanProperty mBeanProperty;

  /** Additional content that will be appended into the model at provided path */
  protected ConstantContent mContent;

  /**
   * Publish data given a beanpath to the model If method was specified, the Object needs to be resolved and method to be called with method parameter If no method, then Object is a beanpath to be
   * directly evaluated
   * 
   * @param objects map of String to Object
   * @param model to publish to
   * @throws NoSuchObjectException if object is not in the map
   * @throws NoSuchMethodException if beanpath cannot be resolved
   * @throws InvocationTargetException if beanpath failed to invoke
   * @throws IllegalAccessException if beanpath tried to access something it should not
   * @throws InvalidObjectException if object is not a Collection type
   */
  public abstract void publish(Map<String, Object> objects, Element model) throws NoSuchObjectException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InvalidObjectException;

  /**
   * Bean property associated with this value
   * 
   * @return BeanProperty
   */
  public BeanProperty getBeanProperty()
  {
    return mBeanProperty;
  }

  /**
   * Get the content XML
   * 
   * @return Element
   */
  public ConstantContent getContent()
  {
    return mContent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(RuleContext ruleContext)
  {
    if ((null != mBeanProperty) && (null == mBeanProperty.getObject() || null == mBeanProperty.getPath()))
    {
      return false;
    }

    return true;
  }

  /**
   * Get input model xpath
   * 
   * @return String
   */
  public String getInputModelXpath()
  {
    return mPath;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    mPath = root.elementText(PATH);
    if (null == mPath)
    {
      throw new RuntimeException("Value of type '" + getType() + "' must have '" + PATH + "' attribute to lookup in input model.");
    }

    // Content
    Element eContent = root.element(CONTENT);
    if (null != eContent)
    {
      mContent = new ConstantContent(eContent);
    }
    else
    {
      mContent = null;
    }

    // Bean
    Element eBean = root.element(BeanProperty.ELEMENT);
    if (null != eBean)
    {
      mBeanProperty = new BeanProperty();
      mBeanProperty.parse(eBean, factory);
    }
    else
    {
      mBeanProperty = null;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addElement(PATH).addText(mPath);

    // Content
    if (null != mContent)
    {
      e.add(mContent.toElement());
    }

    // Bean
    if (null != mBeanProperty)
    {
      e.add(mBeanProperty.toElement());
    }

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(PATH, mPath);

    // Content
    if (null != mContent)
    {
      obj.put(ConstantContent.ELEMENT, mContent.toJson());
    }

    // Bean
    if (null != mBeanProperty)
    {
      obj.put(BeanProperty.ELEMENT, mBeanProperty.toJson());
    }

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);

    mPath = source.getString(PATH);

    // Content
    JSONObject content = source.getJSONObject(ConstantContent.ELEMENT);
    if (null != content)
    {
      mContent.fromJson(content, factory);
    }
    else
    {
      mContent = null;
    }

    // Bean
    JSONObject obj = source.getJSONObject(BeanProperty.ELEMENT);
    if (null != obj)
    {
      mBeanProperty.fromJson(obj, factory);
    }
    else
    {
      mBeanProperty = null;
    }
  }
}
