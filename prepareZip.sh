#!/bin/bash

cp ./HelloWorldFunction/target/Bootstrap .
zip  bootstrap.zip ./bootstrap
rm ./bootstrap
mv bootstrap.zip ./HelloWorldFunction/target/