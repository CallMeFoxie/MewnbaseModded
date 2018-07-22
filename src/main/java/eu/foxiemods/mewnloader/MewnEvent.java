package eu.foxiemods.mewnloader;

public class MewnEvent {
   private String eventName;

   public MewnEvent(String name) {
      this.eventName = name;
   }

   public String getEventName() {
      return eventName;
   }

   public static void registerEvents() {
      MewnEventBus.registerEvent("itemsLoaded", ItemsLoaded.class);
      MewnEventBus.registerEvent("gameTickPre", GameTickPre.class);
      MewnEventBus.registerEvent("gameTickPost", GameTickPost.class);
   }

   public static class ItemsLoaded extends MewnEvent {
      public ItemsLoaded() {
         super("itemsLoaded");
      }

      public static void fire() {
         MewnEventBus.fireEvent(new ItemsLoaded());
      }
   }

   public static class GameTickPre extends MewnEvent {
      public GameTickPre() {
         super("gameTickPre");
      }

      public static void fire() {
         MewnEventBus.fireEvent(new GameTickPre());
      }
   }

   public static class GameTickPost extends MewnEvent {
      public GameTickPost() {
         super("gameTickPost");
      }

      public static void fire() {
         MewnEventBus.fireEvent(new GameTickPost());
      }
   }
}
