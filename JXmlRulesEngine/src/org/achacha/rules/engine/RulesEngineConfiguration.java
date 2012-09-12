package org.achacha.rules.engine;

/**
 * Rules engine configuration object
 */
public class RulesEngineConfiguration
{
  /** Spring bean name */
  public static final String BEAN = "rulesengine.configuration";

  /** rules directory */
  private String mBaseRulesDirectory;

  /** conditions directory */
  private String mBaseConditionsDirectory;

  /** actions directory */
  private String mBaseActionsDirectory;

  /**
   * @return the base rules directory
   */
  public String getBaseRulesDirectory()
  {
    return mBaseRulesDirectory;
  }

  /**
   * @param baseRulesDirectory base rules directory to set
   */
  public void setBaseRulesDirectory(String baseRulesDirectory)
  {
    mBaseRulesDirectory = baseRulesDirectory;
  }

  /**
   * @return the base conditions directory
   */
  public String getBaseConditionsDirectory()
  {
    return mBaseConditionsDirectory;
  }

  /**
   * @param baseConditionsDirectory the base conditions directory to set
   */
  public void setBaseConditionsDirectory(String baseConditionsDirectory)
  {
    mBaseConditionsDirectory = baseConditionsDirectory;
  }

  /**
   * @return the base actions directory
   */
  public String getBaseActionsDirectory()
  {
    return mBaseActionsDirectory;
  }

  /**
   * @param baseActionsDirectory the base actions directory to set
   */
  public void setBaseActionsDirectory(String baseActionsDirectory)
  {
    mBaseActionsDirectory = baseActionsDirectory;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();

    buf.append("RulesEngineConfiguration {\n BaseRulesDirectory=");
    buf.append(mBaseRulesDirectory);
    buf.append("\n BaseConditionsDirectory=");
    buf.append(mBaseConditionsDirectory);
    buf.append("\n BaseActionsDirectory=");
    buf.append(mBaseActionsDirectory);
    buf.append("\n}");

    return buf.toString();
  }
}
