package eu.foxiemods.mewnloader;

public interface MewnMod {
   String getModName();

   String getVersion();

   String getModId();

   void preinit();

   void init();

   void postinit();
}
