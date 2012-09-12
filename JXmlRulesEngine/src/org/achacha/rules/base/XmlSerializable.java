package org.achacha.rules.base;

import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.dom4j.Element;

/**
 * <tt>XmlSerializable</tt> interface defines an object which is able to build an <tt>org.dom4j.Element</tt> representation of itself.
 */
public interface XmlSerializable extends XmlEmittable
{
  /**
   * Returns an <tt>org.dom4j.Element</tt> representation of the object. The given <tt>elementName</tt> is used as the name of the element that is returned.
   * <p>
   * 
   * @param root the name to give to the returned <tt>Element</tt>
   * @param factory to use for building parts
   */
  void parse(Element root, RulesEngineFactory factory);
}
