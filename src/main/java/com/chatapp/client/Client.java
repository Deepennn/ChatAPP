package com.chatapp.client;

import com.chatapp.client.service.Log;
import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;

/**
 * ChatAPP客户端
 * 客户端Socket在Connect2Server
 */
public class Client {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (Exception e) {
            return;
        }
        new Log();
    }
}
