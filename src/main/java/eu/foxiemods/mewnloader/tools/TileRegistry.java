package eu.foxiemods.mewnloader.tools;

import com.badlogic.gdx.Gdx;
import com.cairn4.moonbase.tiles.*;

import java.util.HashMap;

public class TileRegistry {
   private static HashMap<String, Class<? extends Tile>> tileMap;

   static {
      tileMap = new HashMap<>();

      registerTile("AirCleaner", AirCleaner.class);
      registerTile("Airlock", Airlock.class);
      registerTile("AutoAirlock", AutoAirlock.class);
      registerTile("BaseModule", BaseModule.class);
      registerTile("Battery", Battery.class);
      registerTile("CommsTower", CommsTower.class);
      registerTile("Conduit", Conduit.class);
      registerTile("ConstructionYard", ConstructionYard.class);
      registerTile("CraftingStation", CraftingStation.class);
      registerTile("Crater", Crater.class);
      registerTile("FloodLight", FloodLight.class);
      registerTile("FuelTank", FuelTank.class);
      registerTile("Garage", Garage.class);
      registerTile("Generator", Generator.class);
      registerTile("GravelFloor", GravelFloor.class);
      registerTile("GreenHouse", GreenHouse.class);
      registerTile("HabitatModule", HabitatModule.class);
      registerTile("ItemDropper", ItemDropper.class);
      registerTile("ItemPile", ItemPile.class);
      registerTile("Lander", Lander.class);
      registerTile("LaunchPad", LaunchPad.class);
      registerTile("MedBay", MedBay.class);
      registerTile("NavLight", NavLight.class);
      registerTile("PavementFloor", PavementFloor.class);
      registerTile("PowerSwitch", PowerSwitch.class);
      registerTile("RainCollector", RainCollector.class);
      registerTile("Recycler", Recycler.class);
      registerTile("Refinery", Refinery.class);
      registerTile("RepairDroneBase", RepairDroneBase.class);
      registerTile("ResearchObject", ResearchObject.class);
      registerTile("Rtg", Rtg.class);
      registerTile("ScienceLab", ScienceLab.class);
      registerTile("Smelter", Smelter.class);
      registerTile("SolarPanel", SolarPanel.class);
      registerTile("StorageCrate", StorageCrate.class);
      registerTile("StoragePile", StoragePile.class);
      registerTile("Tile", Tile.class);
      registerTile("WallLight", WallLight.class);
      registerTile("WaterSpigot", WaterSpigot.class);
      registerTile("WaterSupply", WaterSupply.class);
   }

   public static void registerTile(String tileName, Class<? extends Tile> tile) {
      if(tileMap.containsKey(tileName)) {
         Gdx.app.error("WAT", "This tile has been already registered!");
      }
      tileMap.put(tileName, tile);
   }

   public static Class<? extends Tile> getTileByName(String tileName) {
      return tileMap.get(tileName);
   }
}
