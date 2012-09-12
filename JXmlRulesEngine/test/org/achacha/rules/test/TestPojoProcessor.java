
package org.achacha.rules.test;

import java.io.File;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Test POJO processor
 */
public class TestPojoProcessor extends RulesEngineUnitTestBase
{
    /** Test case for this run */
    public static final String TEST_CASE = "TestCase_PojoProcessor";
    
    /** test */
    public void testExecutePojoProcessors()
    {
        RulesEngine engine = getFileSystemRulesEngine();
                
        // Request document from testcase
        Document doc = RulesEngineHelper.loadXmlDocument(new File(mBaseTestCaseDir+TEST_CASE+EXT_TESTCASE));
        
        // RuleContext
        RuleContext ruleContext = engine.createContext(doc.getRootElement(), EventVisitor.ERROR);
        
        // Manual step to publish static data
        engine.publishContentToInputModel(ruleContext);
        
        //execute rule using file system loader with input, compare to output
        engine.execute(ruleContext);

        //a_Validate output model
        Element one = doc.getRootElement().element(RuleContext.OUTPUT_MODEL);
        Element two = ruleContext.getOutputModel();
        
        // Remove EventVisitor before comparison
        Element eEventVisitor = ruleContext.getOutputModel().element(EventVisitor.ELEMENT);
        if (null != eEventVisitor)
        {
            System.out.println("[===========================================================================================]");
            System.out.println(RulesEngineHelper.toPrettyPrintXml(eEventVisitor));
            System.out.println("[===========================================================================================]\r\n");
            eEventVisitor.detach();
        }
        else if (ruleContext.getEvents().size() > 0)
        {
            System.out.println("[===========================================================================================]");
            System.out.println(RulesEngineHelper.toPrettyPrintXml(ruleContext.getEvents().toElement()));
            System.out.println("[===========================================================================================]\r\n");
        }

        if (!RulesEngineHelper.compareElements(one, two))
        {
            System.out.println("FAIL for testcase: TestCase_PojoProcessor");
            System.out.println("RuleContext:\r\n"+ruleContext);
            throw new RuntimeException(
                "For: TestCase_PojoProcessor"
                +"\r\nOutput models do not match:\r\n--------------------------------\r\n"
                +one.asXML()
                +"\r\n--------------------------------\r\n"
                +two.asXML()
                +"\r\n--------------------------------\r\n");  
        }

        System.out.println("PASSED for testcase: TestCase_PojoProcessor");
    }

}
