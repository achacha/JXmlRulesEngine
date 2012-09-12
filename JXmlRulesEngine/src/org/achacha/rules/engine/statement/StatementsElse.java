package org.achacha.rules.engine.statement;

/**
 * 'Else' action group
 */
public class StatementsElse extends Statements
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element name */
  public static final String ELEMENT = "Else";

  /**
   * ctor
   */
  public StatementsElse()
  {
    super();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getType()
  {
    return TYPE;
  }
}
