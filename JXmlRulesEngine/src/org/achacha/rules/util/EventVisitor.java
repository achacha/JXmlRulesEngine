package org.achacha.rules.util;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import org.achacha.rules.base.XmlEmittable;
import org.achacha.rules.base.XmlParseable;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Event visitor
 */
public class EventVisitor implements XmlParseable
{
  /** Element */
  public static final String ELEMENT = "EventVisitor";

  /** Error level */
  public static final int DISABLED = 0;

  /** Error level */
  public static final int ERROR = 1;

  /** Event level */
  public static final int EVENT = 2;

  /** Warning level */
  public static final int WARNING = 3;

  /** Warning level */
  public static final int INFO = 4;

  /** Debug level */
  public static final int DEBUG = 5;

  /** Default level */
  private int mLoggingLevel = DISABLED;

  /** List of events */
  private List<Event> mEvents = new LinkedList<Event>();

  /**
   * If event visitor is enabled Logging level of >DISABLED is enables the visitor
   * 
   * @return true if enabled
   */
  public boolean isEnabled()
  {
    return mLoggingLevel > DISABLED;
  }

  /**
   * Check if level is being logged
   * 
   * @return true if logging
   */
  public boolean isDebugEnabled()
  {
    return mLoggingLevel >= DEBUG;
  }

  /**
   * Check if level is being logged
   * 
   * @return true if logging
   */
  public boolean isInfoEnabled()
  {
    return mLoggingLevel >= INFO;
  }

  /**
   * Check if level is being logged
   * 
   * @return true if logging
   */
  public boolean isEventEnabled()
  {
    return mLoggingLevel >= EVENT;
  }

  /**
   * Check if level is being logged
   * 
   * @return true if logging
   */
  public boolean isWarnEnabled()
  {
    return mLoggingLevel >= WARNING;
  }

  /**
   * Default event visitor
   */
  public EventVisitor()
  {
  }

  /**
   * Create event visitor with logging level
   * 
   * @param loggingLevel maximum to log, messages above this level are ignored
   */
  public EventVisitor(int loggingLevel)
  {
    mLoggingLevel = loggingLevel;
  }

  /**
   * Set new logging level
   * 
   * @param loggingLevel int
   */
  public void setLoggingLevel(int loggingLevel)
  {
    mLoggingLevel = loggingLevel;
  }

  /**
   * Add event
   * 
   * @param event to add
   * @param level of the event
   */
  public void add(String event, int level)
  {
    if (mLoggingLevel >= level)
    {
      mEvents.add(new Event(event));
    }
  }

  /**
   * Add debug event
   * 
   * @param event to add
   */
  public void debug(String event)
  {
    if (mLoggingLevel >= DEBUG)
    {
      mEvents.add(new Event(event, DEBUG));
    }
  }

  /**
   * Add info
   * 
   * @param event to add
   */
  public void info(String event)
  {
    if (mLoggingLevel >= INFO)
    {
      mEvents.add(new Event(event, INFO));
    }
  }

  /**
   * Add warning
   * 
   * @param event to add
   */
  public void warn(String event)
  {
    if (mLoggingLevel >= WARNING)
    {
      mEvents.add(new Event(event, WARNING));
    }
  }

  /**
   * Add event
   * 
   * @param event to add
   */
  public void event(String event)
  {
    if (mLoggingLevel >= EVENT)
    {
      mEvents.add(new Event(event, EVENT));
    }
  }

  /**
   * Add error
   * 
   * @param event to add
   */
  public void error(String event)
  {
    if (mLoggingLevel >= ERROR)
    {
      mEvents.add(new Event(event, ERROR));
    }
  }

  /**
   * Add error
   * 
   * @param e Throwable to add
   */
  public void error(Throwable e)
  {
    if (mLoggingLevel >= ERROR)
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter writer = new PrintWriter(baos);
      e.printStackTrace(writer);
      writer.flush();
      mEvents.add(new Event(e.getMessage() + "\r\n" + baos.toString(), ERROR));
    }
  }

  /**
   * Gets # of events logged
   * 
   * @return int
   */
  public int size()
  {
    return mEvents.size();
  }

  /**
   * List of events
   * 
   * @return List
   */
  public List<Event> getEvents()
  {
    return mEvents;
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement()
  {
    return toElement(ELEMENT);
  }

  /**
   * {@inheritDoc}
   */
  public Element toElement(String elementName)
  {
    Element root = DocumentHelper.createElement(elementName);
    for (Event event : mEvents)
    {
      root.add(event.toElement());
    }
    return root;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings(value = { "unchecked" })
  public void parse(Element root)
  {
    mEvents.clear();
    List<Element> events = root.elements(Event.ELEMENT_EVENT);
    for (Element e : events)
    {
      Event event = new Event();
      event.parse(e);
      mEvents.add(event);
    }
  }

  /**
   * Event object
   */
  class Event implements XmlEmittable
  {
    /** Event element name */
    public static final String ELEMENT_EVENT = "Event";

    /** Level attribute name */
    public static final String ATTR_LEVEL = "Level";

    /** CDATA end that can break being embedded in CDATA */
    public static final String CDATA_END = "]]>";

    /** Replace end of CDATA with this */
    public static final String CDATA_END_SAFE = "]]&gt;";

    /** Event text */
    private String mText;

    /** Event level */
    private int mLevel = EventVisitor.EVENT;

    /** No default ctor */
    protected Event()
    {
    }

    /**
     * Construct event
     * 
     * @param text of event
     */
    public Event(String text)
    {
      mText = text;
    }

    /**
     * Construct event
     * 
     * @param text of event
     * @param level of the event
     */
    public Event(String text, int level)
    {
      mText = text;
      mLevel = level;
    }

    /**
     * Get text
     * 
     * @return String
     */
    public String getText()
    {
      return mText;
    }

    /**
     * Get event level
     * 
     * @return level
     */
    public int getLevel()
    {
      return mLevel;
    }

    /**
     * {@inheritDoc}
     */
    public Element toElement()
    {
      return toElement(ELEMENT_EVENT);
    }

    /**
     * {@inheritDoc}
     */
    public Element toElement(String elementName)
    {
      Element root = DocumentHelper.createElement(elementName);

      root.addAttribute(ATTR_LEVEL, Integer.toString(mLevel));

      if (mText.indexOf(CDATA_END) > 0)
      {
        try
        {
          // String output = URLEncoder.encode(mText, "utf-8");
          // String output = mText.replace('<', '{');
          // output = output.replace('>', '}');
          String output = mText.replace(CDATA_END, CDATA_END_SAFE);
          root.addCDATA(output);
        }
        catch (Exception ex)
        {
          throw new RuntimeException(ex);
        }
      }
      else
      {
        root.addCDATA(mText);
      }
      return root;
    }

    /**
     * {@inheritDoc}
     */
    public void parse(Element root)
    {
      mText = StringUtils.defaultString(root.getText());
      mLevel = Integer.parseInt(root.attributeValue(ATTR_LEVEL));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();

      builder.append("[");
      builder.append(mLevel);
      builder.append("]: ");
      builder.append(mText);

      return builder.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();

    builder.append("Event count: ");
    builder.append(mEvents.size());
    builder.append("  level: ");
    builder.append(mLoggingLevel);
    for (Event event : mEvents)
    {
      builder.append("\r\n");
      builder.append(event);
    }

    return builder.toString();
  }
}
