package org.achacha.rules.engine.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.achacha.rules.engine.RulesEngineLoader;
import org.achacha.rules.engine.factory.RulesEngineFactory;

/**
 * Rules Engine loader that uses the file system base directory
 */
public class RulesEngineLoaderImplFileSystem extends RulesEngineLoader
{
  /** Default conditions subdir */
  public static final String DEFAULT_CONDITIONS_DIR = "conditions";
  /** Default actions subdir */
  public static final String DEFAULT_ACTIONS_DIR = "actions";
  /** Default rules subdir */
  public static final String DEFAULT_RULES_DIR = "rules";
  
  /** Base rule directory */
  protected File mBaseRuleDir;

  /** Base conditions directory */
  protected File mBaseConditionsDir;

  /** Base actions directory */
  protected File mBaseActionsDir;

  /**
   * Ctor with String base dir implying default subdirs
   * 
   * @param factory RulesEngineFactory
   * @param baseDir String
   */
  public RulesEngineLoaderImplFileSystem(RulesEngineFactory factory, String baseDir)
  {
    super(factory);
    mBaseRuleDir = new File(baseDir, DEFAULT_RULES_DIR);
    mBaseConditionsDir = new File(baseDir, DEFAULT_CONDITIONS_DIR);
    mBaseActionsDir = new File(baseDir, DEFAULT_ACTIONS_DIR);
  }

  /**
   * Ctor with String base dirs for each conditions, actions and rules
   * 
   * @param factory RulesEngineFactory
   * @param baseRuleDir String
   * @param baseConditionsDir String
   * @param baseActionsDir String
   */
  public RulesEngineLoaderImplFileSystem(RulesEngineFactory factory, String baseRuleDir, String baseConditionsDir, String baseActionsDir)
  {
    super(factory);
    mBaseRuleDir = new File(baseRuleDir);
    mBaseConditionsDir = new File(baseConditionsDir);
    mBaseActionsDir = new File(baseActionsDir);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getActionsStream(String name)
  {
    File file = new File(mBaseActionsDir, name);
    FileInputStream fis;
    try
    {
      fis = new FileInputStream(file);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException("File does not exist or cannot be read: " + file.getAbsolutePath(), e);
    }
    return fis;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getConditionsStream(String name)
  {
    File file = new File(mBaseConditionsDir, name);
    FileInputStream fis;
    try
    {
      fis = new FileInputStream(file);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException("File does not exist or cannot be read: " + file.getAbsolutePath(), e);
    }
    return fis;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InputStream getRuleStream(String name)
  {
    File file = new File(mBaseRuleDir, name);
    FileInputStream fis;
    try
    {
      fis = new FileInputStream(file);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException("File does not exist or cannot be read: " + file.getAbsolutePath(), e);
    }
    return fis;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();

    builder.append("RulesEngineLoaderImplFileSystem {\n  BaseRuleDir=");
    builder.append(mBaseRuleDir);
    builder.append("\n  BaseConditionsDir=");
    builder.append(mBaseConditionsDir);
    builder.append("\n  BaseActionsDir=");
    builder.append(mBaseActionsDir);
    builder.append("\n}");

    return builder.toString();
  }
}
