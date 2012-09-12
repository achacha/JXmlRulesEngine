package org.achacha.rules.engine.condition;

import java.util.List;
import java.util.Set;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareIf;
import org.achacha.rules.engine.factory.CompareType;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.dom4j.Element;

/**
 * Fragment <Compare Type="IfFragment" Path="frag/southwest.if.xml"/>
 */
public class CompareTypeInclude extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Compare type */
  public static final String TYPE = "Include";

  /** Meta type */
  public static final String META = META_FILE;

  /** Operand count */
  public static final int COUNT = 0;

  /** Relative path of the rule */
  public static final String PATH = "Path";

  /** Relative path of the rule */
  protected String mPath = null;

  /** Fragment */
  protected CompareIf mIf = null;

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
  public boolean evaluate(RuleContext ruleContext)
  {
    boolean result = mIf.evaluate(ruleContext);
    return result ^ mNot;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getExpectedValueCount()
  {
    return COUNT;
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
      loader.getEventVisitor().error("Cyclic dependency detected while loading conditions: " + mPath + " from: " + toString());
      throw new RuntimeException("Cyclic dependency detected while loading conditions: " + mPath + " from: " + toString());
    }

    if (loader.getEventVisitor().isDebugEnabled())
    {
      loader.getEventVisitor().debug("Trying to load dependent conditions: " + mPath + " from: " + toString());
    }

    // a_Load condition and add to loaded set
    mIf = loader.getConditions(mPath);
    if (null == mIf)
    {
      loader.getEventVisitor().error("Conditions not found by the loader: " + mPath);
      throw new RuntimeException("Conditions not found by the loader: " + mPath);
    }
    loaded.add(mPath);

    // a_Resolve deeper dependencies
    mIf.loadDependencies(loader, loaded);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element root = super.toElement(elementName);
    root.addElement(PATH).setText(mPath);
    return root;
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
  public String getExpectedValueType()
  {
    return META;
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
    buf.append(getType());
    if (mNot)
    {
      buf.append(",");
      buf.append("NOT");
    }
    if (mIgnoreCase)
    {
      buf.append(",");
      buf.append("NOCASE");
    }
    buf.append(")  path=");
    buf.append(mPath);

    buf.append("  If=");
    buf.append(null == mIf ? "null" : mIf.toString());
    buf.append('}');

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toStringResolved()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("  ");
    buf.append(ELEMENT);
    buf.append("(");
    buf.append(getType());
    if (mNot)
    {
      buf.append(",");
      buf.append("NOT");
    }
    if (mIgnoreCase)
    {
      buf.append(",");
      buf.append("NOCASE");
    }
    buf.append(")  path=");
    buf.append(mPath);
    buf.append("  conditions={ ");
    buf.append(mIf.toStringResolved());
    buf.append('}');

    return buf.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Value> getValues()
  {
    return mIf.getValues();
  }
}