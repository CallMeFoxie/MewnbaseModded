package eu.foxiemods.mewnloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.ServiceLoader;

public class MewnLoader extends URLClassLoader {

   private static ArrayList<MewnMod> MewnMods;

   public MewnLoader(URL[] urls) {
      super(urls);
   }

   @Override
   public void addURL(URL url) {
      super.addURL(url);
   }

   public void addFile(File file) {
      try {
         addURL(file.toURI().toURL());
      } catch (MalformedURLException e) {
         e.printStackTrace();
      }

      System.out.println("Loaded file: " + file.getAbsolutePath());
   }

   public static void init() {

      File folder = new File("mods/");
      File[] files = folder.listFiles();

      MewnMods = new ArrayList<>();

      URLClassLoader currentLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
      MewnLoader l = new MewnLoader(currentLoader.getURLs());

      for(File file : files) {
         if(file.isFile() && file.getName().endsWith(".jar")) {
            l.addFile(file);
         }
      }

      ServiceLoader<MewnMod> sloader = ServiceLoader.load(MewnMod.class, l);
      for (MewnMod mod : sloader) {
         System.out.println("Loaded mod: " + mod.getModName());
         MewnMods.add(mod);
      }

      modPreinit();
   }

   public static void modPreinit() {
      for(MewnMod mod : MewnMods) {
         mod.preinit();
      }
   }

   public static void modInit() {
      for(MewnMod mod : MewnMods) {
         mod.init();
      }
   }

   public static void modPostinit() {
      for(MewnMod mod : MewnMods) {
         mod.postinit();
      }
   }
}
