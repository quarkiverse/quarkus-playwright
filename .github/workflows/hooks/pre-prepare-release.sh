#!/bin/sh

# Ensure browsers are installed
mvn -B -pl runtime exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps"
