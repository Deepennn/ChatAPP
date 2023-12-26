package com.chatapp.client.dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.chatapp.client.entity.User;
import com.chatapp.util.CommonUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 * 数据库操作，将注册的新用户存入数据库，注册时从数据库中查询用户名是否存在
 */
public class AccountDao {
    private static DataSource dataSource;

    static {
        try {
            dataSource = DruidDataSourceFactory.createDataSource
                    (CommonUtils.loadProperties("datasource.properties"));
        } catch (Exception e) {
            System.out.println("数据源加载失败");
            e.printStackTrace();
        }
    }


    private Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("数据库连接获取失败");
            e.printStackTrace();
        }
        return null;
    }

    //登陆（在这里等价注册）时插入用户
    public boolean reg(User user)  {
        System.out.println(user.toString());
        Connection connection = this.getConnection();
        String sql = "INSERT INTO USER (userName) values(?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,user.getUserName());
            int res = statement.executeUpdate();
            if(res==1){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("注册失败");
            e.printStackTrace();

        }finally {
            closeResources(connection,statement);
        }
        return false;
    }

    //登陆（在这里等价注册）——新用户插入数据库
    private void closeResources(Connection connection,Statement statement) {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
