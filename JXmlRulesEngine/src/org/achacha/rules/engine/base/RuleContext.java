package org.achacha.rules.engine.base;

import java.util.LinkedList;
import java.util.List;

import org.achacha.rules.base.XmlSerializable;
import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.util.EventTimer;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Rule engine context Request model contains input model and optional rulename Response model contains output model Request model <xmp> <request> ... <input> ... </input> <rulename>optional rule to
 * autoload</rulename> </request> </xmp> Response model <xmp> <response> ... <output> ... </output> </response> </xmp>
 */
public class RuleContext implements XmlSerializable
{
  /** Element name */
  public static final String ELEMENT = "RuleContext";

  /** Root element name of the input model inside the request model */
  public static final String REQUEST_MODEL = "request";

  /** Root element name of the input model inside the request model */
  public static final String INPUT_MODEL = "input";

  /** Rule to load from the request model */
  public static final String RULENAME = "rulename";

  /** Root element name of the output model */
  public static final String RESPONSE_MODEL = "response";

  /** Root element name of the output model */
  public static final String OUTPUT_MODEL = "output";

  /** Root element name of the rule data */
  public static final String RULEDATA = "ruledata";

  /** Root element name of the output model */
  public static final String VALUES_ELEMENT = "values";

  /** Validation root */
  public static final String VALIDATION = "validation";

  /** Rules to execute element */
  public static final String RULESTOEXECUTE_ELEMENT = "rules-to-execute";

  /** Stop on first true condition */
  public static final String STOPONFIRSTTRUE = "StopOnFirstTrue";

  /** Error */
  public static final String ERROR = "error";

  /** Error */
  public static final String MESSAGE = "message";

  /** Request model */
  protected Element mRequestModel;

  /** Input model */
  protected Element mInputModel;

  /** Response model */
  protected Element mResponseModel;

  /** Output model */
  protected Element mOutputModel;

  /** Rule being evaluated */
  protected Rule mActiveRule;

  /** List of events */
  protected EventVisitor mEvents;

  /** Event timer */
  protected EventTimer mTimer;

  /** Rule factory */
  protected RulesEngineLoader mLoader;

  /** Rule (path) to execute */
  protected List<RuleExecDef> mRulesToExecute = new LinkedList<RuleExecDef>();

  /** if the execution should stop on first TRUE response in execution of a rule */
  protected boolean mStopOnFirstTrue;

  /** No default ctor, MUST have RulesEngineLoader */
  protected RuleContext()
  {
  }

  /**
   * ctor Will look for: 'input' element in requestModel to get input model 'rulename' to name of the rule to auto-load using the loader
   * 
   * @param loader to use
   * @param requestModel Element
   */
  @SuppressWarnings("unchecked")
  public RuleContext(RulesEngineLoader loader, Element requestModel)
  {
    mLoader = loader;
    mEvents = loader.getEventVisitor();
    mTimer = loader.getEventTimer();

    if (!requestModel.getName().equals(REQUEST_MODEL))
    {
      throw new RuntimeException("Request model root element must be called: " + REQUEST_MODEL);
    }

    mRequestModel = requestModel;

    Element input = requestModel.element(INPUT_MODEL);
    if (null != input)
    {
      if (mEvents.isDebugEnabled())
      {
        mEvents.debug("RuleContext: Found input model in request");
      }
      mInputModel = input;
      mInputModel.setParent(null);
    }
    else
    {
      if (mEvents.isDebugEnabled())
      {
        mEvents.debug("RuleContext: Input model not found in request, creating empty default");
      }
      mInputModel = DocumentHelper.createElement(INPUT_MODEL);
    }

    // a_Logging level override
    Element eLoggingLevel = requestModel.element(EventVisitor.ELEMENT);
    if (null != eLoggingLevel)
    {
      String level = eLoggingLevel.getTextTrim();
      if (mEvents.isDebugEnabled())
      {
        mEvents.debug("RuleContext: Overriding logging level to: " + level);
      }
      mEvents.setLoggingLevel(Integer.parseInt(level));
    }

    // a_Parse which rules to execute
    Element eRulesToExecute = requestModel.element(RuleContext.RULESTOEXECUTE_ELEMENT);
    if (null == eRulesToExecute)
    {
      mEvents.error("Expects '" + RuleContext.RULESTOEXECUTE_ELEMENT + "' element: " + requestModel.asXML());
    }
    else
    {
      for (Element eRuleToExec : (List<Element>) eRulesToExecute.elements(Rule.ELEMENT))
      {
        RuleExecDef ruledef = new RuleExecDef();
        ruledef.parse(eRuleToExec);
        mRulesToExecute.add(ruledef);
      }
      if (0 == mRulesToExecute.size())
      {
        mEvents.error("Expects '" + RuleContext.RULESTOEXECUTE_ELEMENT + "/" + Rule.ELEMENT + "' element to specify which rules to execute: " + requestModel.asXML());
      }
    }

    // a_Check if we are to stop after first true test
    mStopOnFirstTrue = (null != requestModel.element(STOPONFIRSTTRUE));

    // a_Create response model and output model inside it
    mResponseModel = DocumentHelper.createElement(RESPONSE_MODEL);
    mOutputModel = mResponseModel.addElement(OUTPUT_MODEL);
  }

