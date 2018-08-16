#!/bin/bash

task=$1

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
}

patch_source() {
  [ ! -d game/decompiled-${GAME_VERSION} ] && echo "Missing decompiled folder! Run decompile_jar first!" && exit 1
  [ ! -f patches/patches-${GAME_VERSION}-compile.patch ] && echo "Missing patch file for ${GAME_VERSION} version!" && exit 1
  
  [ -d game/patched-${GAME_VERSION} ] && rm -rf game/patched-${GAME_VERSION}
  mkdir game/patched-${GAME_VERSION}
  cp -rp game/decompiled-${GAME_VERSION}/* game/patched-${GAME_VERSION}/
  cd game/patched-${GAME_VERSION}/
  patch -p5 -l --binary --no-backup-if-mismatch < ../../patches/patches-${GAME_VERSION}-compile.patch

  [ $? -ne 0 ] && echo "Failed to patch source files!" && exit 1
}

copy_to_src() {
  [ ! -d game/patched-${GAME_VERSION} ] && echo "Missing patched folder! Run patch_soruce first!" && exit 1
  [ -d src/game/java/com/cairn4 ] && echo "Target folder already exists, NOT running!" && exit 1
  [ ! -f patches/patches-${GAME_VERSION}-api.patch ] && echo "Missing API patch file!" && exit 1

  mkdir -p src/game/java/com/cairn4
  cp -rp game/patched-${GAME_VERSION}/* src/game/java/com/cairn4
  cd src/game/java/com/cairn4
  patch -p5 -l --binary --no-backup-if-mismatch < ../../../../../patches/patches-${GAME_VERSION}-api.patch
}

diff_base_game() {
  [ ! -d src/game/java/com/cairn4 ] && echo "Target folder does not exist! Nothing to diff!" && exit 1
  [ ! -d game/patched-${GAME_VERSION} ] && echo "Missing patched folder! Run patch_source first!" && exit 1

  diff -rupb game/patched-${GAME_VERSION} src/game/java/com/cairn4 > patches/patches-${GAME_VERSION}-api.patch

  [ $? -ne 1 ] && echo "Error creating a diff!" && exit 1
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
  "patch_source")
    patch_source
    ;;
  "copy_to_src")
    copy_to_src
    ;;
  "workspace")
    $0 build_fernflower
    $0 unpack_jar
    $0 decompile_jar
    $0 patch_source
    $0 copy_to_src
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
