package org.achacha.rules.engine.factory;

import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base compare type class for collections
 */
public abstract class CompareTypeCollection extends CompareType
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Collection entry element */
  public static final String ELEMENT_ENTRY = "Entry";

  /** Collection entry key attribute */
  public static final String ATTR_KEY = "Key";

  /** Collection index start attribute */
  public static final String ATTR_INDEX_START = "IndexStart";

  /** Collection index end attribute */
  public static final String ATTR_INDEX_END = "IndexEnd";

  /** Start index */
  protected int mIndexStart = 0;

  /** End index, Integer.MAX_VALUE denotes end of collection */
  protected int mIndexEnd = Integer.MAX_VALUE;

  /**
   * {@inheritDoc}
   */
  @Override
  public void parse(Element root, RulesEngineFactory factory)
  {
    super.parse(root, factory);
    String str = root.attributeValue(ATTR_INDEX_START);
    if (null != str)
    {
      mIndexStart = Integer.parseInt(str);
    }
    str = root.attributeValue(ATTR_INDEX_END);
    if (null != str)
    {
      mIndexEnd = Integer.parseInt(str);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public JSONObject toJson() throws JSONException
  {
    JSONObject obj = super.toJson();

    obj.put(ATTR_INDEX_START, mIndexStart);
    obj.put(ATTR_INDEX_END, mIndexEnd);

    return obj;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void fromJson(JSONObject source, RulesEngineFactory factory) throws JSONException
  {
    super.fromJson(source, factory);
    if (source.has(ATTR_INDEX_START))
    {
      mIndexStart = source.getInt(ATTR_INDEX_START);
    }
    if (source.has(ATTR_INDEX_END))
    {
      mIndexEnd = source.getInt(ATTR_INDEX_END);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element root = super.toElement(elementName);

    root.addAttribute(ATTR_INDEX_START, Integer.toString(mIndexStart));
    root.addAttribute(ATTR_INDEX_END, Integer.toString(mIndexEnd));

    return root;
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
      buf.append(",NOT");
    }
    if (mIgnoreCase)
    {
      buf.append(",NOCASE");
    }
    buf.append(",INDEX_START=");
    buf.append(mIndexStart);
    buf.append(",INDEX_END=");
    buf.append(mIndexEnd);
    buf.append(")  { \r\n  ");
    for (Value value : mValues)
    {
      buf.append("  ");
      buf.append(value.toString());
      buf.append("\r\n");
    }
    buf.append("\r\n  }");
    return buf.toString();
  }
}
