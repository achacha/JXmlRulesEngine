package org.achacha.rules.engine.base;

import java.io.Serializable;

import org.achacha.rules.base.JsonEmittable;
import org.achacha.rules.base.JsonParseable;
import org.achacha.rules.base.XmlSerializable;

/**
 * Base interface for all rule parts
 */
public interface Base extends XmlSerializable, Serializable, JsonEmittable, JsonParseable
{
  /** String metadata */
  String META_STRING = "String";

  /** Date metadata */
  String META_DATE = "Date";

  /** Number metadata */
  String META_NUMBER = "Number";

  /** Boolean metadata */
  String META_BOOLEAN = "Boolean";

  /** File metadata */
  String META_FILE = "File";

  /** Object attribute */
  String ATTR_OBJECT = "Object";

  /** Type/operator attribute */
  String ATTR_OPERATOR = "Operator";

  /** Name attribute */
  String ATTR_NAME = "Name";

  /** Editor element that the engine ignores */
  String ELEMENT_EDITOR = "Editor";

  /**
   * Get the type of the object
   * 
   * @return type
   */
  String getType();
}
