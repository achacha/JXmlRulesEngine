package org.achacha.rules.engine.factory;

import java.util.List;

import org.achacha.rules.engine.base.Base;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Static XML content contained in Content element Root element is always Content When published root is never added, only children
 */
public class ConstantContent implements Base
{
  /** serial id */
  private static final long serialVersionUID = 1L;

  /** Bean used to get value */
  public static final String ELEMENT = "Content";

  /** XML content */
  protected Element mContent;

  /**
   * ctor
   */
  public ConstantContent()
  {
  }

  /**
   * ctor
   * 
   * @param root Element which must be Content
   */
  public ConstantContent(Element root)
  {
    assert root.getName().equals(ELEMENT) : "Root element must be '" + ELEMENT + "'";
    mContent = root.createCopy();
  }

  /**
   * Public the static content to the root element
   * 
   * @param root Element
   */
  @SuppressWarnings("unchecked")
  public void publish(Element root)
  {
    for (Node n : (List<Node>) mContent.content())
    {
      root.add((Node) n.clone());
    }
  }

  /**
   * Access content XML
   * 
   * @return Element
   */
  public Element getContent()
  {
    return mContent;
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
  public void parse(Element root, RulesEngineFactory factory)
  {
    assert root.getName().equals(ELEMENT) : "Root element must be '" + ELEMENT + "'";
    mContent = root.createCopy();
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return mContent.createCopy();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Element toElement(String elementName)
  {
    Element root = DocumentHelper.createElement(elementName);
    for (Node n : (List<Node>) mContent.content())
    {
      root.add(n);
    }
    return root;
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();
    obj.put(ELEMENT, mContent.asXML());
    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    String content = source.getString(ELEMENT);
    if (null != content)
    {
      try
      {
        mContent = DocumentHelper.parseText(content).getRootElement();
      }
      catch (DocumentException e)
      {
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return mContent.asXML();
  }
}
