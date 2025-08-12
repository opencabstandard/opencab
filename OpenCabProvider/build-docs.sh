#!/bin/bash

set -ex

# Unless this is a production website build, add a draft banner

if [ "$VERCEL_ENV" != "production" ]; then
    cat << EOF >> ../website/main.css
header::before {
    text-align: center;
    content: "Draft Proposal — For Review Only";
    background-color: hsl(45, 100%, 55%);
    display: block;
    font-size: 150%;
    padding: 5px 0;
}
EOF
fi

./gradlew javadoc 2>&1
cp -R ../website/images app/build/docs/javadoc
