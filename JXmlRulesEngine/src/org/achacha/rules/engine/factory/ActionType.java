package org.achacha.rules.engine.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.action.ActionTypeInclude;
import org.achacha.rules.engine.action.ActionTypeOutputValueAdd;
import org.achacha.rules.engine.action.ActionTypeOutputValueSet;
import org.achacha.rules.engine.action.ActionTypeUnknown;
import org.achacha.rules.engine.base.Base;
import org.achacha.rules.engine.base.IncludeDependency;
import org.achacha.rules.engine.base.RuleContext;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base Action type
 */
public abstract class ActionType implements Base, IncludeDependency
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element name */
  public static final String ELEMENT = "Action";

  /** Map action type to implementation */
  private static HashMap<String, String> mActionClasses = new HashMap<String, String>();

  static
  {
    mActionClasses.put(ActionTypeInclude.TYPE, ActionTypeInclude.class.getName());
    mActionClasses.put(ActionTypeOutputValueAdd.TYPE, ActionTypeOutputValueAdd.class.getName());
    mActionClasses.put(ActionTypeOutputValueSet.TYPE, ActionTypeOutputValueSet.class.getName());
    mActionClasses.put(ActionTypeUnknown.TYPE, ActionTypeUnknown.class.getName());
  }

  /**
   * Accessed by the factory during initialization
   * 
   * @return map of type to class path string
   */
  static Map<String, String> getClassMap()
  {
    return mActionClasses;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void parse(Element root, RulesEngineFactory factory)
  {
    for (Element e : (List<Element>) root.elements(Base.ELEMENT_EDITOR))
    {
      e.detach();
    }
  }

  /**
   * Execute the action
   * 
   * @param ruleContext RuleContext
   */
  public abstract void execute(RuleContext ruleContext);

  /**
   * Get type name
   * 
   * @return type
   */
  public abstract String getType();

  /**
   * Runs validation may throw Exception type if validation fails
   */
  public abstract void validate();

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
  }

  /**
   * Clone self
   * 
   * @param factory RulesEngineFactory
   * @return ActionType
   */
  public ActionType clone(RulesEngineFactory factory)
  {
    ActionType clone = factory.createActionType(toElement());
    return clone;
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
    Element e = DocumentHelper.createElement(elementName);

    return e;
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();

    obj.put(ATTR_OBJECT, ELEMENT);
    obj.put(ATTR_OPERATOR, getType());

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    assert source.getString(ATTR_OBJECT).equals(ELEMENT) : "Invalid type " + ATTR_OBJECT + "=" + source.getString(ATTR_OBJECT) + " expected " + ELEMENT;
    assert source.getString(ATTR_OPERATOR).equals(getType()) : "Invalid type " + ATTR_OPERATOR + "=" + source.getString(ATTR_OPERATOR) + " expected " + getType();
  }
}
