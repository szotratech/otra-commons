package cn.otra.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 用于描述用户自定义类的属性
 * @author xiaodx
 *
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EcField {

	String value();
	
	/**
	 * examples:<br/>
	 * 		<b>boolean to String:expression="{true=正确,false=错误}"</b><br/>
	 * 		<b>number  to String:expression="{0=停用,1=启用,2~3=失败,4~5=异常}"</b>
	 * @return
	 */
	String replace() default "";
	
	String type() default "";
	
	/**
	 * <b>显示Excel的列宽</b><br/>
	 * <b>注：英文字母、数字字符各占一个单位，一个汉字占两个单位</b><br/>
	 * -1不指定
	 * @return
	 */
	int width() default -1;
	
}