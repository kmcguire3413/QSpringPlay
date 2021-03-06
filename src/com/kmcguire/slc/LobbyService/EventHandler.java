package com.kmcguire.slc.LobbyService;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler
{
    public abstract EventPriority       priority() default EventPriority.NORMAL;
}
