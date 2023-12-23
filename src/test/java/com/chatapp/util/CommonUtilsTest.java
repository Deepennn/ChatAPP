package com.chatapp.util;

import com.chatapp.client.entity.User;
import org.junit.Test;

import java.util.Properties;

/**
 * Author:ljl
 * Created:2023/12/19
 */
public class CommonUtilsTest {
    @Test
    public void loadProperties() {
        Properties properties = CommonUtils.loadProperties("datasource.properties");
        System.out.println(properties);
    }


    @Test
    public void object2Json() {
        User user = new User();
        user.setUserName("ljl");
        String  str = CommonUtils.Object2Json(user);
        System.out.println(str);
    }

    @Test
    public void json2Object() {
        String jsonStr = "{\"userName\":\"ljl\"}";
        User user = (User) CommonUtils.Json2Object(jsonStr,User.class);
        System.out.println(user);
    }
}
