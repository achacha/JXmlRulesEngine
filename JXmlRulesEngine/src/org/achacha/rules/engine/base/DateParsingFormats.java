package org.achacha.rules.engine.base;

/**
 * Date parsing formats
 */
public final class DateParsingFormats
{
  /** "EEE, dd MMM yyyy HH:mm:ss Z" */
  public static final String FORMAT_ISO_SMTP = "EEE, dd MMM yyyy HH:mm:ss Z";

  /** "yyyy-MM-dd" */
  public static final String FORMAT_YEAR_MONTH_DAY = "yyyy-MM-dd";

  /** K:mm */
  public static final String FORMAT_HOUR_MINUTE = "k:mm";

  /** yyyy-MM-dd K:mm */
  public static final String FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE = "yyyy-MM-dd k:mm";

  /** Date/time formats using with parsers */
  public static final String[] SUPPORTED_FORMATS = { FORMAT_ISO_SMTP, FORMAT_YEAR_MONTH_DAY, FORMAT_HOUR_MINUTE, FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE };

  /** ctor */
  protected DateParsingFormats()
  {
  }
}
