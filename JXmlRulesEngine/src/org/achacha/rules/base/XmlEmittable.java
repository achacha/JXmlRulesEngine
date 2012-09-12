package org.achacha.rules.base;

import org.dom4j.Element;

/**
 * Xml emittable
 */
public interface XmlEmittable
{
  /** Class attribute */
  String ATTR_CLASS = "Class";

  /** Version attribute */
  String ATTR_VERSION = "Version";

  /**
   * Returns an <tt>org.dom4j.Element</tt> representation of the object. The name of the returned element is set by the implementing class.
   * <p>
   * 
   * @return an <tt>Element</tt> representation of the object
   */
  Element toElement();

  /**
   * Returns an <tt>org.dom4j.Element</tt> representation of the object. The name of the returned element is set by the caller.
   * <p>
   * 
   * @param elementName to use
   * @return an <tt>Element</tt> representation of the object
   */
  Element toElement(String elementName);
}
