package com.jinyu.chatserver.service;

import com.jinyu.dbexecute.SQLs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;

public class Groups {
    private SQLs sqLs = new SQLs();
    static HashMap<String, Queue<String>> groups = new HashMap<>();// 利用map存储群组
    // 这里需要数据库存储群组，目前先用map临时模拟存储

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

    public static java.util.List<String> getGroupsForUser(String userId) {
        java.util.List<String> result = new java.util.ArrayList<>();
        for (String groupName : groups.keySet()) {
            Queue<String> members = groups.get(groupName);
            if (members != null && members.contains(userId)) {
                result.add(groupName);
            }
        }
        return result;
    }
}
