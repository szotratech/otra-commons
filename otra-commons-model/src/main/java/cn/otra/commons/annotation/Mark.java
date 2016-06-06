package cn.otra.commons.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author xiaodx
 * 类，方法，字段的说明
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE,ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Mark {
	String value() default "";
	
	/**
	 * 真实类
	 * @return
	 */
	Class<?> markClass() default Object.class;
	
	boolean required() default false;
	
}
