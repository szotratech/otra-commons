package cn.otra.commons.httpclient.vo;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.HttpStatus;

public class HttpResult {

	
	private byte[] bytes;
	private String charset;
	private int code;
	private String data;

	public HttpResult() {
		super();
	}

	public HttpResult(int code, String data,String charset, byte[] bytes) {
		super();
		this.code = code;
		this.data = data;
		this.charset = charset;
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
	public String getCharset() {
		return charset;
	}

	public int getCode() {
		return code;
	}

	public String getData() {
		if(data == null && charset != null) {
			try {
				data= new String(bytes,charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setData(String data) {
		this.data = data;
	}

	/**
	 * 请求成功，响应为200
	 * @return
	 */
	public boolean success() {
		return code == HttpStatus.SC_OK;
	}

	@Override
	public String toString() {
		return "HttpResult [code=" + code + ", data=" + getData() + ", bytes="
				+ Arrays.toString(bytes) + "]";
	}


}
