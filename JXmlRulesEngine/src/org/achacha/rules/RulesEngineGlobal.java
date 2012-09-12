
package org.achacha.rules;

import java.io.InputStream;
import java.util.Properties;

import org.achacha.rules.engine.RulesEngine;
import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.RulesEngineFactory;
import org.achacha.rules.engine.impl.RulesEngineLoaderImplFileSystem;
import org.apache.log4j.PropertyConfigurator;

/**
 * Global data for the CLI application or as a helper class to do all the initialization
 * 
 * How to use: see RulesMainCLI for an example
 * 
 * 1. Call RulesEngineGlobal.initLog4j() to initialize Log4j (unless you do this some other way)
 * 2. Once you know where the base path is, call create(path_to_base_rules_directory)
 */
public class RulesEngineGlobal
{
  /**
   * Singleton available only after create is called
   * 
   * @return RulesEngineGlobal or null if you forgot to call create method
   */
  synchronized public static final RulesEngineGlobal getInstance()
  {
    return mGlobal;
  }
  
  /** One */
  private static RulesEngineGlobal mGlobal;

  /** Main factory */
  protected RulesEngineFactory mFactory;

  /** Associated loader */
  private RulesEngineLoader mLoader;

  /** Rules engine */
  private RulesEngine mRulesEngine;
  
  /**
   * no default ctor
   */
  protected RulesEngineGlobal() {}
  
  /**
   * @param basePath
   */
  protected RulesEngineGlobal(String basePath)
  {
    mFactory = new RulesEngineFactory();
    mLoader = new RulesEngineLoaderImplFileSystem(mFactory, basePath);
    mRulesEngine = new RulesEngine(mLoader);
  }

  /** @return RulesEngineFactory */
  public RulesEngineFactory getFactory() { return mFactory; }
  /** @return RulesEngineLoader */
  public RulesEngineLoader getLoader() { return mLoader; }
  /** @return RulesEngine */
  public RulesEngine getRulesEngine() { return mRulesEngine; }
  
  /**
   * NOTE: This MUST be called before using the singleton
   */
  public static void create(String basePath)
  {
    // Create singleton instance
    mGlobal = new RulesEngineGlobal(basePath);
  }

  /**
   * Initialize Log4j
   */
  public static void initLog4j()
  {
    try
    {
      InputStream is = RulesEngineGlobal.class.getResourceAsStream("/log4j.properties");
      if (null == is)
      {
        throw new RuntimeException("Failed to find /log4j.properties in the classpath");
      }
      Properties properties = new Properties();

      properties.load(is);
      PropertyConfigurator.configure(properties);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to parse/load log4j.properties, check to make sure the file is valid", e);
    }
  }

}
