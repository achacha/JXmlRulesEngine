package org.achacha.rules.engine.value;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareTypeCollection;
import org.achacha.rules.engine.factory.ValueCollection;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Gets collection in input model from provided xpath
 */
public class ValueCollectionStringInputModel extends ValueBeanBacked implements ValueCollection
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Value type */
  public static final String TYPE = "CollectionStringInputModel";

  /**
   * {@inheritDoc}
   */
  public String getType()
  {
    return TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<String> asStringArray(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".asStringArray: input model path=" + mPath);
    }
    Element collection = (Element) ruleContext.getInputModel().selectSingleNode(mPath);
    if (null == collection)
    {
      if (ruleContext.getEvents().isWarnEnabled())
      {
        ruleContext.getEvents().warn("   " + TYPE + ": Path specified not found in the input model:" + mPath);
      }
      return new ArrayList<String>();
    }

    List<Element> valueElements = collection.elements(CompareTypeCollection.ELEMENT_ENTRY);
    if (null != valueElements)
    {
      // a_List of entries
      ArrayList<String> values = new ArrayList<String>(valueElements.size());
      for (Element e : valueElements)
      {
        values.add(e.getText());
      }

      return values;
    }

    // a_Comma delimited
    ArrayList<String> values = new ArrayList<String>();
    StringTokenizer tokens = new StringTokenizer(collection.getText());
    while (tokens.hasMoreTokens())
    {
      values.add(tokens.nextToken());
    }
    return values;
  }

  /**
   * {@inheritDoc}
   * 
   * @return path specified that points to the collection
   */
  @Override
  public String asString(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".asString: input model path=" + mPath);
    }
    return mPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void publish(Map<String, Object> objects, Element model) throws NoSuchObjectException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InvalidObjectException
  {
    // Content
    Element e = DocumentHelper.makeElement(model, mPath);
    if (null != mContent)
    {
      mContent.publish(e);
    }

    // Bean
    if (null != mBeanProperty)
    {
      Object bean = objects.get(mBeanProperty.getObject());
      if (null == bean)
      {
        throw new NoSuchObjectException(mBeanProperty.getObject());
      }

      // a_This should be a collection
      Object obj = PropertyUtils.getProperty(bean, mBeanProperty.getPath());
      String methodName = mBeanProperty.getMethod();
      if (null != methodName)
      {
        // a_Need to invoke a method
        Object resultObj = MethodUtils.invokeExactMethod(obj, mBeanProperty.getMethod(), mBeanProperty.getMethodParam());
        if (null != resultObj)
        {
          if (resultObj instanceof Collection)
          {
            for (Object o : (Collection<?>) resultObj)
            {
              e.addElement(CompareTypeCollection.ELEMENT_ENTRY).setText(o.toString());
            }
          }
          else
          {
            throw new InvalidObjectException("BeanPath=" + mBeanProperty + " is not a Collection type");
          }
        }
      }
      else
      {
        if (null != obj)
        {
          if (obj instanceof Collection)
          {
            for (Object o : (Collection<?>) obj)
            {
              e.addElement(CompareTypeCollection.ELEMENT_ENTRY).setText(o.toString());
            }
          }
          else
          {
            throw new InvalidObjectException("BeanPath=" + mBeanProperty + " is not a Collection type");
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(RuleContext ruleContext)
  {
    if (null == mPath)
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", path is missing: " + toElement());
      return false;
    }

    if (null == ruleContext.getInputModel().selectSingleNode(mPath))
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", input model node for the path (" + mPath + ") is missing: " + ruleContext.getInputModel().asXML());
      return false;
    }

    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".isValid: result=true");
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("   ");
    buf.append(TYPE);
    buf.append("{ path={");
    buf.append(null != mPath ? mPath : "null");
    buf.append("} }");
    return buf.toString();
  }
}
