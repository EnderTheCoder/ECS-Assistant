package ecsassistant.ecsassistant.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import ecsassistant.ecsassistant.database.Mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserConfig {
    public static String getUserConfig(UUID uuid, String tag) {
        String config;

        config = getConfigRaw(uuid);
        ConfigReader configReader = new ConfigReader();



        JSONObject json = JSON.parseObject(config);
        String configValue = json.getString(tag);
        if (configValue == null) {
            putUserConfig(uuid, tag, configReader.getPlayerDefaultConfig(tag));
            return configReader.getPlayerDefaultConfig(tag);
        }
        return configValue;
    }

    public static void putUserConfig(UUID uuid, String tag, String value) {
        Mysql m = new Mysql();
//        String configLatest = getUserConfig(uuid, tag);

        String config;

        config = getConfigRaw(uuid);

        JSONObject json = JSON.parseObject(config);
//        if (configLatest.equals("")) {
//            json.remove(tag);
//        }
        json.put(tag, value);

        config = json.toJSONString();

        m.prepareSql("UPDATE user_config SET config = ? WHERE uuid = ?");
        m.setData(1, config);
        m.setData(2, String.valueOf(uuid));
        m.execute();
    }

    public static String getConfigRaw(UUID uuid) {
        Mysql m = new Mysql();
        String config = "";

        m.prepareSql("SELECT * FROM user_config WHERE uuid = ?");
        m.setData(1, String.valueOf(uuid));
        m.execute();
        ResultSet result = m.getResult();
        try {
            result.next();
            config = result.getString("config");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return config;
    }

}
