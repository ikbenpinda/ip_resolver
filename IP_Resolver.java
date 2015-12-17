import java.sql.*;
import java.util.Properties;

/**
 * IP address lookup class so no manual entering is needed.
 * todo: preparedstatements, stored procedures, integration tests, finalizers, default search locations.
 *
 * Created by Etienne on 14-12-2015.
 */
public class IP_Resolver {

    private Properties properties;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    /**
     *
     * @param properties
     */
    public IP_Resolver(Properties properties){
        this.properties = properties;
        this.host = properties.getProperty("host");
        this.port = Integer.parseInt(properties.getProperty("port"));
        this.database = properties.getProperty("database");
        this.username = properties.getProperty("username");
        this.password = properties.getProperty("password");
    }

    /**
     *
     * @param host
     * @param port
     * @param database
     * @param username
     * @param password
     */
    public IP_Resolver(String host, int port, String database, String username, String password){
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the last known ip address with given key.
     */
    public String get_ip(String name){
        return executeQuery("SELECT address FROM ip WHERE name=\"" + name + "\";", false);
    }

    /**
     * Returns the last known ip address with given key.
     */
    public String get_ip(int id){
        return executeQuery("SELECT address FROM ip WHERE id=\"" + id + "\";", false);
    }

    /**
     * Sets the ip address with given key.
     * Updates if record already exists.
     * @param address the new ip address as a string.
     */
    public void set_ip(String name, String address){
        String query =
                "INSERT INTO ip (name, address) VALUES(\"" + name + "\", \"" + address + "\") " +
                        "ON DUPLICATE KEY UPDATE address=\"" + address + "\"";
        executeQuery(query, true);
    }

    /**
     * Boilerplate database method.
     */
    private String executeQuery(String query, boolean isupdate){
        Connection connection = null;
        ResultSet result = null;
        String address = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mysql://"
                            + host
                            + ":"
                            + port
                            + "/"
                            + database,
                    username,
                    password);
            connection.setAutoCommit(true);

            if (isupdate) {
                connection.createStatement().executeUpdate(query);
            } else{
                result = connection.createStatement().executeQuery(query);
                if (result.next())
                    address = result.getString(1);
            }
        } catch (ClassNotFoundException e) {
            //handleException(e, "Class not found. Driver missing?");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            connection = null;
            //handleException(e, "Connection failed.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return address;
        }

    }
}
