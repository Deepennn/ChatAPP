package com.chatapp.client.service;

import com.chatapp.client.dao.AccountDao;
import com.chatapp.client.entity.User;
import com.chatapp.util.CommonUtils;
import com.chatapp.util.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Set;

/**
 * 登录
 */
public class Log {
    private JPanel login;
    private JButton logBut;
    private JTextField userNameText;
    private JLabel usernameLable;
    private JFrame frame;

    private AccountDao accountDao = new AccountDao();

    public Log() {

        frame = new JFrame("用户登陆");  //  JFrame是Java中的一个类，是一个容器
        frame.setContentPane(login);    //设置contentPane属性
        //EXIT_ON_CLOSE：退出应用程序默认窗口关闭操作
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //设置用户在此窗体上发起的close时，默认执行的操作
        frame.setLocationRelativeTo(null);
        frame.pack();//调整窗口的大小，以适合其子组件的首选大小和布局，单独使用size（）时。不能用pack
        frame.setVisible(true); //如果窗口是第一次显示，将事先初始化窗口显示的相关资源再显示
        //frame.setVisible(false);将窗口隐藏，但窗口资源还在
        //frame.dispose();将资源销毁
        //frame.close();判断是否已经dispose(),如果没有，则dispose()

        //登录事件
        logBut.addActionListener(new ActionListener() {   //事件源注册监听器
            @Override
            public void actionPerformed(ActionEvent e) { //触发事件要做的事情

                //得到注册的用户信息
                String name = userNameText.getText();

                if(register(name)){
                    //登录成功，建立连接
                    Connect2Server connect2Server = new Connect2Server();
                    // 客户端将自己的信息发送到服务端，用MeaasgeVO发送
                    MessageVO VOtoServer = new MessageVO();
                    VOtoServer.setType("1"); //1为登录
                    VOtoServer.setContent(name);
                    //将信息序列化为json字符串，发给服务器
                    String jsonToServer = CommonUtils.Object2Json(VOtoServer);
                    //得到输出流，发送数据
                    PrintStream printStream = connect2Server.getPrintStream();
                    printStream.println(jsonToServer);

                    //接受信息
                    Scanner scanner = connect2Server.getScanner();
                    //接受在线用户列表
                    if(scanner.hasNextLine()){  //接收type是5的信息
                        //info为json字符串，是接受服务端发过来的用户上线信息
                        String info = scanner.nextLine();
                        MessageVO VOfromServer = (MessageVO) CommonUtils.Json2Object(info,MessageVO.class);
                        String content = VOfromServer.getContent();
                        Set<String> userSet = (Set<String>) CommonUtils.Json2Object(content,Set.class);
                        System.out.println("所有在线用户为："+userSet);

                        //加载用户列表
                        //将当前用户名，所有在线用户，与服务器建立的建立的连接传递到用户列表中去
                        new UserList(name,userSet,connect2Server,frame);

                    }
                }else{
                    // 失败，停留在当前登录页面，提示用户信息错误
                    // showMessageDialog（jPanel，"提示消息"，"标题"，图标（警告⚠，错误❌，问题，信息等））
                    JOptionPane.showMessageDialog(frame,
                            "登录失败!","错误信息",
                            JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    public boolean register(String name){

        //将信息存入User
        User user = new User();
        user.setUserName(name);
        //将user存入数据库
        if(accountDao.reg(user)){
            JOptionPane.showMessageDialog(frame,"注册成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
            return true;
        }else{
            try {
                throw new Exception("注册失败");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            // 失败，停留在当前注册页面，提示用户信息错误，再来一次，或者退出
            // showMessageDialog（jPanel，"提示消息"，"标题"，图标（警告⚠，错误❌，问题，信息等））
            JOptionPane.showMessageDialog(frame, "注册失败!（可能是用户名重复）","错误信息", JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }
}
