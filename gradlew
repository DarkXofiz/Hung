#!/bin/sh

# Gradle start up script for POSIX

app_path=$0

# Resolve symlinks
while [ -h "$app_path" ]; do
    ls=$( ls -ld -- "$app_path" )
    link=${ls#*' -> '}
    case $link in
        /*) app_path=$link ;;
        *) app_path=${app_path%"${app_path##*/}"}$link ;;
    esac
done

APP_HOME=$( cd -P "${app_path%"${app_path##*/}"}." > /dev/null && pwd -P ) || exit
APP_BASE_NAME=${0##*/}

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ ! -f "$CLASSPATH" ]; then
    echo "ERROR: $CLASSPATH bulunamadi. gradle-wrapper.jar dosyasini gradle/wrapper/ icine koy." >&2
    exit 1
fi

# Determine the Java command
if [ -n "$JAVA_HOME" ]; then
    if [ -x "$JAVA_HOME/jre/sh/java" ]; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ]; then
        echo "ERROR: JAVA_HOME gecersiz: $JAVA_HOME" >&2
        exit 1
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1; then
        echo "ERROR: JAVA_HOME ayarli degil ve PATH icinde java yok." >&2
        exit 1
    fi
fi

# Raise open file limit when possible
if ! command -v ulimit >/dev/null 2>&1; then :; else
    MAX_FD=$( ulimit -H -n 2>/dev/null ) && ulimit -n "$MAX_FD" 2>/dev/null || true
fi

# Split DEFAULT_JVM_OPTS / JAVA_OPTS / GRADLE_OPTS safely
set -- \
        "-Dorg.gradle.appname=$APP_BASE_NAME" \
        -classpath "$CLASSPATH" \
        org.gradle.wrapper.GradleWrapperMain \
        "$@"

eval "set -- $(
        printf '%s\n' "$DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS" |
        xargs -n1 |
        sed ' s~[^-[:alnum:]+,./:=@_]~\\&~g; ' |
        tr '\n' ' '
    )" '"$@"'

exec "$JAVACMD" "$@"
