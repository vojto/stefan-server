package DocConvert;

/**
 * Main class.
 * <img src="Stefan.jpg" align=right >
 */
public class DocConverter {

    /**
     * DocConverter Constructor
     *
     * @param String path to load timage
     *
     */
    private DocConverter(String pathUplouad) {
        DocUpload uploadDownload = new DocUpload();

        LoginGoogle login = new LoginGoogle("", "");
        try {
            login.Login();
            DocumentList docList = new DocumentList("DocumentList");
            String docId = uploadDownload.getDocId(docList, pathUplouad);
            docList.downloadDocument(docId, System.getProperty("user.dir") + "/subor.txt", "txt");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * First method called at start. Run this method with one param - path to
     * file to be converted to txt. Supported file types: HTML, RTF, DOC, DOCX,
     * XLS, XLSX, ODT, TXT
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new DocConverter(args[0]);
    }
}