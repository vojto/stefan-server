package DocConvert;

import com.google.gdata.data.docs.DocumentListEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;

/**
 * DocUpload class. Handle all tasks associated with uploading file to google
 * drive
 * <img src="Stefan.jpg" align=right >
 */
public class DocUpload {

    /**
     * Creates MD5sum from file definied in path
     *
     * @param path path to file
     * @return file's MD5sum
     */
    public String getMD5(String path) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            File f = new File(path);

            if (f.length() > 2000000) {
                System.out.println("File is under 2MB");
                File fSubor = new File(System.getProperty("user.dir") + "/subor.txt");
                if (fSubor.exists()) {
                    fSubor.delete();
                }
                System.exit(0);
            }
            InputStream is = new FileInputStream(f);
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            return output;
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException("Unable to process file for MD5 ", e);
        }
    }

    /**
     * Gets document ID of file described in path
     *
     * @param list object providing google access to google services
     * @param path path to file
     * @return document ID
     */
    public String getDocId(DocumentList list, String path) {
        DocumentListEntry entry;

        try {
            Class.forName(DatabaseSettings.DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(DatabaseSettings.URL,
                    DatabaseSettings.USER, DatabaseSettings.PASSWORD);
            Statement stm = connection.createStatement();
            try {
                stm.executeUpdate(DatabaseSettings.QUERY_CREATE_DOCS);
            } catch (Exception e) {
                //database table is already created
            }

            String hash = getMD5(path);
            String docId = checkDB(hash, stm);
            if (docId != null) {
                stm.close();
                return docId;
            } else {
                if (!haveSpace(stm)) {
                    deleteFile(list, stm);
                }
                entry = list.uploadFile(path, hash);
                PreparedStatement pstm = connection.prepareStatement(DatabaseSettings.QUERY_ADD_DOCS);
                pstm.setString(1, entry.getDocId());
                pstm.setString(2, hash);
                pstm.execute();
                pstm.close();
                stm.close();
            }
            connection.close();
            return entry.getDocId();
        } catch (Exception e) {
            System.out.println("Exception occured during wtiring to database: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check database for same file based on hash value
     *
     * @param hash hash value of file
     * @param stm object used to execute sql commands
     * @return file ID if file is already in database, null otherwise
     */
    public String checkDB(String hash, Statement stm) {
        try {
            ResultSet rs = stm.executeQuery(DatabaseSettings.QUERY_SELECT_DOCS);
            while (rs.next()) {
                if (hash.equals(rs.getString(2))) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return null;
    }

    /**
     * Check free space on google drive
     *
     * @param stm object used to execute sql commands
     * @return true if there's free space on google drive, false otherwise
     */
    private boolean haveSpace(Statement stm) {
        try {
            ResultSet rs = stm.executeQuery(DatabaseSettings.QUERY_COUNT_DOCS);
            if (rs.next()) {
                if (5120 < rs.getInt(1) * 2) {
                    return false;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return true;
    }

    /**
     * Remove file from google drive and database
     *
     * @param list object providing google access to google services
     * @param stm object used to execute sql commands
     */
    private void deleteFile(DocumentList list, Statement stm) {

        try {
            ResultSet rs = stm.executeQuery(DatabaseSettings.QUERY_SELECT_DOCS);
            for (int row = 0; row < 20; row++) {
                rs.next();
                DocumentListEntry entry = list.getDocsListEntry(rs.getString(1));
                entry.delete();
            }
            stm.executeUpdate(DatabaseSettings.QUERY_DELETE_DOCS);
        } catch (SQLException ex) {
            System.out.println(ex);
        } catch (Exception ex2) {
            System.out.println(ex2);
        }

    }
}