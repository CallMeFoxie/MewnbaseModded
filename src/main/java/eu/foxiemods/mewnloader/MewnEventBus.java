package eu.foxiemods.mewnloader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.badlogic.gdx.Gdx.app;

public class MewnEventBus {
   public static class MewnEventer {
      public final Object object;
      public final Method method;
      public final MewnMod mod;

      public MewnEventer(MewnMod mod, Object obj, Method meth) {
         this.object = obj;
         this.method = meth;
         this.mod = mod;
      }
   }

   private static HashMap<String, List<MewnEventer>> eventMethods;
   private static HashMap<Class<? extends MewnEvent>, String> events;

   static {
      eventMethods = new HashMap<>();
      events = new HashMap<>();
   }

   public static void registerEvent(String event, Class<? extends MewnEvent> clazz) {
      events.put(clazz, event);
   }

   static void fireEvent(MewnEvent event) {
      List<MewnEventer> eventers = eventMethods.get(event.getEventName());
      if (eventers == null) {
         return;
      }

      for (MewnEventer eventer : eventMethods.get(event.getEventName())) {
         try {
            eventer.method.invoke(eventer.object, event);
         } catch (IllegalAccessException e) {
            app.error(eventer.mod.getModName(), "Mod event error: tried accessing private field!");
         } catch (InvocationTargetException e) {
            app.error(eventer.mod.getModName(), "Mod event error: invocation target exception");
         }
      }
   }

   private static void registerEventHandler(MewnMod mod, Object object, Method method, String eventName) {
      MewnEventer eventer = new MewnEventer(mod, object, method);
      if(!eventMethods.containsKey(eventName)) {
         eventMethods.put(eventName, new ArrayList<MewnEventer>());
      }

      eventMethods.get(eventName).add(eventer);
   }

   public static void registerEventHandlers(MewnMod mod, Object object) {
      Method methods[] = object.getClass().getMethods();
      for (Method method : methods) {
         if(method.getParameterCount() == 1 && events.containsKey(method.getParameterTypes()[0])) {
            registerEventHandler(mod, object, method, events.get(method.getParameterTypes()[0]));
         }
      }
   }
}
