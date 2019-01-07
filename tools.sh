#!/bin/bash

task=$1

ISBOW=
if [ x"$(cat /proc/sys/kernel/osrelease | grep -i Microsoft)" != "x" ]; then
  ISBOW="BOW"
elif [ x"$(uname | grep MINGW)" != "x" ]; then
  ISBOW="MINGW"
elif [ x"$(uname | grep -i Cygwin)" != "x" ]; then
  ISBOW="Cygwin"
fi

[ x"$ISBOW" != "x" ] && echo "Running on platform: $ISBOW"

[ -z "${GAME_VERSION}" ] && echo "Missing ENV GAME_VERSION" && exit 1

build_fernflower() {
  cd tools/fernflower
  
  ./gradlew build
  
  [ $? -ne 0 ] && echo "Failed to build fernflower!" && exit 1
  [ ! -f build/libs/fernflower.jar ] && echo "Missing output jar!" && exit 1
}

unpack_jar() {
  cd game
  
  [ ! -f mewnbase-${GAME_VERSION}.jar ] && echo "Missing source JAR!" && exit 1
  [ -d unpacked-${GAME_VERSION} ] && rm -rf unpacked-${GAME_VERSION}
  
  unzip -o mewnbase-${GAME_VERSION}.jar -d unpacked-${GAME_VERSION}
  
  [ $? -ne 0 ] && echo "Failed to unpack source JAR!" && exit 1
}

decompile_jar() {
  [ ! -f tools/fernflower/build/libs/fernflower.jar ] && echo "Have to run build_fernflower first!" && exit 1

  [ ! -d game/unpacked-${GAME_VERSION} ] && echo "Missing unpacked source JAR! Run unpack_jar first!" && exit 1
  [ -d game/decompiled-${GAME_VERSION} ] && rm -rf game/decompiled-${GAME_VERSION}

  mkdir game/decompiled-${GAME_VERSION}/

  java -jar tools/fernflower/build/libs/fernflower.jar game/unpacked-${GAME_VERSION}/com/cairn4/ game/decompiled-${GAME_VERSION}/

  [ $? -ne 0 ] && echo "Failed to decompile source files!" && exit 1

  # convert DOS2UNIX on BashOnWindows
  if [ x"$ISBOW" != "x" ]; then 
    find game/decompiled-${GAME_VERSION} -type f | grep ".java$" | xargs dos2unix
  fi
}

copy_to_src() {
  [ ! -d game/decompiled-${GAME_VERSION} ] && echo "Missing patched folder! Run patch_soruce first!" && exit 1
  [ -d src/game/java/com/cairn4 ] && echo "Target folder already exists, NOT running!" && exit 1
  [ ! -f patches/patches-${GAME_VERSION}.patch ] && echo "Missing API patch file!" && exit 1

  mkdir -p src/game/java/com/cairn4
  cp -rp game/decompiled-${GAME_VERSION}/* src/game/java/com/cairn4
  cd src/game/java/com/cairn4
  patch -p5 -l --ignore-whitespace --binary --no-backup-if-mismatch < ../../../../../patches/patches-${GAME_VERSION}.patch
}

diff_base_game() {
  [ ! -d src/game/java/com/cairn4 ] && echo "Target folder does not exist! Nothing to diff!" && exit 1
  [ ! -d game/decompiled-${GAME_VERSION} ] && echo "Missing decompiled folder! Run patch_source first!" && exit 1

  diff -rupb -B game/decompiled-${GAME_VERSION} src/game/java/com/cairn4 > patches/patches-${GAME_VERSION}.patch

  [ $? -ne 1 ] && echo "Error creating a diff!" && exit 1
}

copy_resources() {
  [ ! -f patches/resourceslist-${GAME_VERSION}.txt ] && echo "Missing resources list for version ${GAME_VERSION}!" && exit 1
  [ ! -d game/unpacked-${GAME_VERSION} ] && echo "Missing unpacked folder! Run unpack_jar first!" && exit 1

  [ -d src/game/resources ] && rm -rf src/game/resources
  mkdir -p src/game/resources

  cat patches/resourceslist-${GAME_VERSION}.txt | while read -r item; do
    echo "Copying $item"
    if [ x"$item" != "x" ]; then
      cp -rvp game/unpacked-${GAME_VERSION}/$item src/game/resources/
    fi
  done
}

pushd -n $(pwd) >/dev/null

echo "Running task: " $task

case "$task" in
  "build_fernflower")
    build_fernflower
    ;;
  "unpack_jar")
    unpack_jar
    ;;
  "decompile_jar")
    decompile_jar
    ;;
  "copy_to_src")
    copy_to_src
    ;;
  "copy_resources")
    copy_resources
    ;;
  "workspace")
    $0 build_fernflower
    $0 unpack_jar
    $0 decompile_jar
    $0 copy_to_src
    $0 copy_resources
    ./gradlew idea
    ;;
  "diff_base_game")
    diff_base_game
    ;;
  *)
    echo "Unknown task!"
    exit 1
    ;;
esac

popd >/dev/null
