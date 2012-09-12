package org.achacha.rules.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.impl.RulesEngineLoaderImplFileSystem;

/**
 * Base class for testing rules engine Loads the spring context To make this work add: /etc to the classpath of the JUnit configuration
 */
public class RulesEngineUnitTestBase extends TestCase
{
  /** Extension of the rule */
  public static final String EXT_RULE = ".rule";

  /** Extension of the rule conditions fragment */
  public static final String EXT_CONDITIONS = ".conditions";

  /** Extension of the rule actions fragment */
  public static final String EXT_ACTIONS = ".actions";

  /** Context files to load */
  protected static final String[] SPRING_CONTEXTS = { "context/cache-context.xml", "context/rulesengine-context.xml" };

  /** factory */
  protected RulesEngineFactory mFactory = new RulesEngineFactory();

  /** properties */
  protected Properties mProperties;

  /** Test case extension */
  protected static final String EXT_TESTCASE = ".xml";

  protected String mBaseRuleDir;
  protected String mBaseConditionsDir;
  protected String mBaseActionsDir;
  protected String mBaseTestCaseDir;

  /**
   * Rules engine based on file system loader in the config Lazy init during call to getter
   */
  private RulesEngine mRulesEngine;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();

    mProperties = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/test.properties");
    if (null == is)
    {
      throw new RuntimeException("Unable to find test.properties in the classpath, please add testdata directory to classpath of this test");
    }
    assertNotNull(is);
    try
    {
      mProperties.load(is);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException("Failed to load test.properties", e);
    }
    URL url = this.getClass().getResource("/test.properties");
    // a_Get path and remove filename
    String pathfile = url.getPath();
    int slashpos = pathfile.lastIndexOf('/');
    if (-1 == slashpos)
    {
      throw new RuntimeException("Could not determine path for resource (check your classpath): " + pathfile);
    }
    String basepath = pathfile.substring(0, slashpos + 1);
    // System.out.println("BASE_PATH="+basepath);

    mBaseRuleDir = basepath + mProperties.getProperty("unittest.ruledata.rule.dir");
    mBaseConditionsDir = basepath + mProperties.getProperty("unittest.ruledata.conditions.dir");
    mBaseActionsDir = basepath + mProperties.getProperty("unittest.ruledata.actions.dir");
    mBaseTestCaseDir = basepath + mProperties.getProperty("unittest.ruledata.testcases.dir");
  }

  /**
   * Get file system based rules engine
   * 
   * @return RulesEngine
   */
  RulesEngine getFileSystemRulesEngine()
  {
    if (null == mRulesEngine)
    {
      mRulesEngine = new RulesEngine(getFileSystemLoader());
      
      // Add this custom processor that is referenced in the test case
      mRulesEngine.getLoader().getFactory().getProcessorFactory().addProcessor("internalTest", "org.achacha.rules.engine.processor.RulesEngineProcessors.internalTest");

    }
    return mRulesEngine;
  }

  /**
   * Get loader for filesystem
   * 
   * @return RulesEngineLoader
   */
  public RulesEngineLoader getFileSystemLoader()
  {
    RulesEngineLoaderImplFileSystem loader = new RulesEngineLoaderImplFileSystem(mFactory, mBaseRuleDir, mBaseConditionsDir, mBaseActionsDir);

    return loader;
  }

  /**
   * Get all rule names recursively
   * 
   * @return list of all rule names in a given directory
   */
  public List<String> getAllTestCases()
  {
    List<String> names = new LinkedList<String>();
    getInDir(new File(mBaseTestCaseDir), "", names, EXT_TESTCASE);
    return names;
  }

  /**
   * Get all rule names recursively
   * 
   * @return list of all rule names in a given directory
   */
  public List<String> getAllRules()
  {
    List<String> names = new LinkedList<String>();
    getInDir(new File(mBaseRuleDir), "", names, EXT_RULE);
    return names;
  }

  /**
   * Get all rule names for a given directory
   * 
   * @param dir File to search
   * @param basename of the rule
   * @param rulenames to add rule names to
   * @param extension to filter on
   */
  protected void getInDir(File dir, String basename, List<String> rulenames, String extension)
  {
    File[] rulefiles = dir.listFiles();
    if (null == rulefiles)
    {
      return;
    }
    for (int i = 0; i < rulefiles.length; ++i)
    {
      if (rulefiles[i].isDirectory())
      {
        // Directory
        getInDir(rulefiles[i], basename + '/' + rulefiles[i].getName(), rulenames, extension);
      }
      else if (rulefiles[i].getName().endsWith(extension))
      {
        // Rule found
        String name = rulefiles[i].getName();
        name = name.substring(0, name.length() - extension.length());
        if (basename.length() > 0)
        {
          rulenames.add(basename + '/' + name);
        }
        else
        {
          rulenames.add(name);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();

    builder.append("\r\nCWD=");
    File file = new File(".");
    builder.append(file.getAbsolutePath());

    builder.append("\r\nBaseRuleDir=");
    builder.append(mBaseRuleDir);
    builder.append("\r\nBaseConditionsDir=");
    builder.append(mBaseConditionsDir);
    builder.append("\r\nBaseActionsDir=");
    builder.append(mBaseActionsDir);
    builder.append("\r\nBaseTestCaseDir=");
    builder.append(mBaseTestCaseDir);

    return builder.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
}
