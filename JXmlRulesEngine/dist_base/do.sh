#!/bin/bash

# Run it
DIR=`dirname $0`
java -classpath $DIR/classes:$DIR/lib/commons-lang-2.6.jar:$DIR/lib/commons-beanutils-1.8.3.jar:$DIR/lib/commons-cli-1.2.jar:$DIR/lib/commons-io-2.4.jar:$DIR/lib/dom4j-1.6.1.jar:$DIR/lib/json-20090211.jar:$DIR/lib/jaxen-1.1-beta-6.jar:$DIR/lib/log4j-1.2.16.jar:$DIR/lib/JXmlRulesEngine.jar org.achacha.rules.RulesMainCLI $*
