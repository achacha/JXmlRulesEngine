package org.achacha.rules.engine.statement;

/**
 * 'Then' action group
 */
public class StatementsThen extends Statements
{
  /** Serial id */
  private static final long serialVersionUID = 1L;

  /** Element name */
  public static final String ELEMENT = "Then";

  /**
   * ctor
   */
  public StatementsThen()
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
