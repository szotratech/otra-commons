package cn.otra.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonDateFormat {
	String format1 = "yyyy-MM-dd";
	String format2 = "yyyy-MM-dd HH:mm:ss";
	String value() default format2;
	
}
