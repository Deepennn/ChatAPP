package com.chatapp;

import com.chatapp.util.CommonUtils;

/**
 * 聊天服务
 */
public class APP
{
    public static void main( String[] args )
    {
        System.out.println(CommonUtils.class.getClassLoader().getResourceAsStream("datasource.properties"));
        System.out.println(CommonUtils.class.getClassLoader().getResourceAsStream("socket.properties"));
    }
}
