package org.achacha.rules.engine.value;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.factory.CompareTypeCollection;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.factory.Value;
import org.achacha.rules.engine.factory.ValueCollection;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gets collection in output model from provided xpath
 */
public class ValueCollectionStringOutputModel extends Value implements ValueCollection
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Value type */
  public static final String TYPE = "CollectionStringOutputModel";

  /** Output model path */
  public static final String PATH = "Path";

  /** Xpath into the output model */
  protected String mPath;

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
  @SuppressWarnings("unchecked")
  public List<String> asStringArray(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".asStringArray: output model path=" + mPath);
    }
    Element collection = (Element) ruleContext.getInputModel().selectSingleNode(mPath);
    if (null == collection)
    {
      if (ruleContext.getEvents().isWarnEnabled())
      {
        ruleContext.getEvents().warn("   " + TYPE + ": Path specified not found in the output model:" + mPath);
      }
      return new ArrayList<String>();
    }

    List<Element> valueElements = collection.elements(CompareTypeCollection.ELEMENT_ENTRY);
    if (null != valueElements)
    {
      // a_List of entries
      ArrayList<String> values = new ArrayList<String>(valueElements.size());
      for (Element e : valueElements)
      {
        values.add(e.getText());
      }

      return values;
    }

    // a_Comma delimited
    ArrayList<String> values = new ArrayList<String>();
    StringTokenizer tokens = new StringTokenizer(collection.getText());
    while (tokens.hasMoreTokens())
    {
      values.add(tokens.nextToken());
    }
    return values;
  }

  /**
   * {@inheritDoc}
   * 
   * @return path specified that points to the collection
   */
  @Override
  public String asString(RuleContext ruleContext)
  {
    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".asString: output model path=" + mPath);
    }
    return mPath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isValid(RuleContext ruleContext)
  {
    if (null == mPath)
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", path is missing: " + toElement());
      return false;
    }

    if (null == ruleContext.getOutputModel().selectSingleNode(mPath))
    {
      ruleContext.getEvents().error("Invalid " + TYPE + ", output model node for the path (" + mPath + ") is missing: " + ruleContext.getOutputModel().asXML());
      return false;
    }

    if (ruleContext.getEvents().isDebugEnabled())
    {
      ruleContext.getEvents().debug("   " + TYPE + ".isValid: result=true");
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    mPath = root.elementText(PATH);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Element toElement(String elementName)
  {
    Element e = super.toElement(elementName);

    e.addElement(PATH).addCDATA(mPath);

    return e;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("   ");
    buf.append(TYPE);
    buf.append("{ path={");
    buf.append(null != mPath ? mPath : "null");
    buf.append("} }");
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
    super.fromJson(source, factory);

    mPath = source.getString(PATH);
  }
}
