#!/bin/sh
# Maven Wrapper Script - Downloads and runs Maven

set -e

MAVEN_PROJECTBASEDIR="${MAVEN_PROJECTBASEDIR:-$(cd "$(dirname "$0")" && pwd)}"
WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

# Read distribution URL from properties
if [ -f "$WRAPPER_PROPERTIES" ]; then
    MAVEN_DIST_URL=$(grep 'distributionUrl' "$WRAPPER_PROPERTIES" | cut -d'=' -f2)
fi

MAVEN_HOME="$HOME/.m2/wrapper/dists/apache-maven-3.9.6"
MAVEN_CMD="$MAVEN_HOME/bin/mvn"

# Download Maven if not present
if [ ! -f "$MAVEN_CMD" ]; then
    echo "Downloading Maven 3.9.6..."
    mkdir -p "$HOME/.m2/wrapper/dists"
    TEMP_ZIP="$HOME/.m2/wrapper/dists/maven.zip"
    curl -sL "https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip" -o "$TEMP_ZIP"
    unzip -q "$TEMP_ZIP" -d "$HOME/.m2/wrapper/dists"
    rm "$TEMP_ZIP"
    echo "Maven installed successfully!"
fi

# Run Maven
exec "$MAVEN_CMD" "$@"
