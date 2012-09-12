package org.achacha.rules.engine.processor;

import java.util.List;

import org.achacha.rules.engine.base.RuleContext;
import org.dom4j.Element;

/**
 * Rules Engine processors
 */
public class RulesEngineProcessors
{
  /**
   * Internal unit test method (meant only for testing)
   * Provided as a sample of how to add your own processor
   * 
   * 
   * 
   * @param ruleContext RuleContext
   * @param processorXml from request
   */
  @SuppressWarnings("unchecked")
  public void internalTest(RuleContext ruleContext, Element processorXml)
  {
    ruleContext.getOutputModel().addElement("internalTest").addText("Frobozz");
    Element xml = ruleContext.getOutputModel().addElement("processor");
    for (Element e : (List<Element>) processorXml.elements())
    {
      xml.add(e.createCopy());
    }
  }
}
