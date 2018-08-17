package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cairn4.moonbase.worlddata.ItemDropperData;
import com.cairn4.moonbase.worlddata.ItemDropperSet;
import eu.foxiemods.mewnloader.MewnLoader;
import eu.foxiemods.mewnloader.MewnMod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class ResourceTilesLoader {
   public static ArrayList<ItemDropperData> ITEMDROPPERLIST;
   public static String targetFilePath;

   static {
      ITEMDROPPERLIST = new ArrayList<>();
   }

   public static void LoadItems() {
      Path tempDir = Paths.get("run");
      targetFilePath = tempDir.resolve("resources_tiles.atlas").toAbsolutePath().toString();
      tempDir.toFile().mkdir();

      AtlasTools tools = new AtlasTools();

      for (MewnMod mod : MewnLoader.MewnMods) {
         FileHandle tilesFile = Gdx.files.internal(mod.getModId() + "/tiles.json");
         if (!tilesFile.exists()) {
            continue;
         }

         ItemDropperSet itemDropperSet = json.fromJson(ItemDropperSet.class, tilesFile.readString());

         for (ItemDropperData itemData : itemDropperSet.itemDropperList) {
            tools.addTextures(mod.getModId(), itemData.sprites);
         }
         ITEMDROPPERLIST.addAll(itemDropperSet.itemDropperList);
      }

      tools.dump(tempDir, "resources_tiles");
      Gdx.app.debug("test", "generated resource tiles atlas file");
   }
}
