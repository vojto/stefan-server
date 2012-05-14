package DocConvert;

import com.google.gdata.client.GoogleService;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * DocumentList class.
 * Include all methods required to manage google docs
 * <img src="Stefan.jpg" align=right >
 */
public class DocumentList {

    public DocsService service;
    public GoogleService spreadsheetsService;
    public static final String DEFAULT_HOST = "docs.google.com";
    public static final String SPREADSHEETS_SERVICE_NAME = "wise";
    private final String URL_FEED = "/feeds";
    private final String URL_DOWNLOAD = "/download";
    private final String URL_DOCLIST_FEED = "/private/full";
    private final String URL_DEFAULT = "/default";
    private final String URL_CATEGORY_EXPORT = "/Export";
    private String host;
    private final Map<String, String> DOWNLOAD_DOCUMENT_FORMATS;

    {
        DOWNLOAD_DOCUMENT_FORMATS = new HashMap<String, String>();
        DOWNLOAD_DOCUMENT_FORMATS.put("doc", "doc");
        DOWNLOAD_DOCUMENT_FORMATS.put("txt", "txt");
        DOWNLOAD_DOCUMENT_FORMATS.put("odt", "odt");
        DOWNLOAD_DOCUMENT_FORMATS.put("pdf", "pdf");
        DOWNLOAD_DOCUMENT_FORMATS.put("png", "png");
        DOWNLOAD_DOCUMENT_FORMATS.put("rtf", "rtf");
        DOWNLOAD_DOCUMENT_FORMATS.put("html", "html");
        DOWNLOAD_DOCUMENT_FORMATS.put("zip", "zip");
    }

    /**
     * Constructor.
     *
     * @param applicationName name of the application.
     *
     * @throws Exception
     */
    public DocumentList(String applicationName) throws Exception {

        this(applicationName, DEFAULT_HOST);
    }

    /**
     * Constructor
     *
     * @param applicationName name of the application
     * @param host the host that contains the feeds
     *
     * @throws Exception
     */
    public DocumentList(String applicationName, String host) throws Exception {
        if (host == null) {
            throw new Exception("null passed in required parameters");
        }

        service = new DocsService(applicationName);
        service.setUserCredentials("", "");

        // Creating a spreadsheets service is necessary for downloading spreadsheets
        spreadsheetsService = new GoogleService(SPREADSHEETS_SERVICE_NAME, applicationName);

        this.host = host;
    }

    /**
     * Set user credentials based on a username and password.
     *
     * @param user username to log in with.
     * @param pass password for the user logging in.
     *
     * @throws AuthenticationException
     * @throws Exception
     */
    public void login(String user, String pass) throws AuthenticationException,
            Exception {
        if (user == null || pass == null) {
            throw new Exception("null login credentials");
        }

        service.setUserCredentials(user, pass);
        spreadsheetsService.setUserCredentials(user, pass);
    }

    /**
     * Upload a file.
     *
     * @param filepath path to uploaded file.
     * @param title title to use for uploaded file.
     *
     * @throws ServiceException when the request causes an error in the Doclist
     * service.
     * @throws IOException when an error occurs in communication with the
     * Doclist service.
     * @throws Exception
     * @return DocumentListEntry service
     */
    public DocumentListEntry uploadFile(String filepath, String title)
            throws IOException, ServiceException, Exception {
        if (filepath == null || title == null) {
            throw new Exception("null passed in for required parameters");
        }

        File file = new File(filepath);
        String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();

        DocumentEntry newDocument = new DocumentEntry();
        newDocument.setFile(file, mimeType);
        newDocument.setTitle(new PlainTextConstruct(title));
        return service.insert(buildUrl(URL_DEFAULT + URL_DOCLIST_FEED), newDocument);
    }
    
