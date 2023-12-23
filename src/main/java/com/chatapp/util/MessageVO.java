package com.chatapp.util;

import lombok.Data;

/**
 * Author:ljl
 * Created:2023/12/19
 * 服务器与客户端传递信息载体
 */
@Data
public class MessageVO {
    //告知服务器要进行的操作 1：登陆   2：私聊  3：建群  4：群聊  5：退出
    private String type;

    //发送到服务器的具体内容
    private String content;

    //发给谁
    private String to;

    //从谁来
    private String from;

}
