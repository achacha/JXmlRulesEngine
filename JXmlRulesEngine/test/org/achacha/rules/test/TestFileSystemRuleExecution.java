
package org.achacha.rules.test;

import java.io.File;
import java.util.List;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.base.RuleContext;
import org.achacha.rules.engine.base.RulesEngineHelper;
import org.achacha.rules.testbase.RulesEngineUnitTestBase;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.Document;
import org.dom4j.Element;

/**
 * Test rule execution from file system
 *
 * To make this work add: ./testdata to the classpath of the JUnit configuration
 */
public class TestFileSystemRuleExecution extends RulesEngineUnitTestBase
{
    /** test */
    public void testExecuteTestCasesOnFileSystem()
    {
        RulesEngine engine = getFileSystemRulesEngine();

        List<String> names = getAllTestCases();
        if (0 == names.size())
        {
            throw new RuntimeException("Unable to find any test cases in: "+this);
        }
        for (String name : names)
        {
            executeTestCase(engine, mBaseTestCaseDir+name+EXT_TESTCASE);
        }
    }
    
    /**
     * Execute test case
     * 
     * @param engine RulesEngine
     * @param testcasePath to execute
     */
    private void executeTestCase(RulesEngine engine, String testcasePath)
    {
        Document doc = RulesEngineHelper.loadXmlDocument(new File(testcasePath));
        
        //_Build rule context
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
            System.out.println("FAIL for testcase: "+testcasePath);
            System.out.println("RuleContext:\r\n"+ruleContext);
            throw new RuntimeException(
                "For: "
                +testcasePath
                +"\r\nOutput models do not match:\r\n--------------------------------\r\n"
                +one.asXML()
                +"\r\n--------------------------------\r\n"
                +two.asXML()
                +"\r\n--------------------------------\r\n");  
        }

        System.out.println("PASSED for testcase: "+testcasePath);
    }
}
