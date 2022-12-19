#!/bin/bash

rm -rf build */build

errors=()

./gradlew ktlint detekt || { errors+=("validation failed"); }

./gradlew jacocoProjectReport || { errors+=("tests failed"); }

folders=`find . -path "*/build/test-results/testDebugUnitTest" -type d`

for folder in $folders
do
  grep -r "<failure" $folder && { errors+=("UT failed for $folder"); }
done

if [ ${#errors[@]} -ne 0 ]; then
  echo "FAILING due: ${errors[*]}"
  exit 1
fi

echo "SUCCESS"
