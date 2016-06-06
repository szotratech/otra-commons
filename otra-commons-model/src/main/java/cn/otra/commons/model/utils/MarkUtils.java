package cn.otra.commons.model.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.otra.commons.annotation.Mark;

public class MarkUtils {

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static final Map<String, String> getFieldMarks(Class<?> clazz) {
		Map<String, String> marks = new HashMap<String, String>();
		parseMarks(clazz, "", marks);
		return marks;
	}

	public static final String getMarks(Class<?> clazz) {
		if (clazz == null) {
			return null;
		}
		Mark clazzMark = clazz.getAnnotation(Mark.class);
		if (clazzMark != null) {
			return clazzMark.value();
		}
		return null;
	}

	public static final String getMethodMarks(Method method) {
		Mark mark = method.getAnnotation(Mark.class);
		if (mark != null) {
			return mark.value();
		}
		return null;
	}

	private static final void parseMarks(Class<?> clazz, String prefix,
			Map<String, String> marks) {
		if (clazz == null) {
			return;
		}
		// Mark clazzMark = clazz.getAnnotation(Mark.class);
		// if(clazzMark != null) {
		// marks.put(prefix, clazzMark.value());
		// }
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isFinal(mod) || Modifier.isStatic(mod)) {
				continue;
			}
			Mark fieldMark = field.getAnnotation(Mark.class);
			if (fieldMark != null) {
				if (prefix == null || prefix.trim().length() == 0) {
					marks.put(field.getName(), fieldMark.value());
				} else {
					marks.put(prefix + "." + field.getName(), fieldMark.value());
				}
			}
			Class<?> typeClass = field.getType();
			Class<?> newClass = field.getType();
			if(List.class.isAssignableFrom(typeClass)) {
				ParameterizedType pt = (ParameterizedType) field.getGenericType();
				newClass = (Class<?>)pt.getActualTypeArguments()[0];
			} else if(typeClass.isArray()) {
				newClass = typeClass.getComponentType();
			} else if (fieldMark != null && fieldMark.markClass() != null
					&& fieldMark.markClass() != Object.class) {
				newClass = fieldMark.markClass();
			}

			if (prefix == null || prefix.trim().length() == 0) {
				parseMarks(newClass, field.getName(), marks);
			} else {
				parseMarks(newClass, prefix + "." + field.getName(), marks);
			}

		}
	}

	private static final String tag0 = "";
	private static final String tag1 = "	";
	private static final String tag2 = "		";
	private static final String tag3 = "			";
	private static final String tag4 = "				";
	private static final String tag5 = "					";
	private static final String tag6 = "						";
	private static final String tag7 = "							";
	private static final String tag8 = "								";
	private static final String tag9 = "																												";

	private static final String getTag(int tagIdx) {
		switch (tagIdx) {
		case 0:
			return tag0;
		case 1:
			return tag1;
		case 2:
			return tag2;
		case 3:
			return tag3;
		case 4:
			return tag4;
		case 5:
			return tag5;
		case 6:
			return tag6;
		case 7:
			return tag7;
		case 8:
			return tag8;
		case 9:
			return tag9;
		}
		return tag9;
	}

	public static final String getSimpleJson(Class<?> clazz, int tagIdx)
			throws Exception {
		if (clazz == Object.class || !(clazz instanceof Serializable)) {
			return null;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("{\r\n");
		if (clazz != Object.class && clazz instanceof Serializable) {
			Field[] fields = clazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				Class<?> fClass = field.getType();
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				builder.append(getTag(tagIdx + 1)).append("\"")
						.append(field.getName()).append("\": ");
				
				if (fClass.isArray()) {
					builder.append("[");
					builder.append(
							getSimpleJson(fClass.getComponentType(), tagIdx + 1))
							.append("]");
				} else if (fClass == List.class) {
					builder.append("[");
					Mark mark = field.getAnnotation(Mark.class);
					if (mark == null) {
						builder.append("]");
					} else {
						ParameterizedType pt = (ParameterizedType) field.getGenericType();
						builder.append(
								getSimpleJson((Class<?>)pt.getActualTypeArguments()[0], tagIdx + 1))
								.append("]");
					}
				} else {
					if (Number.class.isAssignableFrom(fClass)
							|| fClass == Double.class || fClass == Float.class
							|| fClass == double.class || fClass == float.class
							|| fClass == int.class || fClass == long.class
							|| fClass == Long.class || fClass == Integer.class) {
						builder.append(0);
					} else if (fClass == String.class) {
						if (field.getName() != null
								&& field.getName().equals("msg")) {
							builder.append("\"调用成功\"");
						} else {
							builder.append("\"\"");
						}
					} else if (fClass == Character.class) {
						builder.append("\'\'");
					} else if (fClass == Boolean.class
							|| fClass == boolean.class) {
						builder.append("true");
					} else if (fClass == Date.class) {
						builder.append("\"").append(FORMAT.format(new Date()))
								.append("\"");
					} else {
						builder.append(getSimpleJson(fClass, tagIdx + 1));
					}
				}
				if (i < fields.length - 1) {
					builder.append(",").append("\r\n");
				}
			}
		}
		return builder.append("\r\n").append(getTag(tagIdx)).append("}")
				.toString();
	}

	public static final void processMarksForFile(String textFile) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(textFile));
			String tmp = null;
			while ((tmp = reader.readLine()) != null) {
				tmp = tmp.trim();
				if (tmp.contains("//")) {
					String commen = tmp.split("//")[1];
					// @Mark("机动车序号")
					System.err.println("@Mark(\"" + commen.trim() + "\")");
					System.err.println(tmp);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	@Mark("Foo 类")
	static class Foo {
		@Mark("Foo.name")
		private String name;
		@Mark("Foo.boo")
		private Boo boo;

		static class Boo {
			@Mark("Boo.name")
			private String name;

			@Mark("Boo.aoo")
			private Aoo aoo;

			@Mark("Boo.aoos")
			List<Aoo> aoos;

			static class Aoo {
				@Mark("Aoo.name")
				private String name;
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// System.err.println(getFieldMarks(Foo.class));
		 System.out.println(getSimpleJson(Foo.class,0));
//		Field []fields = Boo.class.getDeclaredFields();
//		for(Field field:fields) {
//			System.err.println(field.getName()+":"+((Class<?>)field.getGenericType()).getName());
//		}
//		ParameterizedType pt = (ParameterizedType)Boo.class.getField("aoos").getGenericType();
//		System.out.println(pt.getActualTypeArguments().length);
//		System.out.println(pt.getActualTypeArguments()[0]);
	}

}
