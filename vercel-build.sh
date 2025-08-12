#!/bin/bash
set -ex

# Test with: docker run -v $PWD:/src --workdir /src --rm -it amazonlinux:2023.8.20250715.0 /bin/bash
# cd OpenCabProvider/app/build/docs/javadoc/; python -m http.server 8085
yum --assumeyes install wget unzip java-17-amazon-corretto-devel

wget --quiet -O android.zip https://dl.google.com/android/repository/commandlinetools-linux-8092744_latest.zip
unzip android.zip
mkdir -p android-sdk/licenses/
# We accept the license
printf "\n24333f8a63b6825ea9c5514f83c2829b004d1fee" > android-sdk/licenses/android-sdk-license

export JAVA_HOME=/etc/alternatives/java_sdk/
cmdline-tools/bin/sdkmanager --sdk_root=android-sdk/ "platform-tools" "platforms;android-28"

export LANG=en_US.UTF-8
cd OpenCabProvider
export LC_ALL=C.UTF-8
export LANG=C.UTF-8
export LANGUAGE=C.UTF-8
ANDROID_SDK_ROOT=../android-sdk/ ./build-docs.sh
