package com.example.jzhou.bicycler;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by jzhou on 17/10/2015.
 */
public class PostgreSqlCon {
    static Connection conn = null;
    static Statement state = null;
    static PreparedStatement pstate;
    static String dirve="org.postgresql.Driver";
    static String url ;
    ResultSet rs=null;

    public void connection(String sql) throws SQLException, ClassNotFoundException {
        url="jdbc:postgresql://pilot1.cloudapp.net:5432/pilot?user=bicycler&password=bicycler_pilot";
        if(conn==null||conn.isClosed())
        {
            Class.forName(dirve);
            conn = DriverManager.getConnection(url);
        }
        if (conn == null)
        {
            Log.d("conn", "hahahahahah");
        }

        query(sql);
    }


    public void query(String sql) throws SQLException {
        pstate = conn.prepareStatement(sql);
        rs=pstate.executeQuery();
        if (pstate!=null){
            System.out.print("pstate is not null");
        }
        if (rs!=null){
            System.out.print("rs is not empty");
        }
    }

    void close(){
        try {
            if (conn != null)
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            pstate.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

