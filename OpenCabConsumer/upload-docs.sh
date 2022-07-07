#!/bin/bash

set -ev

aws s3 sync --exclude ".git/*" --acl=public-read --cache-control "max-age=10" app/build/docs/javadoc/ s3://production-opencab-docs-website-bucket-d1app5i4tqtg/
