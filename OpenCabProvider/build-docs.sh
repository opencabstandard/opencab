#!/bin/bash

set -ex

./gradlew javadoc
cp -R ../website/images app/build/docs/javadoc