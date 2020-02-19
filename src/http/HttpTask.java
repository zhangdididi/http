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
            //1. url为根路径
            if ("/".equals(request.getUrl())) {
                response.build200();
                response.println("<h2>Http服务器首页</h2>");
                return;
            }
            //2. 根据url在WebApp文件夹下去找是否存在资源，存在就返回资源

            //改造：读取项目中的Login.html文件内容，并返回给客户端
            //相对路径读取
            //(1). html文件所在的WebApp需要设置为resource资源文件夹
            // -->将WebApp中所有文件复制到编译后的输出文件夹
            //(2). 通过getClassLoader().getResourceAsStream()获取文件的输入流
            InputStream is = this.getClass().getClassLoader().
                    getResourceAsStream("." + request.getUrl());//http url中
            //存在就返回资源
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String content;
                while ((content = br.readLine()) != null) {
                    response.println(content);
                }
                response.build200();
            } else if ("/login".equals(request.getUrl())) {
                //1. 只接受post方法，否则返回405
                if (!"post".equalsIgnoreCase(request.getMethod())) {
                    response.build405();
                    response.println("不支持的请求方法：" + request.getMethod());
                } else {
                    //2. 校验用户名密码，校验通过返回（省略校验过程）
                    response.build200();
                    response.println("请求的数据：username=" + request.getParameter("username")
                                        + ", password=" + request.getParameter("password"));
                }
            } else {
                //以上路径找不到，说明服务器不提供这个url服务，返回404
                response.build404();
                response.println("找不到资源");
            }
            response.flush();
        } catch (IOException e) {
            //捕获所有异常，表示服务器异常，返回500
            e.printStackTrace();
            response.build500();
            System.out.println("服务器出错");
        } finally {
            //始终需要刷新相应数据给客户端
            response.flush();
        }
    }
}