    /**
   * Gets the entry for the provided object id.
   *
   * @param resourceId the resource id of the object to fetch an entry for.
   *
   * @throws IOException
   * @throws MalformedURLException
   * @throws ServiceException
   * @throws Exception
   * @return DocumentListEntry service
   */
  public DocumentListEntry getDocsListEntry(String resourceId) throws IOException,
      MalformedURLException, ServiceException, Exception {
    if (resourceId == null) {
      throw new Exception("null resourceId");
    }
    URL url = buildUrl(URL_DEFAULT + URL_DOCLIST_FEED + "/" + resourceId);

    return service.getEntry(url, DocumentListEntry.class);
  }

   
    /**
     * Downloads a document.
     *
     * @param filepath path and name of the object to be saved as.
     * @param resourceId the resource id of the object to be downloaded.
     * @param format format to download the file to. The following file types
     * are supported: documents: "doc", "txt", "odt", "png", "pdf", "rtf",
     * "html"
     *
     * @throws IOException
     * @throws MalformedURLException
     * @throws ServiceException
     * @throws Exception
     */
    public void downloadDocument(String resourceId, String filepath, String format)
            throws IOException, MalformedURLException, ServiceException,
            Exception {
        if (resourceId == null || filepath == null || format == null) {
            throw new Exception("null passed in for required parameters");
        }
        String[] parameters = {"docID=" + resourceId, "exportFormat=" + format};
        URL exportUrl = buildUrl(URL_DOWNLOAD + "/documents" + URL_CATEGORY_EXPORT,
                parameters);


        MediaContent mc = new MediaContent();
        mc.setUri(exportUrl.toString());
        MediaSource ms = service.getMedia(mc);

        InputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = ms.getInputStream();
            outStream = new FileOutputStream(filepath);

            int c;
            while ((c = inStream.read()) != -1) {
                outStream.write(c);
            }
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (outStream != null) {
                outStream.flush();
                outStream.close();
            }
        }
    }

    /**
     * Builds a URL from a patch.
     *
     * @param path the path to add to the protocol/host
     *
     * @throws MalformedURLException
     * @throws Exception
     */
    private URL buildUrl(String path) throws MalformedURLException, Exception {
        if (path == null) {
            throw new Exception("null path");
        }

        return buildUrl(path, null);
    }

    /**
     * Builds a URL with parameters.
     *
     * @param path the path to add to the protocol/host
     * @param parameters parameters to be added to the URL.
     *
     * @throws MalformedURLException
     * @throws Exception
     */
    private URL buildUrl(String path, String[] parameters)
            throws MalformedURLException, Exception {
        if (path == null) {
            throw new Exception("null path");
        }

        return buildUrl(host, path, parameters);
    }

    /**
     * Builds a URL with parameters.
     *
     * @param domain the domain of the server
     * @param path the path to add to the protocol/host
     * @param parameters parameters to be added to the URL.
     *
     * @throws MalformedURLException
     * @throws Exception
     */
    private URL buildUrl(String domain, String path, String[] parameters)
            throws MalformedURLException, Exception {
        if (path == null) {
            throw new Exception("null path");
        }

        StringBuffer url = new StringBuffer();
        url.append("https://" + domain + URL_FEED + path);

        if (parameters != null && parameters.length > 0) {
            url.append("?");
            for (int i = 0; i < parameters.length; i++) {
                url.append(parameters[i]);
                if (i != (parameters.length - 1)) {
                    url.append("&");
                }
            }
        }

        return new URL(url.toString());
    }

    /**
     * Builds a URL with parameters.
     *
     * @param domain the domain of the server
     * @param path the path to add to the protocol/host
     * @param parameters parameters to be added to the URL as key value pairs.
     *
     * @throws MalformedURLException
     * @throws Exception
     */
    private URL buildUrl(String domain, String path, Map<String, String> parameters)
            throws MalformedURLException, Exception {
        if (path == null) {
            throw new Exception("null path");
        }

        StringBuffer url = new StringBuffer();
        url.append("https://" + domain + URL_FEED + path);

        if (parameters != null && parameters.size() > 0) {
            Set<Map.Entry<String, String>> params = parameters.entrySet();
            Iterator<Map.Entry<String, String>> itr = params.iterator();

            url.append("?");
            while (itr.hasNext()) {
                Map.Entry<String, String> entry = itr.next();
                url.append(entry.getKey() + "=" + entry.getValue());
                if (itr.hasNext()) {
                    url.append("&");
                }
            }
        }

        return new URL(url.toString());
    }
}