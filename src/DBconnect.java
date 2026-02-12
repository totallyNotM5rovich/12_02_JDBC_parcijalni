import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javax.sql.DataSource;

public class DBconnect {
    public static DataSource createDataSource() {
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setDatabaseName("JavaAdv");
        ds.setUser("sa");
        ds.setPassword("SQL");
        ds.setEncrypt(false);

        return ds;
    }
}
