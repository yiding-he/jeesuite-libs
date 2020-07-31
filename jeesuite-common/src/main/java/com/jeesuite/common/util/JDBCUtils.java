package com.jeesuite.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtils {

    public static String driverClass;

    public static String url;

    public static String userName;

    public static String password;

    static {
        driverClass = ResourceUtils.getAndValidateProperty("jdbc.driverClass");
        url = ResourceUtils.getAndValidateProperty("jdbc.url");
        userName = ResourceUtils.getAndValidateProperty("jdbc.userName");
        password = ResourceUtils.getAndValidateProperty("jdbc.password");
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private JDBCUtils() {

    }

    /**
     * Get connection
     */
    public static Connection getconnnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(url, userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Close connection
     */
    public static void close(ResultSet rs, Statement st, Connection con) {
        try {
            try {
                if (rs != null) {
                    rs.close();
                }
            } finally {
                try {
                    if (st != null) {
                        st.close();
                    }
                } finally {
                    if (con != null)
                        con.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection
     */
    public static void close(ResultSet rs) {
        Statement st = null;
        Connection con = null;
        try {
            try {
                if (rs != null) {
                    st = rs.getStatement();
                    rs.close();
                }
            } finally {
                try {
                    if (st != null) {
                        con = st.getConnection();
                        st.close();
                    }
                } finally {
                    if (con != null) {
                        con.close();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close connection
     */
    public static void close(Statement st, Connection con) {
        try {
            try {
                if (st != null) {
                    st.close();
                }
            } finally {
                if (con != null)
                    con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * insert/update/delete
     */
    public static int update(String sql, Object... args) {
        int result = 0;
        Connection con = getconnnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject((i + 1), args[i]);
                }
            }
            result = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(ps, con);
        }

        return result;
    }

    /**
     * query, because need to manually close the resource, so not recommended
     * for use it
     *
     * @return ResultSet
     */
    @Deprecated
    public static ResultSet query(String sql, Object... args) {
        ResultSet result = null;
        Connection con = getconnnection();
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject((i + 1), args[i]);
                }
            }
            result = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Query a single record
     *
     * @return Map<String, Object>
     */
    public static Map<String, Object> queryForMap(String sql, Object... args) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<Map<String, Object>> list = queryForList(sql, args);
        if (list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Query a single record
     *
     * @return <T>
     */
    public static <T> T queryForObject(String sql, Class<T> clz, Object... args) {
        T result = null;
        List<T> list = queryForList(sql, clz, args);
        if (list.size() > 0) {
            result = list.get(0);
        }
        return result;
    }

    /**
     * Query a single record
     *
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> queryForList(String sql, Object... args) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            con = getconnnection();
            ps = con.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject((i + 1), args[i]);
                }
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    map.put(rsmd.getColumnLabel(i), rs.getObject(i));
                }
                result.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, con);
        }
        return result;
    }

    /**
     * Query a single record
     *
     * @return List<T>
     */
    public static <T> List<T> queryForList(String sql, Class<T> clz, Object... args) {
        List<T> result = new ArrayList<T>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getconnnection();
            ps = con.prepareStatement(sql);
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject((i + 1), args[i]);
                }
            }
            rs = ps.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (rs.next()) {
                T obj = clz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    String methodName = "set" + columnName.substring(0, 1).toUpperCase()
                            + columnName.substring(1, columnName.length());
                    Method method[] = clz.getMethods();
                    for (Method meth : method) {
                        if (methodName.equals(meth.getName())) {
                            meth.invoke(obj, rs.getObject(i));
                        }
                    }
                }
                result.add(obj);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            close(rs, ps, con);
        }
        return result;
    }
}