package com.chatapp;

import com.chatapp.util.CommonUtils;

/**
 * Hello world!
 *
 */
public class APP
{
    public static void main( String[] args )
    {
        System.out.println(CommonUtils.class.getClassLoader().getResourceAsStream("datasource.properties"));
        System.out.println( "Hello World!" );
    }
}
