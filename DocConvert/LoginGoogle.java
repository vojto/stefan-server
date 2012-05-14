package DocConvert;

import com.google.gdata.client.docs.DocsService;

/**
 * LoginGoogle class. Handle connection to google services.
 * <img src="Stefan.jpg" align=right >
 */
public class LoginGoogle {
    /**
     * Google docs serivces
     */
    private DocsService service ;
    /**
     * Google account user name
     */
    private String USERNAME;
    /**
     * Google account password
     */
    private String PASSWORD;
    /**
     * LoginGoogle Constructor
     * @param USERNAME Google service username
     * @param PASSWORD Google service password
     */
    public LoginGoogle(String USERNAME, String PASSWORD){
        this.PASSWORD = PASSWORD;
        this.USERNAME = USERNAME;
    
    }
    /**
     * Login to google services
     * @return true on connect successful, false otherwise
     */
    public boolean Login(){
        try{
            service = new DocsService("DocumentList");
            service.setUserCredentials(USERNAME + "@gmail.com", PASSWORD);
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
}
