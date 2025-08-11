#!/bin/bash
set -ex

# Test with: docker run --rm -it amazonlinux:2.0.20191217.0 sh
<<<<<<< HEAD
yum install wget unzip
amazon-linux-extras install java-openjdk11
yum install java-11-openjdk-devel
=======
yum install wget unzip java-17-amazon-corretto-devel
>>>>>>> 1b709f3 (Fixes for Java 17)

wget --quiet -O android.zip https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip
unzip android.zip
mkdir -p android-sdk/licenses/
# We accept the license
printf "\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > android-sdk/licenses/android-sdk-license

export JAVA_HOME=/etc/alternatives/java_sdk/
cmdline-tools/bin/sdkmanager --sdk_root=android-sdk/ "platform-tools" "platforms;android-28"

export LANG=en_US.UTF-8
cd OpenCabProvider
ANDROID_SDK_ROOT=../android-sdk/ ./build-docs.sh
