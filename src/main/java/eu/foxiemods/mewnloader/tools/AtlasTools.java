package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AtlasTools {
   private static ArrayList<String> registeredSprites;
   private TexturePacker packer;

   static {
      registeredSprites = new ArrayList<>();
   }

   public AtlasTools() {
      packer = new TexturePacker(new TexturePacker.Settings());
   }

   public void addTexture(String modid, String sprite) {
      if (registeredSprites.contains(sprite)) {
         Gdx.app.error(modid, "This sprite has already been loaded!");
         return;
      }

      FileHandle spriteFile = Gdx.files.internal(modid + "/sprites/" + sprite + ".png");
      if(!spriteFile.exists()) {
         Gdx.app.error(modid, "Missing sprite " + sprite);
         return;
      }

      try {
         packer.addImage(ImageIO.read(spriteFile.read()), sprite);
      } catch (IOException e) {
         Gdx.app.error(modid, "Error adding a sprite: " + e.getMessage());
      }
   }

   public void addTextures(String modid, List<String> sprites) {
      for(String sprite : sprites) {
         addTexture(modid, sprite);
      }
   }

   public void dump(Path path, String type) {
      if (path.resolve(type + ".atlas").toFile().exists()) {
         path.resolve(type + ".atlas").toFile().delete();
      }
      File target = path.toFile();
      packer.pack(target, type + ".atlas");
   }
}
