
package org.achacha.rules.util;

import java.util.LinkedList;
import java.util.List;

import org.achacha.rules.base.XmlEmittable;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Times events <verbose> EventTimer events = new EventTimer(); events.set("Starting test"); //do some stuff events.set("Validating"); //validate stuff events.reset(); </verbose>
 * System.out.println(events);
 */
public final class EventTimer implements XmlEmittable
{
  /** Element name */
  public static final String ELEMENT = "EventTimer";

  /** Container for events */
  private List<Event> mEvents = new LinkedList<Event>();

  /** current event */
  private Event mCurrent;

  /** name for the events */
  private String mName;

  /** Total time */
  private long mStartTime = -1;

  /** ctor */
  public EventTimer()
  {
    this(ELEMENT);
  }

  /**
   * ctor
   * 
   * @param name of the initial event which is the overall name
   */
  public EventTimer(String name)
  {
    mName = name;
    mStartTime = System.currentTimeMillis();
  }

  /**
   * Set current event Save previous with its duration
   * 
   * @param eventName to set as current
   */
  public void set(String eventName)
  {
    if (null != mCurrent)
    {
      mCurrent.stop(System.currentTimeMillis());
      mEvents.add(mCurrent);
    }
    mCurrent = new Event(eventName, System.currentTimeMillis());
  }

  /**
   * Save current event then clear current event
   */
  public void reset()
  {
    if (null != mCurrent)
    {
      mCurrent.stop(System.currentTimeMillis());
      mEvents.add(mCurrent);
    }
    mCurrent = null;
  }

  /**
   * Get total time interval
   * 
   * @return total interval of the events
   */
  public long getTotalTime()
  {
    return System.currentTimeMillis() - mStartTime;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    StringBuilder buf = new StringBuilder();
    buf.append("EVENTS[");
    buf.append(String.valueOf(getTotalTime()));
    buf.append("ms](\"");
    buf.append(mName);
    buf.append("\")\n{\n");
    for (Event event : mEvents)
    {
      buf.append("  ");
      buf.append(event);
      buf.append("\n");
    }
    if (null != mCurrent)
    {
      buf.append("  ");
      buf.append(mCurrent.toString());
      buf.append("\n");
    }
    buf.append("}\n");
    return buf.toString();
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
    Element eventTimer = DocumentHelper.createElement(elementName);

    eventTimer.addAttribute("Name", mName);
    eventTimer.addElement("TotalTime").addText(String.valueOf(getTotalTime()));

    for (Event event : mEvents)
    {
      eventTimer.add(event.toElement());
    }

    if (null != mCurrent) // when timer is stopped there is no current event
    {
      eventTimer.add(mCurrent.toElement());
    }

    return eventTimer;
  }

  /**
   * Event
   */
  private class Event implements XmlEmittable
  {
    /** Element name */
    public static final String ELEMENT_EVENT = "Event";

    /** event name */
    private String mEventName;

    /** start time of event */
    private long mEventStartTime;

    /** stop time of event */
    private long mStopTime;

    /**
     * create new Event
     * 
     * @param name of event
     * @param startTime of event
     */
    public Event(String name, long startTime)
    {
      mEventName = name;
      mEventStartTime = startTime;
    }

    /** no default ctor */
    @SuppressWarnings("unused")
    protected Event()
    {
    }

    /**
     * Set stop time
     * 
     * @param stopTime of event
     */
    void stop(long stopTime)
    {
      mStopTime = stopTime;
    }

    /**
     * Check if event is still active
     * 
     * @return true is stop time is 0
     */
    boolean isActive()
    {
      return (0 == mStopTime);
    }

    /**
     * Get interval for the event
     * 
     * @return milliseconds, if <0 then event is still active
     */
    long getInterval()
    {
      if (0 == mStopTime)
      {
        return System.currentTimeMillis() - mEventStartTime;
      }
      return mStopTime - mEventStartTime;
    }

    /**
     * Event name
     * 
     * @return string
     */
    @SuppressWarnings("unused")
    public String getName()
    {
      return mEventName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
      StringBuffer buf = new StringBuffer();
      buf.append('[');
      buf.append(mEventName);
      buf.append("]=");
      buf.append(Long.toString(getInterval()));

      if (isActive())
      {
        buf.append(" (active)");
      }
      return buf.toString();
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
      Element event = DocumentHelper.createElement(elementName);
      event.addText(mEventName);
      event.addAttribute("interval", Long.toString(getInterval()));
      if (isActive())
      {
        event.addAttribute("active", "true");
      }
      return event;
    }
  }

}
