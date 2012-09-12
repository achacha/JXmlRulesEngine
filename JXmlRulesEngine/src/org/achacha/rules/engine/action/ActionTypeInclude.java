package org.achacha.rules.engine.action;

import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.ActionType;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.statement.Statements;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Include other actions action
 */
public class ActionTypeInclude extends ActionType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Type */
  public static final String TYPE = "Include";

  /** Name attribute */
  public static final String PATH = "Path";

  /** Relative path of the actions */
  protected String mPath = null;

  /** Actions included */
  protected Statements mActions = null;

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType()
  {
    return TYPE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
    // a_Check for cycle
    if (loaded.contains(mPath))
    {
      loader.getEventVisitor().error("Cyclic dependency detected while loading actions: " + mPath + " from: " + toString());
      throw new RuntimeException("Cyclic dependency detected while loading: " + mPath + " from: " + toString());
    }

    if (loader.getEventVisitor().isDebugEnabled())
    {
      loader.getEventVisitor().debug("Trying to load dependent actions: " + mPath + " from: " + toString());
    }

    // a_Load actions and add to loaded set
    mActions = loader.getActions(mPath);
    if (null == mActions)
    {
      loader.getEventVisitor().error("Actions not found by the loader: " + mPath);
      throw new RuntimeException("Actions not found by the loader: " + mPath);
    }
    loaded.add(mPath);

    // a_Resolve deeper dependency
    mActions.loadDependencies(loader, loaded);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute(RuleContext ruleContext)
  {
    mActions.execute(ruleContext);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void parse(Element root, RulesEngineFactory factory)
  {
    super.parse(root, factory);
    mPath = root.element(PATH).getText();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void validate()
  {
    if (null == mPath)
    {
      throw new IllegalArgumentException("Missing element '" + PATH + "' in: " + toString());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addAttribute(ATTR_OPERATOR, TYPE);
    e.addElement(PATH).setText(mPath);

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("  ");
    buf.append(ELEMENT);
    buf.append("(");
    buf.append(TYPE);
    buf.append(")  path=");
    buf.append(mPath);

    buf.append("  Actions=");
    buf.append(null == mActions ? "null" : mActions.toString());
    buf.append('}');

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("  ");
    buf.append(ELEMENT);
    buf.append("(");
    buf.append(TYPE);
    buf.append(")  path=");
    buf.append(mPath);
    buf.append("actions={");
    buf.append(mActions.toStringResolved());
    buf.append('}');

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(PATH, mPath);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mPath = source.getString(PATH);
  }
}
