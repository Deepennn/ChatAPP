package com.chatapp.client.service;

import com.chatapp.util.CommonUtils;
import com.chatapp.util.MessageVO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 建群
 */
public class CreateGroup {
    private JPanel createGroup;
    private JPanel userPan;
    private JTextField groupNameField;
    private JButton okBut;
    private JScrollPane checkPan;
    private JPanel newCheckBox;
    private  JFrame frame;

    private Set<String> userSet;
    private Connect2Server connect2Server;
    private PrintStream printStream;
    private String myName;
    private Set<String> selectPersonSet = new HashSet<>();
    private UserList userList;

    public CreateGroup(String name, Set<String> userSet, Connect2Server connect2Server, UserList userList) {
        this.userSet = userSet;
        this.myName = name;
        this.connect2Server = connect2Server;
        this.printStream = connect2Server.getPrintStream();
        this.userList = userList;

        frame = new JFrame("创建群组");
        frame.setContentPane(createGroup);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000,500);
        frame.setVisible(true);

        //加载要选择的联系人
        newCheckBox.setLayout(new BoxLayout(newCheckBox,BoxLayout.Y_AXIS));
        JCheckBox[] jCheckBoxes = new JCheckBox[userSet.size()];
        Iterator<String> iterator = userSet.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String userName = iterator.next();
            if(userName!=myName){
                jCheckBoxes[i] = new JCheckBox(userName);
                newCheckBox.add(jCheckBoxes[i]);
                i++;
            }
        }
        checkPan.setViewportView(newCheckBox);
        checkPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        newCheckBox.revalidate();
        newCheckBox.revalidate();

        okBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //判断那些用户加入了群聊
                Component[] components = newCheckBox.getComponents();
                for(Component component :components){
                    JCheckBox checkBox = (JCheckBox) component;
                    if(checkBox.isSelected()){
                        selectPersonSet.add(checkBox.getText());
                    }
                }
                selectPersonSet.add(myName);
                //将信息发送到服务端保存
                String groupName = groupNameField.getText();
                MessageVO voCheckInfo = new MessageVO();
                voCheckInfo.setTo(CommonUtils.Object2Json(selectPersonSet));
                voCheckInfo.setType("10");
                voCheckInfo.setFrom(myName);
                voCheckInfo.setContent(groupName);
                printStream.println(CommonUtils.Object2Json(voCheckInfo));
                frame.dispose();
                //刷新群列表
                userList.groupLoad();
            }
        });
    }
}
