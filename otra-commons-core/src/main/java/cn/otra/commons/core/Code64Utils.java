package cn.otra.commons.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 将数字转换成字符串，混淆显示
 * @author xiaodx
 *
 */
public class Code64Utils {

	private static char[] codeBases = "OIU-Q3h8DEFG6ABtCLMpNHJ1KnT9W0VPSR_YXZfg2debca4olmjk5iwvusrq7zyx".toCharArray();
	private static final Map<Character,Integer> idxMap = new HashMap<Character, Integer>(64);
	
	public static final void setBase(String base64String) {
		codeBases = base64String.toCharArray();
		if(codeBases.length != 64) {
			throw new RuntimeException("base64String.lengh() must be 64.");
		}
		init();
	}
	
	private static final void init() {
		idxMap.clear();
		for(int i=0;i<codeBases.length;i++) {
			idxMap.put(codeBases[i], i);
		}
	}
	
	static {
		init();
	}
	
	/**
	 * 将一个数字转换成字符串，长度至少为4位<br/>
	 * <b>4位即可表示所有大于0,小于1千500万的整数(int,64x63x62x61)</b><br/>
	 * <b>6位即可表示所有大于0的整数(int,64x63x62x61x60x59 > Integer.MAX_VALUE（2147483647 ）)</b><br/>
	 * <b>11位即可表示所有大于0的长整数(long > Long.MAX_VALUE（9223372036854775807）)</b>
	 * @param value
	 * @return
	 */
	public static final String encode(long value) {
		if(value < 0) {
			throw new RuntimeException("input value must greater than 0! "+value+"<0");
		}
		char buf[] = new char[64];
		boolean negative = (value < 0);
		int charPos = 63;

		if (!negative) {
			value = -value;
		}
		Arrays.fill(buf, 60, 63, codeBases[0]);
		while (value <= -64) {
			buf[charPos--] = codeBases[-(int)(value % 64)];
			value = value / 64;
		}
		buf[charPos] = codeBases[-(int)value];

		if (negative) {
			buf[--charPos] = '-';
		}
		if(64-charPos < 4) {
			charPos = 60;
		}
		return new String(buf, charPos, 64-charPos);
	}
	
	/**
	 * 将一个字符串转换成数字
	 * @param value
	 * @return
	 */
	public static final long decode(String value) {
		char[] ds = value.toCharArray();
		long result = 0;
		for(int i=0;i<ds.length;i++) {
			Character c = ds[i];
			Integer idx = idxMap.get(c);
			if(idx == null) {
				throw new RuntimeException("invalid char '"+c+"' in value!");
			}
			int power = ds.length - i -1;
//			System.err.println("power="+power);
//			result += (powerMap.get(power)*idx);
			result += (Math.pow(64, power)*idx);
		}
		return result;
	}
	
	public static void main(String[] args) {
//		for(int i=0;i<20;i++) {
//			String result = encode(i);
//			System.err.println(result+","+decode(result));
//		}
//		System.err.println(Integer.MAX_VALUE);
		String result = encode(Integer.MAX_VALUE);
		System.err.println(result+","+decode(result));
//		long st = System.currentTimeMillis();
//		for(int i=0;i<1000;i++) {
//			encode(Long.MAX_VALUE);
//			decode(result);
//		}
//		System.err.println(System.currentTimeMillis() - st);
//		long a = 64*63*62*61*60*59l;
//		System.err.println(a);
//		System.err.println(Integer.MAX_VALUE);
		
	}
	
	static final int d(int v) {
		if(v == 1) {
			return 1;
		}
		return v*d(v-1);
	}
	

}
