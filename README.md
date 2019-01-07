This code allows to have a modded version of [MewnBase](https://cairn4.itch.io/mewnbase/devlog/59895/v0461-hotfix).

### Installation procedure :
```bash
git clone https://github.com/CallMeFoxie/MewnbaseModded.git --recursive
export GAME_VERSION=0.46.1
cd MewnbaseModded
mkdir -p game
```

Find the mewnbase jar  
here for example :  
~/.config/itch/apps/mewnbase/desktop-1.0.jar  
And copy.
```bash
cp ~/.config/itch/apps/mewnbase/desktop-1.0.jar game/mewnbase-${GAME_VERSION}.jar
```

### Preparation of sources :
```bash
./tools.sh workspace
```

### First compilation :
```bash
./gradlew build
```
The result is available in the folder:  
WORKSPACE_DIR/MewnbaseModded/build/libs/MewnBaseModded-1.0-SNAPSHOT.jar
