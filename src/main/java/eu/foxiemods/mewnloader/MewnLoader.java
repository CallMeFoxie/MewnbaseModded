package eu.foxiemods.mewnloader;

import com.badlogic.gdx.Gdx;
import eu.foxiemods.mewnloader.tools.ItemsLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.ServiceLoader;

public class MewnLoader extends URLClassLoader {

   public static ArrayList<MewnMod> MewnMods;

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

      URLClassLoader systemClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
      Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
      Method method = null;
      try {
         method = classLoaderClass.getDeclaredMethod("addURL", new Class[]{URL.class});
         method.setAccessible(true);
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
         Gdx.app.exit();
      }


      for(File file : files) {
         if(file.isFile() && file.getName().endsWith(".jar")) {
            try {
               method.invoke(systemClassLoader, new Object[] {file.toURI().toURL()});
            } catch (MalformedURLException e) {
               e.printStackTrace();
            } catch (IllegalAccessException e) {
               e.printStackTrace();
            } catch (InvocationTargetException e) {
               e.printStackTrace();
            }
         }
      }

      ServiceLoader<MewnMod> sloader = ServiceLoader.load(MewnMod.class, systemClassLoader);
      for (MewnMod mod : sloader) {
         System.out.println("Loaded mod: " + mod.getModName());
         MewnMods.add(mod);
      }

      MewnEvent.registerEvents();
      modPreinit();
   }

   public static void modPreinit() {
      ItemsLoader.LoadItems();
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
