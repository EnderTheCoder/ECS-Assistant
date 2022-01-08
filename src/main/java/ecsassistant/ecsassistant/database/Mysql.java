package ecsassistant.ecsassistant.database;

import ecsassistant.ecsassistant.config.ConfigReader;
import org.bukkit.ChatColor;

import java.sql.*;

import static org.bukkit.Bukkit.getLogger;

public class Mysql{


    private static Connection connection;
    private PreparedStatement statement;

    public boolean mysqlInit() {

        ConfigReader config = new ConfigReader();

        String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";


        // 连接参数的固定格式
        String DB_URL =
                "jdbc:mysql://" + config.getMysqlConfig("Mysql.Address") + ":" +
                        config.getMysqlConfig("Mysql.Port") +
                        "/" +
                        config.getMysqlConfig("Mysql.DataBaseName") +
                        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            // 驱动名称
            Class.forName(JDBC_DRIVER); // forName 又来了！
            connection = DriverManager.getConnection(DB_URL, config.getMysqlConfig("Mysql.Username"), config.getMysqlConfig("Mysql.password"));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public Boolean prepareSql(String sql) {
        try {
            statement = connection.prepareStatement(sql);
            return true;
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while preparing sql.");
            e.printStackTrace();
            return false;
        }
    }

    public Boolean setData(Integer number,String data) {
        try {
            statement.setString(number, data);
            return true;
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while binding data for sql.");
            e.printStackTrace();
            return false;
        }
    }

    public Boolean execute() {
        try {
            statement.execute();
            return true;
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while executing sql.");
            e.printStackTrace();
            return false;
        }
    }

    public ResultSet getResult() {
        try {
            return statement.executeQuery();
        } catch (SQLException e) {
            getLogger().warning(ChatColor.RED + "An error in mysql occurred while getting sql result.");
            e.printStackTrace();
            return null;
        }
    }
}

