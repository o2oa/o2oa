package com.x.base.core.project.tools;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sword
 * @date 2023/03/29 16:22
 **/
public class SSLHttpClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSLHttpClientUtil.class);

    /** keystore类型JKS*/
    private static final String KEY_STORE_TYPE_JKS = "JKS";
    private CloseableHttpClient httpClient;
    /**
     * @throws Exception
     */
    public SSLHttpClientUtil(String password, String p12FilePath) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
        KeyStore trustKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream instream = new FileInputStream(p12FilePath);

        try {
            //密钥库口令
            keyStore.load(instream, password.toCharArray());
        } catch (CertificateException e) {
            logger.warn("加载客户端端可信任证书出错了");
            logger.error(e);
        } finally {
            try {
                instream.close();
            } catch (Exception ignore) {
            }
        }
        SSLContext sslcontext = SSLContexts.custom()
                //忽略掉对服务器端证书的校验
                .loadTrustMaterial(new TrustStrategy() {
                    @Override
                    public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }

                })
                .loadKeyMaterial(keyStore, password.toCharArray())
                .build();

        SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1.1"},
                null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        this.httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param map
     * @throws Exception
     */
    public String post(String url, Map<String, Object> map) throws Exception {
        // 声明POST请求
        HttpPost httpPost = new HttpPost(url);
        // 判断map不为空
        if (null != map) {
            // 声明存放参数的List集合
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // 遍历map，设置参数到list中
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 创建form表单对象
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "utf-8");
            formEntity.setContentType("Content-Type:application/json");
            // 把表单对象设置到httpPost中
            httpPost.setEntity(formEntity);
        }
        // 使用HttpClient发起请求，返回response
        CloseableHttpResponse response = this.httpClient.execute(httpPost);
        // 获取实体
        HttpEntity entity = response.getEntity();
        // 将实体装成字符串
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        return res;
    }


    /**
     * 发送POST请求
     *
     * @param url
     * @param map
     * @return
     * @throws Exception
     */
    public String get(String url, Map<String, Object> map,Map<String,String> headers) throws Exception {
        String params = null;
        if (null != map) {
            // 声明存放参数的List集合
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            // 遍历map，设置参数到list中
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            // 转化参数
            params = EntityUtils.toString(new UrlEncodedFormEntity(list, "utf-8"));
        }
        url += StringUtils.isNotBlank(params) ? ("?" + params) : "";
        HttpGet httpGet = new HttpGet(url);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entity : headers.entrySet()) {
                httpGet.setHeader(entity.getKey(), entity.getValue());
            }
        }
        // 使用HttpClient发起请求，返回response
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 获取实体
        HttpEntity entity = response.getEntity();
        // 将实体装成字符串
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        response.close();
        return res;
    }

    /**
     * 发送POST请求（JSON参数）
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String post(String url, String json,Map<String,String> headers) throws IOException {
        // 声明POST请求
        HttpPost httpPost = new HttpPost(url);
        // 表示客户端发送给服务器端的数据格式
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Accept", "application/json");
        StringEntity param = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(param);
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entity : headers.entrySet()) {
                httpPost.setHeader(entity.getKey(), entity.getValue());
            }
        }
        CloseableHttpResponse resp = this.httpClient.execute(httpPost);
        HttpEntity entity = resp.getEntity();
        // 将实体装成字符串
        String res = EntityUtils.toString(entity, Charset.defaultCharset());
        EntityUtils.consume(entity);
        resp.close();
        logger.info("post-- json:{} || res:{} ",json,res);
        return res;
    }
}
