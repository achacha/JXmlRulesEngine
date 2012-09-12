package org.achacha.rules.engine.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.achacha.rules.engine.base.RuleContext;
import org.dom4j.Element;

/**
 * Factory for creating PojoProcessor objects by name
 */
public class RulesEngineProcessorFactory
{
  /** pre-processor element name */
  public static final String ELEMENT_PRE_PROCESSOR = "pre-processor";

  /** post-processor element name */
  public static final String ELEMENT_POST_PROCESSOR = "post-processor";

  /** Name of the processor */
  public static final String ATTR_PROCESSOR_NAME = "Name";

  /** map of name to processors */
  protected Map<String, RulesEnginePojoProcessor> mProcessors = new HashMap<String, RulesEnginePojoProcessor>();

  /**
   * Default ctor
   */
  public RulesEngineProcessorFactory()
  {
  }

  /**
   * Ctor used to add more processors
   * Name to POJO path (package.object.method) format
   * 
   * @param processors name to object.method map
   */
  public RulesEngineProcessorFactory(Map<String, String> processors)
  {
    mProcessors = new HashMap<String, RulesEnginePojoProcessor>();

    // Add the processors provided
    for (String name : processors.keySet())
    {
      String objectMathod = processors.get(name);
      mProcessors.put(name, new RulesEnginePojoProcessor(objectMathod));
    }
  }

  /**
   * Add more processors
   * 
   * e.g. 
   *  engine.getLoader().getFactory().getProcessorFactory().addProcessor("internalTest", "org.achacha.rules.engine.processor.RulesEngineProcessors.internalTest");
   * 
   * @param name of the processor
   * @param objectMethod POJO processor path
   */
  public void addProcessor(String name, String objectMethod)
  {
    mProcessors.put(name, new RulesEnginePojoProcessor(objectMethod));
  }

  /**
   * Add more processors
   * 
   * @param name of the processor
   * @param processor POJO processor
   */
  public void addProcessor(String name, RulesEnginePojoProcessor processor)
  {
    mProcessors.put(name, processor);
  }
  
  /**
   * Get Pojo processor
   * 
   * @param name of processor
   * @return RulesEnginePojoProcessor
   */
  public RulesEnginePojoProcessor get(String name)
  {
    return mProcessors.get(name);
  }

  /**
   * Execute pre-processors
   * 
   * @param ruleContext RuleContext
   */
  public void executePreProcessors(RuleContext ruleContext)
  {
    executeProcessors(ELEMENT_PRE_PROCESSOR, ruleContext);
  }

  /**
   * Execute post-processors
   * 
   * @param ruleContext RuleContext
   */
  public void executePostProcessors(RuleContext ruleContext)
  {
    executeProcessors(ELEMENT_POST_PROCESSOR, ruleContext);

  }

  /**
   * Execute processors
   * 
   * @param elementName of processors
   * @param ruleContext RuleContext
   */
  @SuppressWarnings("unchecked")
  protected void executeProcessors(String elementName, RuleContext ruleContext)
  {
    List<Element> processors = (List<Element>) ruleContext.getRequestModel().elements(elementName);
    for (Element processor : processors)
    {
      String name = processor.attributeValue(ATTR_PROCESSOR_NAME);
      RulesEnginePojoProcessor pojo = get(name);
      if (null == pojo)
      {
        throw new RuntimeException("RulesEngineProcessorFactory: Processor not found: " + name + " in " + processor.asXML() + " available: " + mProcessors.keySet());
      }
      else
      {
        pojo.execute(ruleContext, processor);
      }
    }
  }
}
