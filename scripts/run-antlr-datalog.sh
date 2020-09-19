#!/bin/bash -e

parent_path=$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )
working_path="$parent_path/../src/dynamite/datalog/parser"
antlr4="java -jar $parent_path/../lib/antlr-4.7.1-complete.jar"

# run antlr4
$antlr4 $parent_path/Datalog.g4 -no-listener -visitor -package dynamite.datalog.parser -o $working_path

# enter working directory
cd "$working_path"

# delete redundant files
rm "Datalog.interp"
rm "Datalog.tokens"
rm "DatalogLexer.interp"
rm "DatalogLexer.tokens"

