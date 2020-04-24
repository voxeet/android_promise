rm -rf promise/build

./gradlew :promise:assembleRelease :promise:bintrayUpload
