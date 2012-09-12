package org.achacha.rules.engine.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.StringTokenizer;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * Rules engine helper
 */
public final class RulesEngineHelper
{
  /**
   * default character encoding
   */
  public static final String DEFAULT_ENCODING = "UTF-8";

  /** No default ctor */
  private RulesEngineHelper()
  {
  }

  /**
   * Gets new <tt>Document</tt> from a <tt>File</tt>.
   * 
   * @param file to read XML from
   * @return a new <tt>org.dom4j.Document</tt>
   */
  public static Document loadXmlDocument(File file)
  {
    FileInputStream fis;
    try
    {
      fis = new FileInputStream(file);
    }
    catch (FileNotFoundException e)
    {
      throw new RuntimeException("Unable to open: " + file, e);
    }
    return loadXmlDocument(fis);
  }

  /**
   * Gets a new <tt>Document</tt> from an <tt>InputStream</tt>.
   * 
   * @param inputStream the stream of xml
   * @return a new <tt>org.dom4j.Document</tt>
   */
  public static Document loadXmlDocument(InputStream inputStream)
  {
    SAXReader xmlReader = getReader();
    InputStreamReader inputReader = null;
    try
    {
      inputReader = new InputStreamReader(inputStream, DEFAULT_ENCODING);
    }
    catch (UnsupportedEncodingException e)
    {
      throw new Error("JVM must support encoding " + DEFAULT_ENCODING, e);
    }

    try
    {
      Document doc = xmlReader.read(inputReader);
      doc.setXMLEncoding(DEFAULT_ENCODING);
      return doc;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Error while reading XML stream", e);
    }
  }

  /**
   * get the Sax read /w correct feature settings and no validation
   * 
   * @return the sax reader
   */
  protected static SAXReader getReader()
  {
    SAXReader reader = null;
    try
    {
      reader = new SAXReader();
      // Sets the character encoding. Should always be UTF-8!
      reader.setEncoding(DEFAULT_ENCODING);
      // Feature: Don't load external DTDs.
      reader.setIncludeExternalDTDDeclarations(false);
      // Whether to ignore commens (skip over them and don't add them to the document tree).
      reader.setIgnoreComments(false);
      // When you get two text nodes in a row, combine them into one. You
      // can only get two text nodes in a row in very weird situations
      // involving comments (if you have ignoreComments=true) or entities
      // that expand to "". This shouldn't really ever apply to us,
      // but in case it does, we want to merge them.
      reader.setMergeAdjacentText(true);
      // Ignores text nodes that consist entirely of whitespace:
      // they are not added to the document tree at all.
      // We never care about blank text nodes anyway.
      reader.setStripWhitespaceText(true);
      // Whether to validate against the DTD or not.
      reader.setValidation(false);
    }
    catch (Exception ex)
    {
      throw new RuntimeException("Failed to get SAX reader", ex);
    }

    return reader;
  }

  /**
   * Pretty print XML format
   * 
   * @param root Element
   * @return Pretty print String
   */
  public static String toPrettyPrintXml(Element root)
  {
    OutputFormat format = OutputFormat.createPrettyPrint();
    format.setEncoding(DEFAULT_ENCODING);

    StringWriter sw = new StringWriter();
    XMLWriter writer = new XMLWriter(sw, format);

    try
    {
      writer.write(root);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Unable to pretty print XML: " + root.asXML(), e);
    }

    return sw.toString();
  }

  /**
   * Gets a reference to a class, wrapping the {@link ClassNotFoundException}.
   * 
   * @param className the name of the class.
   * @return a valid class.
   */
  @SuppressWarnings(value = { "rawtypes" })
  public static Class getClass(String className)
  {
    Class clazz = null;
    try
    {
      clazz = Class.forName(className);
    }
    catch (ClassNotFoundException e)
    {
      throw new RuntimeException("Class not found: " + className, e);
    }
    return clazz;
  }

