package com.kct.bluetooth.utils;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/3/21
 * 描述: ${VERSION}
 * 修订历史：
 */

public class HTTPUtil {

    /**
     * POST方法提交HTTP请求，返回请求的结果
     *
     * @param url
     * @param params
     * @return 请求结果
     * @throws IOException
     */
    public static String sendPost(String url, String params) throws IOException {
        StringBuffer result = new StringBuffer();

        // 创建URL对象
        URL _url = new URL(url);
        // 创建HTTP连接
        /**
         * 使用.openConnection()方法实例化一个URLConnection对象
         * */
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();

        // 以下设置网络连接的相关参数
        /* 使用POST方法进行请求传递时，必须定义setDoInput和setDoOutput方法 */
        // 设置输入可用
        conn.setDoInput(true);
        // 设置输出可用
        conn.setDoOutput(true);

        // 设置不使用缓存
        conn.setUseCaches(false);
        // 设置连接超时的时间 - 5s
        conn.setConnectTimeout(5000);
        // 设置读取超时的时间 - 5s
        conn.setReadTimeout(5000);
        // 设置HTTP请求的方法 - POST
        conn.setRequestMethod("POST");
        // 设置HTTP请求属性 - 连接方式：保持
        conn.setRequestProperty("Connection", "Keep-Alive");
        // 设置HTTP请求属性 - 字符集：UTF-8
        conn.setRequestProperty("Charset", "UTF-8");
        // 设置HTTP请求属性 - 传输内容的类型 - 简单表单
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        // 设置HTTP请求属性 - 传输内容的长度
        conn.setRequestProperty("Content-Length",
                String.valueOf(params.length()));
        // 设置HTTP请求属性 - 用户代理
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
        // 发送参数 ，采用字符流发送数据
        PrintWriter pw = new PrintWriter(conn.getOutputStream());
        pw.write(params);
        pw.flush();
        pw.close();
        // 获取返回的结果
        if (200 == conn.getResponseCode()) {// 判断状态码
            // 读取服务器返回的 结果 - 字符流
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            // 每次读取一行
            String line;
            while((line = br.readLine()) != null){
                result.append(line);
            }
        }
        // 关闭HTTP连接
        conn.disconnect();
        return result.toString();
    }

    /**
     * GET方法提交HTTP请求，返回请求的结果
     * @param url
     * @return 请求的结果
     * @throws IOException
     */
    public static String sendGet(String url) throws IOException {

        StringBuffer result = new StringBuffer();
        // 创建URL对象
        URL _url = new URL(url);
        // 创建HTTP连接
        HttpURLConnection conn = (HttpURLConnection) _url.openConnection();
        // 设置网络连接的相关参数
        // 设置输入可用
        conn.setDoInput(true);
        // 设置输出可用
        conn.setDoOutput(true);
        // 设置不使用缓存
        conn.setUseCaches(false);
        // 设置连接超时的时间 - 5s
        conn.setConnectTimeout(5000);
        // 设置读取超时的时间 - 5s
        conn.setReadTimeout(5000);
        // 设置HTTP请求的方法 - GET
        conn.setRequestMethod("GET");
        // 设置HTTP请求属性 - 连接方式：保持
        conn.setRequestProperty("Connection", "Keep-Alive");
        // 设置HTTP请求属性 - 字符集：UTF-8
        conn.setRequestProperty("Charset", "UTF-8");
        // 获取返回的结果
        if (200 == conn.getResponseCode()) {// 判断状态码
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            // 每次读取一行
            String line;
            while((line = br.readLine()) != null){
                result.append(line);
            }
        }
        // 关闭HTTP连接
        conn.disconnect();
        return result.toString();
    }


    public static byte[] downFile(final String path) throws IOException {
        byte[] fileData = null;
        URL url;
        HttpURLConnection connection;
        try {
            //统一资源
            url = new URL(path);
            //打开链接
            connection = (HttpURLConnection) url.openConnection();
            //设置链接超时
            connection.setConnectTimeout(4000);
            //设置允许得到服务器的输入流,默认为true可以不用设置
            connection.setDoInput(true);
            //设置允许向服务器写入数据，一般get方法不会设置，大多用在post方法，默认为false
            //connection.setDoOutput(true);//此处只是为了方法说明
            //设置请求方法
            connection.setRequestMethod("GET");
            // 设置HTTP请求属性 - 连接方式：保持
            connection.setRequestProperty("Connection", "Keep-Alive");
            //设置请求的字符编码
            connection.setRequestProperty("Charset", "utf-8");
            //得到链接的响应码 200为成功
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //得到服务器响应的输入流
                InputStream inputStream = connection.getInputStream();
                //创建缓冲输入流对象，相对于inputStream效率要高一些
                BufferedInputStream bfi = new BufferedInputStream(inputStream);
                ByteArrayOutputStream bfo = new ByteArrayOutputStream();
                //此处的len表示每次循环读取的内容长度
                int len;
                //已经读取的总长度
                int totle = 0;
                //bytes是用于存储每次读取出来的内容
                byte[] bytes = new byte[1024];
                while ((len = bfi.read(bytes)) != -1) {
                    //每次读取完了都将len累加在totle里
                    totle += len;
                    //通过文件输出流写入从服务器中读取的数据
                    bfo.write(bytes, 0, len);
                }
                //关闭打开的流对象
                fileData = bfo.toByteArray();
                bfo.close();
                inputStream.close();
                bfi.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileData;
    }
}
