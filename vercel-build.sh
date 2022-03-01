#!/bin/bash

# apt-get install default-jdk-headless wget unzip

set -e

wget -O android.zip https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip
unzip android.zip
mkdir -p android-sdk/licenses/
# We accept the license
printf "\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > android-sdk/licenses/android-sdk-license
cmdline-tools/bin/sdkmanager --sdk_root=android-sdk/ "platform-tools" "platforms;android-28"

cd OpenCabProvider
ANDROID_SDK_ROOT=../android-sdk/ ./build-docs.sh
