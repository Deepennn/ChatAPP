package com.chatapp.client.service;

import com.chatapp.util.CommonUtils;
import com.chatapp.util.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.*;

/**
 * 退出群组
 */
public class ExitGroup {
    private JPanel exitGroup;
    private JPanel groupPan;
    private JButton okBut;
    private JScrollPane checkPan;
    private JPanel newCheckBox;
    private  JFrame frame;

    private Connect2Server connect2Server;
    private PrintStream printStream;
    private String myName;
    private Set<String> selectGroupSet = new HashSet<>();
    private UserList userList;

    //群名以及群成员 String为群名
    private Map<String,Set<String>> groupMap;

    public ExitGroup(String name, Map<String,Set<String>> groupMap, Connect2Server connect2Server, UserList userList) {
        this.groupMap = groupMap;
        this.myName = name;
        this.connect2Server = connect2Server;
        this.printStream = connect2Server.getPrintStream();
        this.userList = userList;

        frame = new JFrame("退出群组");
        frame.setContentPane(exitGroup);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,500);
        frame.setVisible(true);

        //加载要选择的群组
        newCheckBox.setLayout(new BoxLayout(newCheckBox,BoxLayout.Y_AXIS));
        JCheckBox[] jCheckBoxes = new JCheckBox[groupMap.size()];
        Iterator<String> iterator = groupMap.keySet().iterator();
        int i = 0;
        while(iterator.hasNext()){
            String groupName = iterator.next();
            jCheckBoxes[i] = new JCheckBox(groupName);
            newCheckBox.add(jCheckBoxes[i]);
            i++;
        }
        checkPan.setViewportView(newCheckBox);
        checkPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        newCheckBox.revalidate();
        newCheckBox.revalidate();

        okBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断要退出那些群组
                Component[] components = newCheckBox.getComponents();
                for(Component component :components){
                    JCheckBox checkBox = (JCheckBox) component;
                    if(checkBox.isSelected()){
                        selectGroupSet.add(checkBox.getText());
                    }
                }
                //将信息发送到服务端保存
                MessageVO voCheckInfo = new MessageVO();
                voCheckInfo.setType("12");
                voCheckInfo.setFrom(myName);
                voCheckInfo.setContent(CommonUtils.Object2Json(selectGroupSet));
                printStream.println(CommonUtils.Object2Json(voCheckInfo));
                frame.dispose();
                //刷新群列表
                userList.groupLoad();
            }
        });
    }
}
