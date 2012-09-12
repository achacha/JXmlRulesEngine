package org.achacha.rules.engine.value;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.util.Map;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * RequestContext parameter
 */
public class ValueInputModel extends ValueBeanBacked
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Value type */
  public static final String TYPE = "InputModel";

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
  @Override
  public String asString(RuleContext ruleContext)
  {
    Node node = ruleContext.getInputModel().selectSingleNode(mPath);
    String value = "";
    if (null != node)
    {
      value = StringUtils.defaultString(node.getText());
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("   " + TYPE + ".asString: name=" + mPath + " value=" + value);
      }
    }
    else
    {
      ruleContext.getEvents().error("XPath not found in input model: " + mPath);
    }
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void publish(Map<String, Object> objects, Element model) throws NoSuchObjectException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    // Create element at path
    Element e = DocumentHelper.makeElement(model, mPath);

    // Content
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
      Object obj = PropertyUtils.getProperty(bean, mBeanProperty.getPath());
      if (null != obj)
      {
        String methodName = mBeanProperty.getMethod();
        if (null != methodName)
        {
          // a_Need to invoke a method
          Object resultObj = MethodUtils.invokeExactMethod(obj, mBeanProperty.getMethod(), mBeanProperty.getMethodParam());
          if (null != resultObj)
          {
            e.addCDATA(resultObj.toString());
          }
        }
        else
        {
          e.addCDATA(obj.toString());
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
    if (!super.isValid(ruleContext))
    {
      ruleContext.getEvents().error(TYPE + ".isValid: Bean property not found: " + toElement());
      return false;
    }

    if (null == mPath)
    {
      ruleContext.getEvents().error(TYPE + ".isValid: Path is null (not found): " + toElement());
      return false;
    }

    Node node = ruleContext.getInputModel().selectSingleNode(mPath);
    if (ruleContext.getEvents().isDebugEnabled())
    {
      String result = "null";
      if (null != node)
      {
        result = node.getText();
      }
      ruleContext.getEvents().debug("   " + TYPE + ".isValid: xpath=" + mPath + " result=" + result);
    }
    return null != node;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void parse(Element root, RulesEngineFactory factory)
  {
    super.parse(root, factory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addAttribute(ATTR_OPERATOR, TYPE);

    return e;
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
    buf.append("{ ");
    buf.append(PATH);
    buf.append('=');
    buf.append(null != mPath ? mPath : "null");
    buf.append("  ");
    buf.append(CONTENT);
    buf.append('=');
    buf.append(null != mContent ? mContent.toString() : "null");
    buf.append("  BeanObject=");
    buf.append(null != mBeanProperty ? mBeanProperty : "null");
    buf.append(" }");
    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);
  }
}
