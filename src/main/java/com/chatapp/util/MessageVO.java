package com.chatapp.util;

import lombok.Data;

/**
 * 服务器与客户端之间的报文
 */
@Data
public class MessageVO {
    //客户端告知服务器要进行的操作
    // 1： 登陆
    // 2： 私聊
    // 3： 群聊
    // 4： 退出应用
    // 10： 建群
    // 12: 退出群组

    //服务器告知客户端要进行的操作
    // 5:  加载用户列表（Log）
    // 6:  新用户上线信息
    // 7:  接收私聊
    // 8:  接收群聊
    // 9:  将别人创建的群（有自己）加入群聊列表
    // 11: 用户退出信息
    // 13: 用户退出群组信息
    private String type;

    //发送的具体内容
    private String content;

    //发给谁
    private String to;

    //从谁来
    private String from;

}
