package http;

import java.io.IOException;
import java.net.Socket;

public class HttpTask implements Runnable {

    private Response response;

    private Request request;
//    private Socket socket;

    public HttpTask(Socket socket) {
//        this.socket = socket;
        try {
            //通过客户端发送报头的输入流（请求数据）
            //来创建http请求对象
            request = Request.buildRequest(socket.getInputStream());
            response = Response.buildResponse(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("客户端连接的IO流错误", e);
        }
    }

    @Override
    public void run() {

        try {
            //1. 根据解析出的request对象中属性，来进行逻辑处理
            //2. 在不同逻辑中将要返回的数据设置到response对象中
            //3. 刷新响应信息，返回给客户端

            //调整业务逻辑
            //1. 根据url在WebApp文件夹下去找是否存在资源，存在就返回资源

            response.setStatusNum(200);
            response.setMessage("ok");
            //改造：读取项目中的Login.html文件内容，并返回给客户端
            //1. html文件所在的WebApp需要设置为resource资源文件夹
            // -->将WebApp中所有文件复制到编译后的输出文件夹
            //2. 通过getClassLoader().getResourceAsStream()获取文件的输入流
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("login.html");
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String content;
            while ((content = br.readLine()) != null) {
                response.println(content);
            }
            response.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
