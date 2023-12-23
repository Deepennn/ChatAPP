package com.chatapp.client.dao;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.chatapp.client.entity.User;
import com.chatapp.util.CommonUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Author:ljl
 * Created:2023/12/19
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

    //注册时插入用户
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


//    //登陆
//    public User login(String name){
//        String sql = "SELECT * FROM user WHERE userName = ?";
//        Connection connection = this.getConnection();
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        try {
//            statement = connection.prepareStatement(sql);
//            statement.setString(1,name);
//
//            resultSet = statement.executeQuery();
//            if(resultSet.next()){
//                User user = getUser(resultSet);
//                return user;
//            }
//        } catch (SQLException e) {
//            System.out.println("登陆失败");
//            e.printStackTrace();
//        }finally {
//            closeResources(connection,statement,resultSet);
//        }
//        return null;
//    }
//
//    private User getUser(ResultSet resultSet) {
//        User user = new User();
//        try {
//            user.setUserName(resultSet.getString("userName"));
//            return user;
//        } catch (SQLException e) {
//            System.out.println("用户获取失败");
//            e.printStackTrace();
//        }
//        return null;
//    }

    //注册用户——新用户插入数据库
    private void closeResources(Connection connection,Statement statement) {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //登陆-获取用户信息结果集
    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeResources(connection,statement);
    }

}
