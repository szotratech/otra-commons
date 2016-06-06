//package cn.otra.commons.httpclient;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.ConnectException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.nio.charset.Charset;
//import java.security.KeyStore;
//import java.security.cert.CertificateException;
//import java.security.cert.X509Certificate;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ConcurrentHashMap;
//
//import javax.net.ssl.SSLContext;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpHost;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.config.RequestConfig;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.CloseableHttpResponse;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.apache.http.conn.ConnectTimeoutException;
//import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
//import org.apache.http.conn.ssl.SSLContextBuilder;
//import org.apache.http.conn.ssl.SSLContexts;
//import org.apache.http.conn.ssl.TrustStrategy;
//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.StringBody;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.LaxRedirectStrategy;
//import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.jsoup.Jsoup;
//import org.jsoup.helper.StringUtil;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import cn.otra.commons.httpclient.vo.CertInfo;
//import cn.otra.commons.httpclient.vo.FileInput;
//import cn.otra.commons.httpclient.vo.HttpResult;
//import cn.otra.commons.httpclient.vo.Timeout;
//
///**
// * @author xiaodx
// * Hello world!
// * 
// */
//public class HttpClientUtils {
//
//	private static final Logger LOG = Logger.getLogger(HttpClientUtils.class);
//	private static RequestConfig DEFAULT_REQUEST_CONFIG = RequestConfig
//			.custom().setSocketTimeout(120000)// 请求超时|读取超时
//			.setConnectTimeout(120000).build();// 连接超时
//	
//	private static final String DEFAULT_CHARSET = "utf-8";
//	private static boolean hasSetAllTimeout = false;//host = "*"
//	private static final String GLOBLE_HOST = "*";//全局host
//	private static final Map<String, Timeout> timeoutMap = new ConcurrentHashMap<String, Timeout>();
//	
////	private static final ThreadLocal<CloseableHttpClient> clientCache = new ThreadLocal<CloseableHttpClient>() {
////		protected CloseableHttpClient initialValue() {
////			//支持重定向
////			return HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
////		};
////	};
//	
//	private static final Map<String, CloseableHttpClient> httpsCertHttpClientMap = new ConcurrentHashMap<String, CloseableHttpClient>();
//	private static final Map<String, CloseableHttpClient> httpClientMap = new ConcurrentHashMap<String, CloseableHttpClient>();
//	
//	private static final Map<String, CertInfo> httpsCertMap = new ConcurrentHashMap<String, CertInfo>();
//	
//	/**
//	 * 重置httpClient
//	 * @param host
//	 */
//	public static final void resetHttpClient(String host) {
////		clientCache.remove();
//		String hostKey = getHost(host);
//		if(hostKey != null && hostKey.trim().length() > 0) {
//			httpClientMap.remove(host);
//			httpsCertHttpClientMap.remove(hostKey);
//		}
//	}
//	
//	@Deprecated
//	public static final void setTimeoutMs(int socketTimeout,int connectTimeout,int connectionRequestTimeout) {
//		DEFAULT_REQUEST_CONFIG = RequestConfig
//				.custom().setSocketTimeout(socketTimeout)// 请求超时
//				.setConnectTimeout(connectTimeout)// 传输超时
//				.setConnectionRequestTimeout(connectionRequestTimeout).build();// 设置请求和传输超时时间
//	}
//	
//	public static final String getHost(String hostOrUrl) {
//		if(hostOrUrl == null) {
//			return null;
//		}
//		if(hostOrUrl.trim().length() == 0) {
//			return null;
//		}
//		if(hostOrUrl.startsWith("http:")) {
//			hostOrUrl = hostOrUrl.replace("http://", "");
//		}
//		if(hostOrUrl.startsWith("https:")) {
//			hostOrUrl = hostOrUrl.replace("https://", "");
//		}
//		if(hostOrUrl.contains("/")) {
//			hostOrUrl = hostOrUrl.substring(0,hostOrUrl.indexOf("/"));
//		}
//		if(hostOrUrl.contains(":")) {
//			hostOrUrl = hostOrUrl.substring(0,hostOrUrl.indexOf(":"));
//		}
//		return hostOrUrl;
//	}
//	
//	/**
//	 * 设置超时,单位为毫秒
//	 * @param host 访问服务器的host,如www.baidu.com
//	 * @param socketTimeout 请求超时|读取超时
//	 * @param connectTimeout 连接超时
//	 */
//	public static final void setTimeoutMillis(String host,int socketTimeout,int connectTimeout) {
//		if(host == null) {
//			throw new RuntimeException("host can not be null.");
//		}
//		if(hasSetAllTimeout && !host.equals(GLOBLE_HOST)) {
//			throw new RuntimeException("全局设置已经开启，无法设置其他超时.");
//		}
//		
//		host = getHost(host);
//		
//		Timeout timeout = timeoutMap.get(host);
//		if(timeout != null) {
//			LOG.warn("host["+host+"]'s socketTimeout="+timeout.getSocketTimeout()+",connectTimeout="+timeout.getConnectTimeout()+" will be replace by socketTimeout="+socketTimeout+",connectTimeout="+connectTimeout);
//			timeout.setConnectTimeout(connectTimeout);
//			timeout.setSocketTimeout(socketTimeout);
//		} else {
//			timeout = new Timeout(host, socketTimeout, connectTimeout);
//		}
//		if(host.equals(GLOBLE_HOST)) {
//			timeoutMap.clear();
//			timeoutMap.put(host, timeout);
//			hasSetAllTimeout = true;
//		} else {
//			timeoutMap.put(host, timeout);
//		}
//		LOG.info("超时设置："+timeoutMap);
//	}
//	
//	/**
//	 * 设置超时,单位为秒
//	 * @param host 访问服务器的host,如www.baidu.com
//	 * @param socketTimeout 请求超时|读取超时
//	 * @param connectTimeout 连接超时
//	 */
//	public static final void setTimeoutSecond(String host,int socketTimeout,int connectTimeout) {
//		setTimeoutMillis(host, socketTimeout*1000, connectTimeout*1000);
//	}
//	
//	private static final void setTimeout(HttpGet httpGet) {
//		LOG.debug("setTimeout["+httpGet.getURI().getHost()+"]");
//		
//		Timeout timeout = timeoutMap.get(GLOBLE_HOST);
//		if(timeout == null) {
//			timeout = timeoutMap.get(httpGet.getURI().getHost());
//		}
//		if(timeout != null) {
//			httpGet.setConfig(RequestConfig
//					.custom().setSocketTimeout(timeout.getSocketTimeout())// 请求超时|读取超时
//					.setConnectTimeout(timeout.getConnectTimeout()).build()// 连接超时
//					);
//			return;
//		}
//		httpGet.setConfig(DEFAULT_REQUEST_CONFIG);
//	}
//	
//	private static final void setTimeout(HttpPost httpPost) {
//		LOG.debug("setTimeout["+httpPost.getURI().getHost()+"]");
//		Timeout timeout = timeoutMap.get(GLOBLE_HOST);
//		if(timeout == null) {
//			timeout = timeoutMap.get(httpPost.getURI().getHost());
//		}
//		if(timeout != null) {
//			httpPost.setConfig(RequestConfig
//					.custom().setSocketTimeout(timeout.getSocketTimeout())// 请求超时|读取超时
//					.setConnectTimeout(timeout.getConnectTimeout()).build()// 连接超时
//					);
//			return;
//		}
//		httpPost.setConfig(DEFAULT_REQUEST_CONFIG);
//	}
//	
//	public static final CloseableHttpClient createSimpleHttpClient(String host) {
//		if(host == null) {
//			throw new RuntimeException("host is null");
//		}
//		CloseableHttpClient httpClient = httpClientMap.get(host);
//		if(httpClient == null) {
//			PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//			cm.setMaxTotal(20);//连接池最大并发连接数
//			cm.setDefaultMaxPerRoute(5);//单路由最大并发数
//			httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).setConnectionManager(cm).build();
////			httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
//			httpClientMap.put(host, httpClient);
//		}
//		return httpClient;
//	}
//	
//	private static final CloseableHttpClient getHttpClient(String url) {
//		CloseableHttpClient httpClient = null;
//		String host = getHost(url);
//		if(url.startsWith("https")) {
//			httpClient = createSSLClientDefault(host);	
//		} else {
//			httpClient = createSimpleHttpClient(host);
//		}
//		return httpClient;
//	}
//	
//	public static final HttpResult post(String url,HttpHost proxy, Map<String, String> headers,Map<String, String> params,String sendEncoding,FileInput ...files) {
//		if(LOG.isDebugEnabled()) {
//			LOG.debug("url="+url+",proxy = "+proxy+",headers="+headers+",params="+params);
//		}
//		CloseableHttpResponse response = null;
//		CloseableHttpClient httpClient = null;
//		httpClient = getHttpClient(url);
//		HttpPost post = null;
//		int code = HttpStatus.SC_OK;
//		
//		Map<String, String> extMap = getPramasFromUri(url);
//		if(extMap.size() > 0) {
//			if(params == null) {
//				params = extMap;
//			} else {
//				params.putAll(extMap);
//			}
//		}
//		
//		try {
//			post = new HttpPost(url);
//			if(files.length > 0) {
//				MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE);//.addBinaryBody(name, b)
//				for(FileInput input:files) {
//					entityBuilder.addBinaryBody(input.getName(), input.getFile());
//				}
//				
//				if (!isEmptyCollection(params)) {
//					entityBuilder.setCharset(Charset.forName(DEFAULT_CHARSET));
//					
//					for(Map.Entry<String, String> en:params.entrySet()) {
//						
//						ContentType contentType = ContentType.create("text/plain", DEFAULT_CHARSET);
//						StringBody stringBody=new StringBody(en.getValue(),contentType);
//						entityBuilder.addPart(en.getKey(),stringBody);
//					}
//				}
//				post.setEntity(entityBuilder.build());
//			} else {
//				if (!isEmptyCollection(params)) {
//					if(sendEncoding != null && sendEncoding.trim().length() > 0) {
//						sendEncoding = sendEncoding.toLowerCase();
//					} else {
//						sendEncoding = DEFAULT_CHARSET;
//					}
//					post.setEntity(new UrlEncodedFormEntity(
//							getNameValuePairsFromMap(params), sendEncoding));
//				}
//			}
//			
//			if(headers == null) {
//				headers = new HashMap<String, String>();
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			String userAgent = headers.get("User-Agent");
//			if(userAgent == null || userAgent.trim().length() == 0) {
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			for (Entry<String, String> header : headers.entrySet()) {
//				post.setHeader(header.getKey(), header.getValue());
//			}
//
//			setTimeout(post);
//
//			if (proxy != null) {
//				response = httpClient.execute(proxy, post);
//			} else {
//				response = httpClient.execute(post);
//			}
//			LOG.info("url=" + url + ",response=" + response);
//			code = response.getStatusLine().getStatusCode();
//			if (code != HttpStatus.SC_OK) {
//				LOG.error("httpPost Status: "
//						+ response.getStatusLine().getStatusCode() + ",Reason:"
//						+ response.getStatusLine().getReasonPhrase());
//				post.abort();
//			}
//			HttpEntity entity = response.getEntity();
//			byte [] bytes = EntityUtils.toByteArray(entity);
//			String charset = getCharsetEncodingFromEntity(entity,bytes);
//			return new HttpResult(code, null,charset, bytes);
//		} catch (ConnectTimeoutException e) {//请求超时
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//503服务暂时不可用（服务器由于维护或者负载过重未能应答）
//			}
//			if(post != null) {
//				post.abort();
//			}
//			LOG.error(url,e);
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		}  catch (ConnectException e) {//找不到服务
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//
//			}
//			if(post != null) {
//				post.abort();
//			}
//			LOG.error(url,e);
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		} catch (Exception e) {
//			if(post != null) {
//				post.abort();
//			}
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
//			}
//			LOG.error(url,e);
//			return new HttpResult(code, "服务器异常。url=["+url+"]",null, null);
//		} finally {
//			closeResponse(response);
//		}
//	}
//	
//	public static final HttpResult postString(String url,HttpHost proxy, Map<String, String> headers,String stringValue,String sendEncoding) {
//		if(LOG.isDebugEnabled()) {
//			LOG.debug("url="+url+",proxy = "+proxy+",headers="+headers+",stringValue="+stringValue);
//		}
//		if(sendEncoding == null || sendEncoding.trim().length() == 0) {
//			sendEncoding = DEFAULT_CHARSET;
//		}
//		CloseableHttpResponse response = null;
//		CloseableHttpClient httpClient = null;
//		httpClient = getHttpClient(url);
//		HttpPost post = null;
//		int code = HttpStatus.SC_OK;
//		
//		try {
//			post = new HttpPost(url);
//			
//			if(stringValue != null && stringValue.trim().length() > 0) {
//				post.setEntity(new StringEntity(stringValue, sendEncoding));
//			}
//			
//			if(headers == null) {
//				headers = new HashMap<String, String>();
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			String userAgent = headers.get("User-Agent");
//			if(userAgent == null || userAgent.trim().length() == 0) {
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			for (Entry<String, String> header : headers.entrySet()) {
//				post.setHeader(header.getKey(), header.getValue());
//			}
//
//			setTimeout(post);
//
//			if (proxy != null) {
//				response = httpClient.execute(proxy, post);
//			} else {
//				response = httpClient.execute(post);
//			}
//			LOG.info("url=" + url + ",response=" + response);
//			code = response.getStatusLine().getStatusCode();
//			if (code != HttpStatus.SC_OK) {
//				LOG.error("httpPost Status: "
//						+ response.getStatusLine().getStatusCode() + ",Reason:"
//						+ response.getStatusLine().getReasonPhrase());
//				post.abort();
//			}
//			HttpEntity entity = response.getEntity();
//			byte [] bytes = EntityUtils.toByteArray(entity);
//			String charset = getCharsetEncodingFromEntity(entity,bytes);
//			return new HttpResult(code, null,charset, bytes);
//		} catch (ConnectTimeoutException e) {//请求超时
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//503服务暂时不可用（服务器由于维护或者负载过重未能应答）
//			}
//			if(post != null) {
//				post.abort();
//			}
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		}  catch (ConnectException e) {//找不到服务
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//
//			}
//			if(post != null) {
//				post.abort();
//			}
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		} catch (Exception e) {
//			if(post != null) {
//				post.abort();
//			}
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
//			}
//			LOG.error(url,e);
//			return new HttpResult(code, "服务器异常。url=["+url+"]",null, null);
//		} finally {
//			closeResponse(response);
//		}
//	}
//
//	public static final HttpResult get(String uri,HttpHost proxy, Map<String, String> headers,Map<String, String> params,String sendEncoding) {
//		try {
//			return get(new URI(uri), proxy, headers, params, sendEncoding);
//		} catch (URISyntaxException e) {
//			LOG.error(uri,e);
//			int code = 500;
//			return new HttpResult(code, "服务器异常,url error,url=["+uri+"]",null, null);
//		}
//	}
//	
//	public static final HttpResult get(URI uri,HttpHost proxy, Map<String, String> headers,Map<String, String> params,String sendEncoding) {
//		CloseableHttpResponse response = null;
//		CloseableHttpClient httpClient = null;
//		httpClient = getHttpClient(uri.toString());
//		HttpGet get = null;
//		int code = HttpStatus.SC_OK;
//		try {
//			if (!isEmptyCollection(params)) {
//				String paramsStr = "?"
//						+ URLEncodedUtils.format(
//								getNameValuePairsFromMap(params), StringUtil.isBlank(sendEncoding)?DEFAULT_CHARSET:sendEncoding);
//				get = new HttpGet(uri + paramsStr);
//				if (LOG.isDebugEnabled()) {
//					LOG.debug(uri + paramsStr);
//				}
//			} else {
//				get = new HttpGet(uri);
//			}
//
//			setTimeout(get);
//
//			if(headers == null) {
//				headers = new HashMap<String, String>();
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			String userAgent = headers.get("User-Agent");
//			if(userAgent == null || userAgent.trim().length() == 0) {
//				headers.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.124 Safari/537.36");
//			}
//			for (Entry<String, String> header : headers.entrySet()) {
//					get.addHeader(header.getKey(), header.getValue());
//			}
//			if (proxy != null) {
//				response = httpClient.execute(proxy, get);
//			} else {
//				response = httpClient.execute(get);
//			}
//			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//				LOG.error("httpGet Status: "
//						+ response.getStatusLine().getStatusCode() + ",Reason:"
//						+ response.getStatusLine().getReasonPhrase());
//				get.abort();
//				code = response.getStatusLine().getStatusCode();
//			}
//			LOG.info("url=" + uri + ",response=" + response);
//			byte [] bytes = EntityUtils.toByteArray(response.getEntity());
//			String charset = getCharsetEncodingFromEntity(response.getEntity(),bytes);
//			return new HttpResult(code, null,charset, bytes);
//		} catch (ConnectTimeoutException e) {//请求超时
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//503服务暂时不可用（服务器由于维护或者负载过重未能应答）
//			}
//			if(get != null) {
//				get.abort();
//			}
//			LOG.error(uri,e);
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		}  catch (ConnectException e) {//找不到服务
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_SERVICE_UNAVAILABLE;//
//			}
//			if(get != null) {
//				get.abort();
//			}
//			LOG.error(uri,e);
//			return new HttpResult(code, "服务暂时不可用（服务器由于维护或者负载过重未能应答）",null, null);
//		} catch (Exception e) {
//			if(get != null) {
//				get.abort();
//			}
//			if(code == HttpStatus.SC_OK) {
//				code = HttpStatus.SC_INTERNAL_SERVER_ERROR;
//			}
//			LOG.error(uri,e);
//			return new HttpResult(code, "服务器异常",null, null);
//		} finally {
//			closeResponse(response);
//		}
//	}
//	
//	/**
//	 * 添加https证书
//	 * @param host
//	 * @param keyStoreType
//	 * @param filePath
//	 * @param password
//	 */
//	public static final void addHttpsCert(String host,String keyStoreType,String filePath,String password) {
//		host = getHost(host);
//		File file = new File(filePath);
//		if(!file.exists()) {
//			throw new RuntimeException("file ["+filePath+"] not found!");
//		}
//		if(password == null || password.trim().length() == 0) {
//			throw new RuntimeException("password is empty!");
//		}
//		CertInfo certInfo = new CertInfo(host, keyStoreType, filePath, password);
//		httpsCertMap.put(host, certInfo);
//	}
//	
//	private static final CertInfo getCertInfo(String host) {
//		host = getHost(host);
//		if(host == null) {
//			return null;
//		}
//		return httpsCertMap.get(host);
//	}
//	
//	
//	
//	
//	
//
//	public static CloseableHttpClient createSSLClientDefault() {
//		try {
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
//					null, new TrustStrategy() {
//						// 信任所有
//						public boolean isTrusted(X509Certificate[] chain,
//								String authType) throws CertificateException {
//							return true;
//						}
//					}).build();
//			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//					sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//			return HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy()).setSSLSocketFactory(sslsf).build();
//		} catch (Exception e) {
//			LOG.error("",e);
//		}
//		return HttpClients.createDefault();
//	}
//	
//	public static CloseableHttpClient createSSLClientDefault(String host) {
//		try {
//			host = getHost(host);
//			if(host == null) {
//				return createSSLClientDefault();
//			}
//			CloseableHttpClient httpClient = httpsCertHttpClientMap.get(host);
//			if(httpClient != null) {
//				return httpClient;
//			}
//			CertInfo certInfo = getCertInfo(host);
//			if(certInfo != null) {
//				KeyStore keyStore  = KeyStore.getInstance(certInfo.getKeyStoreType());
//		        FileInputStream instream = new FileInputStream(new File(certInfo.getFilePath()));
//		        try {
//		            keyStore.load(instream, certInfo.getPassword().toCharArray());
//		        } finally {
//		            instream.close();
//		        }
//		        // Trust own CA and all self-signed certs
//		        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(keyStore, certInfo.getPassword().toCharArray())
//		                .build();
//		        // Allow TLSv1 protocol only
//		        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//		        		sslContext,
//		                new String[] { "TLSv1" },
//		                null,
//		                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//		        httpClient = HttpClients.custom()
//		        		.setRedirectStrategy(new LaxRedirectStrategy())
//		                .setSSLSocketFactory(sslsf)
//		                .build();
//		        httpsCertHttpClientMap.put(host, httpClient);
//		        return httpClient;
//			} else {
//				httpClient = createSSLClientDefault();
//				httpsCertHttpClientMap.put(host, httpClient);
//				return httpClient;
//			}
//		} catch (Exception e) {
//			LOG.error("",e);
//		}
//		return createSSLClientDefault();
//	}
//	
//	public static final HttpResult get(String url) {
//		return get(url, null, null, null,null);
//	}
//
//	public static final HttpResult get(String url,HttpHost proxy) {
//		return get(url, proxy, null, null,null);
//	}
//	
//	public static final HttpResult getWithParameters(String url, Map<String, String> params) {
//		return get(url, null, null, params,null);
//	}
//	
//	public static final HttpResult getWithHeaders(String url, Map<String, String> headers) {
//		return get(url, null, headers, null,null);
//	}
//
//	public static final HttpResult get(String url,HttpHost proxy, Map<String, String> params) {
//		return get(url, proxy, null, params,null);
//	}
//
//	public static final HttpResult get(String url, Map<String, String> headers,Map<String, String> params) {
//		return get(url, null, headers, params,null);
//	}
//	
//	public static final HttpResult get(String url, Map<String, String> headers,Map<String, String> params,String sendEncoding) {
//		return get(url, null, headers, params,sendEncoding);
//	}
//
//	private static final boolean isEmptyCollection(Map<?, ?> collection) {
//		return (collection == null || collection.isEmpty());
//	}
//
//
//	public static final HttpResult post(String url) {
//		return post(url, null, null);
//	}
////
////	public static final HttpResult post(String url, Map<String, String> headers,String data) {
////		
////	}
//
//	public static final HttpResult postWithParameters(String url, Map<String, String> params) {
//		return post(url, null, params);
//	}
//	
//	public static final HttpResult postWithHeaders(String url, Map<String, String> headers) {
//		return post(url, headers, null);
//	}
//
//	public static final HttpResult post(String url,Map<String, String> headers, Map<String, String> params) {
//		return post(url, null, headers, params);
//	}
//	
//	public static final HttpResult postFile(String url,FileInput ...files) {
//		return post(url, null, null, null,null, files);
//	}
//	
//	public static final HttpResult postFile(String url,Map<String, String> params,FileInput ...files) {
//		return post(url, null, null, params,null, files);
//	}
//	
//	
//	
//	private static final FileInput [] tmpeFiles = new FileInput[0];
//	public static final HttpResult post(String url,HttpHost proxy, Map<String, String> headers,Map<String, String> params) {
//		return post(url, proxy, headers, params,null, tmpeFiles);
//	}
//	
//	private static final String getCharsetEncodingFromEntity(HttpEntity entity,byte [] bytes) {
//		String charset = DEFAULT_CHARSET;
//		//1.try from entity
//		if(entity.getContentEncoding() != null) {
//			charset = entity.getContentEncoding().getValue();
//			if(charset != null && charset.trim().length() > 0) {
//				return charset;
//			}
//		}
//		//2.try from ContentType
//		ContentType contentType = ContentType.get(entity);
//	    if (contentType != null) {
//	    	Charset cs = contentType.getCharset();
//	    	if(cs != null) {
//	    		charset = cs.name();
//	    		if(charset != null && charset.trim().length() > 0) {
//					return charset;
//				}
//	    	}
//	    }
//	    //3.try from html
//		try {
//			String content = new String(bytes,DEFAULT_CHARSET);
//			Document doc = Jsoup.parse(content);
//			
//			Elements elements = doc.getElementsByAttribute("charset");
//			for(Element element:elements) {
//				charset = element.attr("charset");
//				if(charset != null && charset.trim().length() > 0) {
//					return charset;
//				}
//			}
//			
//			elements = doc.getElementsByAttributeValue("http-equiv", "Content-Type");
//			for(Element element:elements) {
//				String contentChar = element.attr("content");
//				if(contentChar != null && contentChar.contains("text/html;")) {
//					charset = contentChar.replace("text/html;", "").trim().replace("charset=", "");
//					if(charset != null && charset.trim().length() > 0) {
//						return charset;
//					}
//				}
//			}
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
////		System.err.println("****************charset="+charset);
//		return charset;
//	}
//
//
//	private static final List<NameValuePair> getNameValuePairsFromMap(
//			Map<String, String> params) {
//		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
//		if (!isEmptyCollection(params)) {
//			for (Entry<String, String> e : params.entrySet()) {
//				pairs.add(new BasicNameValuePair(e.getKey(), e.getValue()));
//			}
//		}
//		return pairs;
//	}
//	
//	private static final Map<String,String> getPramasFromUri(String uri) {
//		Map<String,String> map = new HashMap<String, String>();
//		if(uri == null || uri.trim().length() == 0) {
//			return map;
//		}
//		if(!uri.contains("?")) {
//			return map;
//		}
//		String pv = uri.substring(uri.indexOf("?")+1);
//		String[] kv = pv.split("&");
//		if(kv.length == 0) {
//			return map;
//		}
//		for(String s:kv) {
//			String[] akv = s.split("=");
//			if(akv.length == 2) {
//				map.put(akv[0], akv[1]);
//			}
//		}
//		return map;
//	}
//
//	/**
//	 * 关闭连接
//	 * 
//	 * @param url
//	 */
//	// public static final void closeHttpClient() {
//	// try {
//	// httpClient.close();
//	// } catch (IOException e) {
//	// e.printStackTrace();
//	// }
//	// }
//
//	public static final void closeResponse(CloseableHttpResponse response) {
//		if (response == null) {
//			return;
//		}
//		try {
//			HttpEntity entity = response.getEntity();
//			if (entity != null) {
//				InputStream inputStream = entity.getContent();
//				if (inputStream != null) {
//					inputStream.close();
//				}
//			}
//			response.close();
//		} catch (Exception e) {
//			LOG.error("closeResponse", e);
//		}
//	}
//
//	public static void main(String[] args) throws UnsupportedEncodingException {
////		
////		String url = "http://localhost:5091/search/create";
//////		String url = "http://demo.image.otra.cn/";
////		String stringValue = "request....";
////		HttpResult result = HttpClientUtils.get("http://192.168.11.2:-1/repair/repair_vctw_qry_detail.asp?wrk_no=WV16030007&svc=HNFV");
////		System.err.println(result);
////		HttpGet get = new HttpGet("http://192.168.11.2:-1/repair/repair_vctw_qry_detail.asp?wrk_no=WV16030007&svc=HNFV");
////		setTimeout(get);
//		
////		String host = getHost("http://111.205.191.231:9080/iastat_sz/peruser/queryByFrame.do");
////		System.err.println(host);
//		
//		HttpClientService service = new HttpClientService();
//		HttpResult result = service.get("http://www.baidu.com");
//		System.err.println(result.getData());
//	}
//
//}
