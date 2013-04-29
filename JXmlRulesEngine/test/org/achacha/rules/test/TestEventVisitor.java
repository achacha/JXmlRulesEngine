
package org.achacha.rules.test;

import org.achacha.rules.testbase.RulesEngineUnitTestBase;
import org.achacha.rules.util.EventVisitor;
import org.dom4j.Element;


/**
 * JUnit test for EventVisitor
 */
public class TestEventVisitor extends RulesEngineUnitTestBase
{
  /** Test */
  public void testLoggingErrorLevels()
  {
    EventVisitor ev = new EventVisitor(EventVisitor.ERROR);
    
    if (ev.isDebugEnabled())
    {
      ev.debug("Debugging event added 1");
    }
    if (ev.isWarnEnabled())
    {
      ev.warn("Warning event added 1");
    }
    if (ev.isEventEnabled())
    {
      ev.event("Event added 1");
    }
    ev.error("Error event added 1");
    
    //System.out.println(ev.toElement().asXML());
    if (ev.getEvents().size() != 1)
    {
      throw new RuntimeException("Logging level error, only 1 ERROR should have been logged.");
    }

    System.out.println("PASSED EventVisitor error logging");
  }

  /** Test */
  public void testLoggingWarningLevels()
  {
    EventVisitor ev = new EventVisitor(EventVisitor.WARNING);
    
    if (ev.isDebugEnabled())
    {
      ev.debug("Debugging event added 1");
    }
    if (ev.isWarnEnabled())
    {
      ev.warn("Warning event added 1");
    }
    if (ev.isEventEnabled())
    {
      ev.event("Event added 1");
    }
    ev.error("Error event added 1");
    
    if (ev.getEvents().size() != 3)
    {
      throw new RuntimeException("Logging level error, only 3 ERROR should have been logged.");
    }

    System.out.println("PASSED EventVisitor warning logging");
  }
  
  /** Test */
  public void testXmlSerialization()
  {
    EventVisitor evBefore = new EventVisitor();
    evBefore.event("One");
    evBefore.event("Two");
    evBefore.event("Three");
    evBefore.event("Four");

    Element elemBefore = evBefore.toElement();
    String xmlBefore = elemBefore.asXML();
    
    EventVisitor evAfter = new EventVisitor();
    evAfter.parse(elemBefore);
    Element elemAfter = evAfter.toElement();
    String xmlAfter = elemAfter.asXML();
    
    if (!xmlBefore.equals(xmlAfter))
    {
      throw new RuntimeException("XmlSerialization failed.  BEFORE: "+xmlBefore+"\r\nAFTER: "+xmlAfter);
    }

    System.out.println("PASSED EventVisitor XML serialization");
  }
}
