package com.chatapp.server;

import com.chatapp.util.CommonUtils;
import com.chatapp.util.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ChatAPP服务端
 * ServerSocket：服务端Socket
 * Socket（服务端IP，端口号）=> 写在配置文件（socket.properties）中
 */
public class Server {
    private final static int PORT = CommonUtils.getPORT();

    //String为姓名  缓存在线用户  线程安全
    private static final Map<String,Socket> userMap  = new ConcurrentHashMap<>();
    //String为群组名称
    private static final Map<String,Set<String>> groupMap  = new ConcurrentHashMap<>();
    //缓存服务器与客户端的所有输出流
    private static Map<String,PrintStream> printStreamMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            //创建一个ServerSocket实例，在指定的端口上等待客户端连接。
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动，等待客户端连接。。。");
            ExecutorService executorService = Executors.newFixedThreadPool(50); // 最大50个线程的线程池
            for(int i = 0; i<50; i++){ // while (true)
                // accept()会一直阻塞当前线程，直到有客户端发起连接，才返回一个新的与客户端进行通信的Socket对象。
                Socket socket = serverSocket.accept();
                System.out.println("有新的连接，端口号为"+socket.getPort());
                executorService.submit(new Server().new ExecutorServer(socket)); // 将连接的Socket交给线程池的一个线程来处理
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ExecutorServer implements Runnable{
        private final Socket client; // 服务每个用户的服务socket
        // 每个与用户交互的Stream
        private Scanner scanner;//输入流，读数据
        private PrintStream printStream; //输出流，写数据

        public ExecutorServer(Socket client) {
            this.client = client;
            try {
                this.scanner = new Scanner(client.getInputStream());
                this.printStream= new PrintStream(this.client.getOutputStream(),true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (true){
                if(scanner.hasNextLine()){
                    //info为json字符串,是客户端发给服务器的消息
                    String info = scanner.nextLine();
                    MessageVO voFromClient = (MessageVO) CommonUtils.Json2Object(info,MessageVO.class);
                    String type = voFromClient.getType();
                    String content = voFromClient.getContent();
                    String to = voFromClient.getTo();
                    String from = voFromClient.getFrom();
                    if(type.equals("1")){
                        //登录
                        //给用户发送所有在线用户的信息
                        MessageVO voToClient = new MessageVO();
                        voToClient.setType("5");
                        voToClient.setContent(CommonUtils.Object2Json(userMap.keySet()));
                        printStream.println(CommonUtils.Object2Json(voToClient));
                        //将新上线的用户发送给所有的在线的用户
                        MessageVO voLoginMes = new MessageVO();
                        voLoginMes.setType("6");
                        voLoginMes.setContent(content);  //content为myname
                        sendAll(CommonUtils.Object2Json(voLoginMes),content);
                        //缓存服务器到新上线用户的输出流
                        printStreamMap.put(content,printStream);
                        //将用户名存到在线用户缓存中
                        userMap.put(content,client);
                        System.out.println(content+"上线了");
                    }else if(type.equals("2")){
                        //私聊
                        MessageVO voToClient = new MessageVO();
                        voToClient.setFrom(from);
                        voToClient.setTo(to);
                        voToClient.setType("7");
                        voToClient.setContent(content);
                        send(CommonUtils.Object2Json(voToClient),to);
                    }else if(type.equals("3")){
                        //群聊
                        MessageVO voToGroup = new MessageVO();
                        voToGroup.setFrom(from);
                        voToGroup.setType("8");
                        voToGroup.setContent(content);
                        voToGroup.setTo(to);
                        send(CommonUtils.Object2Json(voToGroup),groupMap.get(to));
                    }else if(type.equals("4")){
                        //退出应用
                        MessageVO voExitInfo = new MessageVO();
                        voExitInfo.setType("11");
                        voExitInfo.setFrom(from);
                        userMap.remove(from);
                        send(CommonUtils.Object2Json(voExitInfo),userMap.keySet());
                    }else if(type.equals("10")){
                        //建群（建群时可不用通知其他在群组中的成员，第一次发送消息时再通知其他成员有新的群组成立）
                        //将群加入到服务器中
                        if(groupMap.containsKey(content)){
                            MessageVO voCreateGroup = new MessageVO();
                            voCreateGroup.setType("9");
                            voCreateGroup.setTo(from);
                            voCreateGroup.setFrom("no");
                            send(CommonUtils.Object2Json(voCreateGroup),from);
                        }else{
                            Set<String> userNameSet = (Set<String>)CommonUtils.Json2Object(to,Set.class);
                            groupMap.put(content,userNameSet);
                            MessageVO voCreateGroup = new MessageVO();
                            voCreateGroup.setType("9");
                            voCreateGroup.setContent(content);//群名
                            voCreateGroup.setTo(to);
                            voCreateGroup.setFrom(from);
                            send(CommonUtils.Object2Json(voCreateGroup),userNameSet);
                        }
                    }else if(type.equals("12")){
                        //退出群组
                        Set<String> groupNameSet = (Set<String>)CommonUtils.Json2Object(content,Set.class);
                        for(String groupName : groupNameSet){
                            Set<String> groupMemberSet = groupMap.get(groupName);
                            MessageVO voExitGroup = new MessageVO();
                            voExitGroup.setType("13");
                            voExitGroup.setContent(groupName);//群名
                            voExitGroup.setTo(CommonUtils.Object2Json(groupMemberSet));
                            voExitGroup.setFrom(from);
                            send(CommonUtils.Object2Json(voExitGroup),groupMemberSet);
                            groupMemberSet.remove(from);
                        }
                    }
                }
            }
        }


        //群聊找到要发的客户端，发送
        private void send(String message,Set<String> userSet){
            for(Map.Entry<String,PrintStream> entry : printStreamMap.entrySet()){
                if(userSet.contains(entry.getKey())){
                    entry.getValue().println(message);
                }
            }
        }

        //私聊找到要发的客户端，发送
        private void send(String str,String toFriendName){
            printStreamMap.get(toFriendName).println(str);
        }

        /**
         * 向所有在线用户发送新用户上线信息或者群聊
         * @param message  要发送的数据
         */
        private void sendAll(String message,String name){
            //Set<Map.Entry<K,V>> entrySet();
            for(Map.Entry<String,PrintStream> entry : printStreamMap.entrySet()){
                if(!Objects.equals(entry.getKey(), name)){
                    entry.getValue().println(message);
                }
            }
        }
    }
}
