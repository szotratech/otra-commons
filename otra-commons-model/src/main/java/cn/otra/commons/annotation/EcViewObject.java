package cn.otra.commons.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EcViewObject {

	/**
	 * 要导出数据所在的类
	 * @return
	 */
	Class<?> baseClass();
	
	/**
	 * 存放数据的key
	 * <br/>
	 * <b>请使用pageKey替代</b>
	 * @return
	 */
	@Deprecated 
	String dataKey() default "ECPage";
	
	String pageKey() default "pageData";
	
	/**
	 * 请求映射
	 * @return
	 */
	String []requestMapping();
	
	/**
	 * 导出的文件名
	 * @return
	 */
	String []fileName() default {};
	
}