  /**
   * Get loader
   * 
   * @return RulesEngineLoader
   */
  public RulesEngineLoader getLoader()
  {
    return mLoader;
  }

  /**
   * Get request model
   * 
   * @return Element
   */
  public Element getRequestModel()
  {
    return mRequestModel;
  }

  /**
   * Get input model
   * 
   * @return Element
   */
  public Element getInputModel()
  {
    return mInputModel;
  }

  /**
   * Get response model
   * 
   * @return Element
   */
  public Element getResponseModel()
  {
    return mResponseModel;
  }

  /**
   * Get output model
   * 
   * @return Element
   */
  public Element getOutputModel()
  {
    return mOutputModel;
  }

  /**
   * Set Rule being executed
   * 
   * @param rule Rule
   */
  public void setActiveRule(Rule rule)
  {
    mActiveRule = rule;
  }

  /**
   * Get Rule being executed
   * 
   * @return Rule
   */
  public Rule getActiveRule()
  {
    return mActiveRule;
  }

  /**
   * List of events during rule execution
   * 
   * @return EventVisitor
   */
  public EventVisitor getEvents()
  {
    return mEvents;
  }

  /**
   * Get timer
   * 
   * @return EventTimer
   */
  public EventTimer getTimer()
  {
    return mTimer;
  }

  /**
   * Get list of rules to execute
   * 
   * @return List
   */
  public List<RuleExecDef> getRulesToExecute()
  {
    return mRulesToExecute;
  }

  /**
   * Set if should stop after first TRUE response in rule execution
   * 
   * @param b if to stop
   */
  public void setStopOnFirstTrue(boolean b)
  {
    mStopOnFirstTrue = b;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isStopOnFirstTrue()
  {
    return mStopOnFirstTrue;
  }

  /**
   * {@inheritDoc}
   */
  public void parse(Element root, RulesEngineFactory factory)
  {
    throw new RuntimeException("Parsing from XML is not supported.");
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
    Element root = DocumentHelper.createElement(ELEMENT);

    root.add(mRequestModel.createCopy());
    root.add(mResponseModel.createCopy());
    root.add(mEvents.toElement());
    root.add(mLoader.toElement());
    if (null != mActiveRule)
    {
      root.add(mActiveRule.toElement());
    }

    return root;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();

    builder.append("RuleContext {\r\n");
    builder.append(":RULE:");
    builder.append(null == mActiveRule ? "null" : RulesEngineHelper.toPrettyPrintXml(mActiveRule.toElement()));
    builder.append(":RULE_RESOLVED:");
    builder.append(null == mActiveRule ? "null" : mActiveRule.toStringResolved());
    builder.append("\r\n:INPUT:");
    builder.append(RulesEngineHelper.toPrettyPrintXml(mRequestModel));
    builder.append("\r\n:OUTPUT:");
    builder.append(RulesEngineHelper.toPrettyPrintXml(mResponseModel));
    builder.append("\r\n:LOADER:\r\n");
    builder.append(mLoader);
    builder.append(":EVENTS:\r\n");
    builder.append(mEvents);
    builder.append("\r\n}");

    return builder.toString();
  }

  /**
   * Get the current project scope
   * 
   * @return Project scope
   */
  public String getProjectScope()
  {
    return mLoader.getProjectScope();
  }
}
