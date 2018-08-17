package eu.foxiemods.mewnloader.tools;

import java.util.ArrayList;
import java.util.HashMap;

public class CraftingRegistry {
   public ArrayList<String> buildables;

   public static HashMap<String, CraftingRegistry> registries;

   static {
      registries = new HashMap<>();
   }

   public static CraftingRegistry getCraftingRegistry(String name) {
      CraftingRegistry existing = registries.get(name);
      return existing == null ? createRegistry(name) : existing;
   }

   public static CraftingRegistry createRegistry(String name) {
      CraftingRegistry registry = new CraftingRegistry();
      registries.put(name, registry);
      return registry;
   }

   public CraftingRegistry() {
      buildables = new ArrayList<>();
   }
}
