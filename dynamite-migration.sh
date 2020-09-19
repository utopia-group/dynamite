#!/bin/bash

java -Xmx32g -cp dynamite.jar:lib/antlr-4.7.1-complete.jar:lib/bson-3.10.2.jar:lib/gson-2.8.5.jar:lib/z3-4.8.5.jar:lib/mongodb-driver-core-3.10.2.jar:lib/mongodb-driver-sync-3.10.2.jar dynamite.DataMigration $@

