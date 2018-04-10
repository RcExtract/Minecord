package com.rcextract.minecord;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.bukkit.event.EventPriority;

@Retention(RUNTIME)
@Target(METHOD)
public @interface CommandHandler {

	String value();
	EventPriority priority() default EventPriority.MONITOR;
}
