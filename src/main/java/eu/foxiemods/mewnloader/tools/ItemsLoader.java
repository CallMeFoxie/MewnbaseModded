package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.cairn4.moonbase.ItemData;
import com.cairn4.moonbase.ItemDataSet;
import eu.foxiemods.mewnloader.CraftingRegistry;
import eu.foxiemods.mewnloader.MewnLoader;
import eu.foxiemods.mewnloader.MewnMod;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class ItemsLoader {
   public static HashMap<String, TextureAtlas> skinAtlas;
   public static ArrayList<ItemData> ITEMDATALIST;
   static {
      skinAtlas = new HashMap<>();
      ITEMDATALIST = new ArrayList<>();
   }

   public static void LoadItems() {
      Path tempDir = Paths.get("run");
      if(tempDir.toFile().exists()) {
         try {
            FileUtils.deleteDirectory(tempDir.toFile());
         } catch (IOException e) {
            e.printStackTrace();
         }
      }

      tempDir.toFile().mkdir();

      for (MewnMod mod : MewnLoader.MewnMods) {
         FileHandle itemsFile = Gdx.files.internal(mod.getModId() + "/items.json");
         if (!itemsFile.exists()) {
            continue;
         }

         ItemDataSet itemDataSet = json.fromJson(ItemDataSet.class, itemsFile.readString());

         // generate new atlas
         TexturePacker packer = new TexturePacker(new TexturePacker.Settings());

         for (ItemData itemData : itemDataSet.itemDataList) {
            itemData.modId = mod.getModId();
            FileHandle spriteFile = Gdx.files.internal(mod.getModId() + "/sprites/" + itemData.sprite + ".png");

            if(!spriteFile.exists()) {
               Gdx.app.error("test", "MISSING SPRITE!");
            }
            try {
               packer.addImage(ImageIO.read(spriteFile.read()), itemData.sprite);
            } catch (IOException e) {
               e.printStackTrace();
            }

            // register in crafting registry
            if(itemData.craftedIn != null && !itemData.craftedIn.equals("")) {
               CraftingRegistry.getCraftingRegistry(itemData.craftedIn).buildables.add(itemData.id);
            }
         }
         Gdx.app.debug(mod.getModId(), "Adding items from mod");
         ITEMDATALIST.addAll(itemDataSet.itemDataList);
         packer.pack(tempDir.toFile(), mod.getModId() + ".atlas");
         Gdx.app.debug("test", "generated atlas file on path: " + tempDir.toFile().getAbsolutePath());
         skinAtlas.put(mod.getModId(), new TextureAtlas(Gdx.files.absolute(tempDir.resolve(mod.getModId() + ".atlas").toAbsolutePath().toString())));
      }
   }
}
