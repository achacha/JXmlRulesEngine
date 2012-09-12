package org.achacha.rules.engine.factory;

import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.Base;
import org.achacha.rules.engine.base.IncludeDependency;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.statement.StatementsThen;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Wraps If and Then
 */
public class Case implements Base, IncludeDependency
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element */
  public static final String ELEMENT = "Case";

  /** If */
  protected CompareIf mIf;

  /** Then */
  protected StatementsThen mThen = new StatementsThen();

  /**
   * Evaluate If and if true execute the Then
   * 
   * @param ruleContext RuleContext
   * @return true when If evalutes to true and Then was executed
   */
  public boolean execute(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("Evaluating If condition: " + mIf);
    }
    if (mIf.evaluate(ruleContext))
    {
      if (ruleContext.getEvents().isDebugEnabled())
      {
        ruleContext.getEvents().debug("If condition is true, executing THEN: " + mThen);
      }
      mThen.execute(ruleContext);
      return true;
    }
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("If condition is false");
    }
    return false;
  }

  /**
   * Get If
   * 
   * @return CompareIf
   */
  public CompareIf getIf()
  {
    return mIf;
  }

  /**
   * Get Then
   * 
   * @return StatementsThen
   */
  public StatementsThen getThen()
  {
    return mThen;
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
    assert root.getName().equals(ELEMENT) : "Element name must be '" + ELEMENT + "': " + root.asXML();

    // a_Handle required If
    Element eIf = root.element(CompareIf.ELEMENT);
    if (null == eIf)
    {
      throw new RuntimeException("Did not find required 'If' element: " + root.asXML());
    }
    mIf = factory.createCompareIf(eIf);

    // a_Handle optional Then
    mThen.parse(root.element(StatementsThen.ELEMENT), factory);
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
    Element root = DocumentHelper.createElement(elementName);

    root.add(mIf.toElement());
    root.add(mThen.toElement());

    return root;
  }

  /**
   * {@inheritDoc}
   */
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = new JSONObject();

    obj.put(CompareIf.ELEMENT, mIf.toJson());
    obj.put(StatementsThen.ELEMENT, mThen.toJson());

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    mIf.fromJson(source.getJSONObject(CompareIf.ELEMENT), factory);
    mThen.fromJson(source.getJSONObject(StatementsThen.ELEMENT), factory);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append(mIf);
    buf.append("\r\n");
    buf.append(mThen);

    return buf.toString();
  }

  /**
   * Full toString()
   * 
   * @return String
   */
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();
    buf.append(mIf.toStringResolved());
    buf.append("\r\n");
    buf.append(mThen.toStringResolved());

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  public void loadDependencies(RulesEngineLoader loader, Set<String> loaded)
  {
    mIf.loadDependencies(loader, loaded);
    mThen.loadDependencies(loader, loaded);
  }
}
