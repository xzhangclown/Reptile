package so.sao.crawler;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author guangpu.yan
 * @create 2017-10-02 23:58
 **/
public class DB {
    public static List<Map<String,String>> getData(int begin, int size){
        List<Map<String,String>> result = new ArrayList<>();
        List<String>l = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://10.100.50.55:3306/ty_supplier","root","123456");
            ps = conn.prepareStatement("select id, name from commodity limit "+begin+","+size);
            rs = ps.executeQuery();
            while (rs.next()){
                Map<String,String> m = new HashMap<>();
                m.put("id",rs.getString(1));
                m.put("name",rs.getString(2).replaceAll("[#|&|?| |　]",""));
                if(!l.contains(m.get("name").replaceAll("[#|&|?| |　]","")))
                result.add(m);
                l.add(rs.getString(2).replaceAll("[#|&|?| |　]",""));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(rs!=null){
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static void addData(Map m){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://10.100.50.55:3306/test?useUnicode=true&characterEncoding=UTF-8","root","123456");
            ps = conn.prepareStatement("insert into yhd (one,two,three,four,five) values('"+m.get("one")+"','"+m.get("two")+"','"+m.get("three")+"','"+m.get("four")+"','"+m.get("five")+"')");
            ps.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {

            if(ps!=null){
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
