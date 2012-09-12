package org.achacha.rules.engine.base;

import org.achacha.rules.base.XmlParseable;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Definition of how the rule should be executed
 */
public class RuleExecDef implements XmlParseable
{
  /** Rule name */
  protected String mName;

  /**
   * Construct rule definition object
   */
  public RuleExecDef()
  {
  }

  /**
   * Construct rule definition object
   * 
   * @param name of rule
   */
  public RuleExecDef(String name)
  {
    mName = name;
  }

  /**
   * Rule name
   * 
   * @return String
   */
  public String getName()
  {
    return mName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return new StringBuilder("RuleExecDef: name=").append(mName).toString();
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root)
  {
    mName = root.getText();
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return toElement(Rule.ELEMENT);
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element root = DocumentHelper.createElement(Rule.ELEMENT);

    root.setText(mName);

    return root;
  }
}
