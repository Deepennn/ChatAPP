package com.chatapp.client;

import com.chatapp.client.service.Log;
import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;

/**
 * 聊天室客户端
 *
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