  /**
   * Instantiates a {@link Class} wrapping the {@link InstantiationException} and {@link IllegalAccessException} with a {@link RuntimeException}.
   * 
   * @param clazz the class to instantiate.
   * @return an instance of the specified class.
   */
  @SuppressWarnings(value = { "rawtypes" })
  public static Object newInstance(Class clazz)
  {
    Object obj = null;
    try
    {
      obj = clazz.newInstance();
    }
    catch (InstantiationException e)
    {
      throw new RuntimeException("Could not instantiate class: " + clazz.getName(), e);
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException("Could not instantiate class: " + clazz.getName(), e);
    }
    return obj;
  }

  /**
   * Copies an element in-place; the target becomes a complete copy of the source. This is used when you already have the target element and can't just create a new element (because you want to
   * preserve the target element's position in the tree).
   * 
   * @param source The element you want to copy
   * @param target The element you want to become the new copy
   */
  @SuppressWarnings(value = { "unchecked" })
  public static void copyElement(Element source, Element target)
  {
    target.attributes().clear();
    target.content().clear();

    for (Attribute attrib : (List<Attribute>) source.attributes())
    {
      target.add((Node) attrib.clone());
    }

    for (Node node : (List<Node>) source.content())
    {
      target.add((Node) node.clone());
    }

    target.setName(source.getName());
  }

  /**
   * Add an element from a given path (ignores leading /, all path is relative) e.g. <xmp> <root><foo/></root> and /foo/bar --> <root><foo/><foo><bar/></foo></root> and <bar/> is returned
   * <root><foo/></root> and foo/bar --> <root><foo/><foo><bar/></foo></root> and <bar/> is returned </xmp> If adding absolute the root element name MUST match the name of the root element provided
   * 
   * @param root Element to create from
   * @param path to create
   * @return Element leaf created
   */
  public static Element addElement(Element root, String path)
  {
    Element base = root;
    StringTokenizer tokens = new StringTokenizer(path, "/");
    while (tokens.hasMoreTokens())
    {
      base = base.addElement(tokens.nextToken());
    }

    return base;
  }

  /**
   * Overwrite an element from a given path (ignores leading /, all path is relative) Will try to reuse any existing elements (using first one found) e.g. <root><foo/></root> and /foo/bar -->
   * <root><foo><bar/></foo></root> and <bar/> is returned <root><foo/></root> and foo/bar --> <root><foo><bar/></foo></root> and <bar/> is returned If adding absolute the root element name MUST match
   * the name of the root element provided
   * 
   * @param root Element to create from
   * @param path to create
   * @return Element leaf created
   */
  public static Element overwriteElement(Element root, String path)
  {
    Element base = root;
    StringTokenizer tokens = new StringTokenizer(path, "/");
    while (tokens.hasMoreTokens())
    {
      String name = tokens.nextToken();
      Element e = base.element(name);
      if (null != e)
      {
        base = e;
      }
      else
      {
        base = base.addElement(name);
      }
    }

    return base;
  }

  /**
   * Compare elements
   * 
   * @param one element
   * @param two element
   * @return true if equal
   */
  public static boolean compareElements(Element one, Element two)
  {
    org.dom4j.util.NodeComparator comparator = new org.dom4j.util.NodeComparator();
    try
    {
      return 0 == comparator.compare(one, two);
    }
    catch (NullPointerException e)
    {
      // NOTE: There is a bug in dom4j-1.6.1 that when you compare 2 nodes and they have same # of attributes
      // however they only have 2 attribute names in common, then you get NPE which is equivalent to the nodes not being equal
      // Offending code in org/dom4j/util/NodeComparator.class line 184
      //
      // e.g. following comparison will yield NPE (one has Optimizable the other has Changed, nodes are different but instead of returning non-zero we
      // get NPE)
      //
      // <Datum ID="targetIdKey" Label="foo" Name="targetIdKey" Type="String" Optimizable="false">productId</Datum>
      // <Datum ID="targetIdKey" Label="foo" Name="targetIdKey" Type="String" Changed="true"/>
      return false;
    }
  }

}
