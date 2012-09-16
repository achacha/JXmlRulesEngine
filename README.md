Java XML based simple rules engine
==============================

What am I?
---------
A simple file based XML defined rules engine.  You write the generic rules and then run them with different input.

Rules are modular, so you can modify parts of them without changing the whole set.
Everything is in simple file based XML format (XSD included).


Requirements
-----------
Java Developers Kit (JDK) version 1.6 or newer
Ant with Ivy (copy/symlink ivy.jar to ant/lib or similar)


How do I set it up? (in great detail)
---------------------------------
1. ant resolve  (in the directory where build.xml lives), this will download all the needed jars (and some more) from a Maven repository and put them into the lib directory
2. Open eclipse (set the workspace directory to the one this README file lives in, makes it more conventient)
3. File | Import... | Existing Project from JXmlRulesEngine directory where .project file lives
4. For the imported project, right click and Properties | Java | Java Build Path | Libraries and select JRE System Library and change that to your JDK (version 1.6 or newer please)


How do I run unit tests?
---------------------
(`ant resolve`) - just once, to pull in external dependencies using Ivy

(`ant test`) - to run the unit tests



TODO
----
I am working on documentation, samples and tutorials.  The engine is fully functional and passes all unit tests.



v0.9 - All works but not yet well documented.
====
