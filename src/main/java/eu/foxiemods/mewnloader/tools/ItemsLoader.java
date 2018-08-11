package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.cairn4.moonbase.ItemData;
import com.cairn4.moonbase.ItemDataSet;
import com.cairn4.moonbase.ItemFactory;
import eu.foxiemods.mewnloader.MewnLoader;
import eu.foxiemods.mewnloader.MewnMod;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.badlogic.gdx.net.HttpRequestBuilder.json;

public class ItemsLoader {
   public static void LoadItems() throws IOException {
      Path tempDir = Files.createTempDirectory("run");
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
            packer.addImage(ImageIO.read(spriteFile.read()), itemData.sprite);
         }
         Gdx.app.debug(mod.getModId(), "Adding items from mod");
         ItemFactory.ITEMDATALIST.addAll(itemDataSet.itemDataList);
         packer.pack(tempDir.toFile(), mod.getModId() + ".atlas");
         Gdx.app.debug("test", "generated atlas file on path: " + tempDir.toFile().getAbsolutePath());
      }
   }
}
