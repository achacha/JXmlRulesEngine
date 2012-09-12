package org.achacha.rules.engine.statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.Base;
import org.achacha.rules.engine.base.IncludeDependency;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.ActionType;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Group of actions
 */
public class Statements implements Base, IncludeDependency
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Current version */
  public static final String VERSION = String.valueOf(serialVersionUID);

  /** Element name */
  public static final String ELEMENT_ACTIONS = "Actions";

  /** Type */
  public static final String TYPE = "Actions";

  /** Optional name */
  protected String mName;

  /** List of actions */
  protected List<ActionType> mActions = new LinkedList<ActionType>();

  /**
   * ctor
   */
  public Statements()
  {
  }

  /**
   * {@inheritDoc}
   */
  public String getType()
  {
    return ELEMENT_ACTIONS;
  }

  /**
   * Execute a group of actions and result a result of the first action that requested a redirect Normally null is returned if no rule is redirecting
   * 
   * @param ruleContext RuleContext
   */
  public void execute(RuleContext ruleContext)
  {
    // a_Process actions
    for (ActionType action : mActions)
    {
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("Executing action:\r\n" + action.toString());
      }

      // a_Execute action and action result
      action.execute(ruleContext);
    }

  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings(value = { "unchecked" })
  public void parse(Element root, RulesEngineFactory factory)
  {
    mActions.clear();
    if (null == root)
    {
      mName = StringUtils.EMPTY;
      return;
    }
    mName = StringUtils.defaultString(root.attributeValue(ATTR_NAME), StringUtils.EMPTY);

    List<Element> actions = root.elements(ActionType.ELEMENT);
    for (Element act : actions)
    {
      ActionType action = factory.createActionType(act);
      action.validate();
      mActions.add(action);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
    // a_Initialize actions
    for (ActionType action : mActions)
    {
      action.loadDependencies(loader, loaded);
    }
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element e = DocumentHelper.createElement(elementName);
    for (ActionType action : mActions)
    {
      e.add(action.toElement());
    }
    return e;
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return toElement(getType());
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();
    obj.put(ATTR_NAME, mName);
    obj.put(ATTR_OPERATOR, getType());

    JSONArray array = new JSONArray();
    for (ActionType action : mActions)
    {
      array.put(action.toJson());
    }
    obj.put(TYPE, array);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mActions.clear();
    if (null == source)
    {
      mName = StringUtils.EMPTY;
      return;
    }

    assert source.getString(ATTR_OPERATOR).equals(TYPE) : "Invalid type " + ATTR_OPERATOR + " is " + source.get(ATTR_OPERATOR) + " instead of " + TYPE;

    mName = source.getString(ATTR_NAME);

    JSONArray array = source.getJSONArray(TYPE);
    for (int i = 0; i < array.length(); ++i)
    {
      JSONObject objAction = array.getJSONObject(i);

      String object = objAction.getString(ATTR_OBJECT);
      String type = objAction.getString(ATTR_OPERATOR);
      Base statement = factory.create(object, type);
      if (null != statement)
      {
        statement.fromJson(objAction, factory);
        if (object.equals(ActionType.ELEMENT))
        {
          ActionType action = (ActionType) statement;
          action.validate();
          mActions.add(action);
        }
        else
        {
          throw new RuntimeException("Invalid JSON statement object:" + source.toString(1));
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();

    buf.append(getType());
    buf.append("{\r\n");
    for (ActionType action : mActions)
    {
      buf.append("  ");
      buf.append(action);
    }
    buf.append("\r\n}");

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();

    buf.append(getType());
    buf.append("{\r\n");
    for (ActionType action : mActions)
    {
      buf.append("  ");
      buf.append(action.toStringResolved());
    }
    buf.append("\r\n}");

    return buf.toString();
  }
}
