package com.chatapp.client.entity;

import org.junit.Test;

/**
 * Author:ljl
 * Created:2023/12/19
 */
public class UserTest {

    //测试lombok能否正常使用
    @Test
    public void getUserName() {
        User user = new User();
        user.setUserName("ljl");
        System.out.println(user.getUserName());
    }
}
