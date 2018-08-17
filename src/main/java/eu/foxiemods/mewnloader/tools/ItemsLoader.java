package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.cairn4.moonbase.ItemData;
import com.cairn4.moonbase.ItemDataSet;
import eu.foxiemods.mewnloader.MewnLoader;
import eu.foxiemods.mewnloader.MewnMod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class ItemsLoader {
   public static ArrayList<ItemData> ITEMDATALIST;
   public static String targetFilePath;
   static {
      ITEMDATALIST = new ArrayList<>();
   }

   public static void LoadItems() {
      Path tempDir = Paths.get("run");
      targetFilePath = tempDir.resolve("items.atlas").toAbsolutePath().toString();

      tempDir.toFile().mkdir();

      AtlasTools tools = new AtlasTools();

      for (MewnMod mod : MewnLoader.MewnMods) {
         FileHandle itemsFile = Gdx.files.internal(mod.getModId() + "/items.json");
         if (!itemsFile.exists()) {
            continue;
         }

         ItemDataSet itemDataSet = json.fromJson(ItemDataSet.class, itemsFile.readString());

         for (ItemData itemData : itemDataSet.itemDataList) {
            itemData.modId = mod.getModId();
            tools.addTexture(mod.getModId(), itemData.sprite);

            // register in crafting registry
            if(itemData.craftedIn != null && !itemData.craftedIn.equals("")) {
               CraftingRegistry.getCraftingRegistry(itemData.craftedIn).buildables.add(itemData.id);
            }
         }
         ITEMDATALIST.addAll(itemDataSet.itemDataList);
      }

      tools.dump(tempDir, "items");
      Gdx.app.debug("test", "generated items atlas file");
   }
}
