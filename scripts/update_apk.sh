#!/bin/bash


set -e

if [ "${TRAVIS_PULL_REQUEST_BRANCH}" == "" ]; then
    echo "We only work with pull request";
    exit 0;
fi

git config --global user.name "Travis CI"
git config --global user.email "noreply+travis@robotutor.org"

export DEPLOY_BRANCH=${DEPLOY_BRANCH:-development}
export PUBLISH_BRANCH=${PUBLISH_BRANCH:-master}
DATE_TODAY=$(date +%Y-%m-%d)

echo $TRAVIS_REPO_SLUG, $TRAVIS_PULL_REQUEST;

release_apk_build () {
    echo "Building release apk";
    ./gradlew bundlePlayStoreRelease;
}

debug_apk_build () {
    echo "Building debug apk";
    ./gradlew assembleDebug;
}

#yes | sdkmanager "platforms;android-29"
#yes | sdkmanager "platforms;android-30"
#yes | sdkmanager "build-tools;26.0.1"
#yes | sdkmanager "build-tools;29.0.2"
#yes | sdkmanager "build-tools;30.0.3"
#android update sdk --no-ui --all
#yes | ~/Android/Sdk/tools/bin/sdkmanager --licenses
pwd
if [ "${TRAVIS_BRANCH}" == "${PUBLISH_BRANCH}" ]; then
    release_apk_build
else
    debug_apk_build
fi



git clone --quiet --branch=apk https://robotutor:$GH_TOKEN@github.com/RoboTutorLLC/RTFace_Login.git apk > /dev/null


cd apk

echo `ls`
find ../build -type f -name '*.apk' -exec mv -v {} temp.apk \;



mv temp.apk RTFace_Login-${TRAVIS_PULL_REQUEST_BRANCH}-${DATE_TODAY}.apk


ls
echo `ls -al`
git status
echo $(git status)
# Create a new branch that will contains only latest apk
# git checkout --orphan temporary


# Add generated APK
git add .
git commit -am " ${TRAVIS_BRANCH} : ($(git rev-parse --short HEAD)) : ($(date +%Y-%m-%d.%H:%M:%S))"

# Delete current apk branch
# git branch -D apk
# Rename current branch to apk
# git branch -m apk

# Force push to origin since histories are unrelated


git push origin apk > /dev/null

