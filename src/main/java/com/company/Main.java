
// inspired by: http://www.jcgonzalez.com/quick-mysql-commands-examples

package com.company;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    private static final String TRUSTSTORE = "C:\\ssl\\test001\\truststore";    // Certificate Authority (CA)
    private static final String TRUSTSTORE_PWD = "111111";
    private static final String KEYSTORE = "C:\\ssl\\test001\\keystore";    // User certificate
    private static final String KEYSTORE_PWD = "export";    // le pwd du fichier keystore, doit être le même que le pwd du fichier inclu dans le keystore.

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://192.168.1.114:3306"
            + "?verifyServerCertificate=true"  // verifyServerCertificate=true => truststore
            + "&useSSL=true"
            + "&requireSSL=true";

    private static final String USER = "oldux509";
    private static final String PASS = "oldux509";

    private static final long PAUSE_MILLIS = 1000;

    private static void checkStoreFile(String path) {
        System.out.println( "file: " + path);

        File file = new File(path);

        System.out.println( "exists: "  + file.exists() );
        System.out.println( "is file: "  + file.isFile() );
        System.out.println( "can read: " + file.canRead() );
        System.out.println( "can write: " + file.canWrite() );
        System.out.println( "is hidden: " + file.isHidden() );
        System.out.println();
    }

    public static void main(String[] args) {

        System.out.println();
        System.out.println("#####################");
        System.out.println();

        checkStoreFile(KEYSTORE);
        checkStoreFile(TRUSTSTORE);

        System.setProperty("javax.net.ssl.keyStore",KEYSTORE);
        System.setProperty("javax.net.ssl.keyStorePassword",KEYSTORE_PWD);
        System.setProperty("javax.net.ssl.trustStore",TRUSTSTORE);
        System.setProperty("javax.net.ssl.trustStorePassword",TRUSTSTORE_PWD);
        System.setProperty("javax.net.debug","all");    // network traffic to stdout

        System.out.println("#####################");
        System.out.println();
        System.out.println("KeyStore System Property: " + System.getProperty("javax.net.ssl.keyStore"));
        System.out.println("TrustStore System Property: " + System.getProperty("javax.net.ssl.trustStore"));
        System.out.println();
        System.out.println("#####################");
        System.out.println();

        Connection conn = null;
        try {
            Class.forName(JDBC_DRIVER).newInstance();
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            System.out.println();
            System.out.println("##### Database connection established");
            System.out.println();

            ResultSet rs;

            System.out.println("##### Session ssl status:");
            System.out.println();
            rs = conn.createStatement().executeQuery("SHOW SESSION STATUS LIKE '%ssl%'");
            while (rs.next()){
                System.out.println("##### " + rs.getString(1) + " : " + rs.getString(2));
            }

            System.out.println();
            System.out.println("#####################");
            System.out.println();

            for(int i=1; i<=5; i++){
                try {
                    conn.setAutoCommit(true);
                    rs = conn.createStatement().executeQuery("SELECT CURRENT_TIMESTAMP() as Date");
                    while (rs.next()) {
                        System.out.println();
                        System.out.println(i + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + rs.getString("Date"));
                    }
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("..");
                System.out.println();
                Thread.sleep(PAUSE_MILLIS);
            }
        } catch (Exception e) {
            System.err.println("Cannot connect to database server");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println();
                    System.out.println("Database Connection Terminated");
                } catch (Exception e) {
                }
            }
        }
    }
}
