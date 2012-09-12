#!/bin/bash
ant dist

echo "Changing directory to ./dist/JXmlRulesEngine/"
cd dist/JXmlRulesEngine/
./do.sh -p ../../data/cli/ -r ../../data/cli/request/color_is_red.req.xml -o red.xml
./do.sh -p ../../data/cli/ -r ../../data/cli/request/color_is_not_red.req.xml -o notred.xml

