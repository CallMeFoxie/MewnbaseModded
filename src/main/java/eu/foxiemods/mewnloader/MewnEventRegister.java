package eu.foxiemods.mewnloader;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MewnEventRegister {
   String eventName();
}
