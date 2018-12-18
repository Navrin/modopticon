package uk.co.samwho.modopticon.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.TYPE_PARAMETER, ElementType.PARAMETER, ElementType.TYPE_USE})
public @interface Init {
}
