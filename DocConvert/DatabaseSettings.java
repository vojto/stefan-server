package DocConvert;


/**
 * Class describing database settings.
 * <img src="Stefan.jpg" align=right >
 */
public class DatabaseSettings {
    /**
     * Database to connect
     */
    public static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    /**
     * Database URL
     */
    public static final String URL = "";
    /**
     * user name
     */
    public static final String USER = "";
    /**
     * user password
     */
    public static final String PASSWORD = "";
    

    /**
     * create table command
     */
    public static final String QUERY_CREATE_DOCS = "CREATE TABLE documents (docId VARCHAR(128) NOT NULL, hash VARCHAR(128) NOT NULL)";
    /**
     * insert into table command
     */
    public static final String QUERY_ADD_DOCS = "INSERT INTO documents (docId , hash) VALUES (?, ?)";
    
    /**
     * delete first 20 lines from database command
     */
    public static final String QUERY_DELETE_DOCS = "DELETE FROM documents LIMIT 20";
    
    /**
     * select count of documents in database command
     */
    public static final String QUERY_COUNT_DOCS = "SELECT COUNT(*) FROM documents";
    
    /**
     * get data from database command
     */
    public static final String QUERY_SELECT_DOCS = "SELECT docId , hash FROM documents";

   
}