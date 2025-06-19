package com.jinyu.dbexecute;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        SQLTest sqlTest = new SQLTest();
        try {
            sqlTest.Test();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
