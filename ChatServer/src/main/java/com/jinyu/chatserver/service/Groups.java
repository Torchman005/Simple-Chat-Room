package com.jinyu.chatserver.service;

import com.jinyu.dbexecute.SQLs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

public class Groups {
    SQLs sqLs = new SQLs();

    public void addGroup(String groupName) {
        boolean jubge = sqLs.buildGroup(groupName);
        if (jubge){
            System.out.println("群聊" + groupName + "创建成功");
        } else {
            System.out.println("群聊" + groupName + "创建失败");
        }
    }

    public Queue<String> getGroup(String groupName) {
        return sqLs.getGroupMembers(groupName);
    }

    public boolean hasGroup(String groupName) {
        return sqLs.isGroup(groupName);
    }

    public java.util.List<String> getGroupsForUser(String userId) {
        java.util.List<String> result = new java.util.ArrayList<>();

        for (String groupName : sqLs.getGroupNames()) {
            Queue<String> members = sqLs.getGroupMembers(groupName);
            if (members != null && members.contains(userId)) {
                result.add(groupName);
            }
        }
        return result;
    }
}
