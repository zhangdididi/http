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

        response.setStatusNum(200);
        response.setMessage("ok");
        response.println("正确响应了客户端的信息");
        response.flush();
    }
}
