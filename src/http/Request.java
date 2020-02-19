package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Request {

    //请求方法：get/post
    private String method;
    //请求地址，对应服务器提供的服务路径
    private String url;
    //http版本号
    private String version;
    //请求头
    private Map<String, String> headers = new HashMap<>();
    //请求参数
    private Map<String, String> parameters = new HashMap<>();

    /**
     * 类似单例的写法，提供私有构造方法
     */
    private Request() {

    }

    /**
     * 通过客户端发送的http请求数据报，转换为Request请求类
     * 包装请求方法、URL、版本号、请求头以及请求参数
     * @param inputStream
     * @return
     */
    public static Request buildRequest(InputStream inputStream) {
        Request request = new Request();

        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            System.out.println("=============开始解析http请求============");
            String requestLine = input.readLine();
            //处理请求行
            request.parseRequestLine(requestLine);
            //处理请求头
            String header;
            System.out.println("请求头：");
            while ((header = input.readLine()) != null && header.length() != 0) {
                String[] parts = header.split(":");//用冒号分隔key和value
                request.headers.put(parts[0].trim(), parts[1].trim());//去除空白字符串
                System.out.printf("%s:%s\n", parts[0].trim(), parts[1].trim());
            }
            //处理请求正文
            //如果是POST方法提交并且有Content-Length，表示请求数据中包含请求体
            //此时需要处理请求体
            if ("POST".equalsIgnoreCase(request.method)
                    && request.headers.containsKey("Content-Length")) {
                int length = Integer.parseInt(request.headers.get("Content-Length"));
                char[] chars = new char[length];
                input.read(chars, 0, length);
                request.parseParameters(new String(chars));
            }
            System.out.print("请求参数：");
            for (Map.Entry<String, String> entry : request.parameters.entrySet()) {
                System.out.printf("%s:%s, ", entry.getKey(), entry.getValue());
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("处理请求数据错误", e);
        }
        return request;
    }

    /**
     *  解析请求行为method、uel、version
     * @param requestLine
     */
    private void parseRequestLine(String requestLine) {
        //解析到this的属性中
        if (requestLine == null) {
            return;
        }
        String[] parts = requestLine.split(" ");
        method = parts[0];
        url = parts[1];
        //如果url包含问号，则表示url中存在请求参数需要处理
        int index = url.indexOf("?");
        if (index != -1) {
            parseParameters(url.substring(index + 1));//问号后是请求参数
            url = url.substring(0, index);//问号前是url
        }
        version = parts[2];
        System.out.printf("请求方法：%s, url：%s, 版本号：%s\n", method, url, version);
    }

    /**
     * 解析请求参数：key:value&key:value& ...
     * @param parameters
     */
    private void parseParameters(String parameters) {
        //获取到每个键值对
        String[] parts = parameters.split("&");
        if (parts != null && parts.length != 0) {
            //part对应key:value
            for (String part : parts) {
                String[] params = part.split("=");
                this.parameters.put(params[0], params[1]);
            }
        }
    }
}
