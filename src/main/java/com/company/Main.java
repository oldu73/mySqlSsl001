package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://192.168.1.114:3306"
            + "?verifyServerCertificate=true"  // verifyServerCertificate=true => truststore
            + "&useSSL=true"
            + "&requireSSL=true";

    private static final String USER = "oldux509";
    private static final String PASS = "oldux509";

    private static final long PAUSE_MILLIS = 1000;

    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.keyStore","C:\\ssl\\test001\\keystore");
        System.setProperty("javax.net.ssl.keyStorePassword","export");// le mdp du fichier keystore, doit être le même que le mdp du fichier inclu dans le keystore.
        System.setProperty("javax.net.ssl.trustStore","C:\\ssl\\test001\\truststore");
        System.setProperty("javax.net.ssl.trustStorePassword","111111"); // le mdp du fichier truststore.
        System.setProperty("javax.net.debug","all");

        System.out.println();
        System.out.println("#####################");
        System.out.println();
        System.out.println("keyStore: " + System.getProperty("javax.net.ssl.keyStore"));
        System.out.println("truststore: " + System.getProperty("javax.net.ssl.trustStore"));
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

            rs = conn.createStatement().executeQuery("SHOW SESSION STATUS LIKE 'Ssl_cipher_list'");
            System.out.println();
            System.out.println("##### Enabled cipher suites:");
            while (rs.next()){
                System.out.println(rs.getString(2));
            }

            System.out.println();

            rs = conn.createStatement().executeQuery("SHOW SESSION STATUS LIKE 'Ssl_cipher'");
            System.out.println();
            System.out.println("##### Cipher suite in use:");
            while (rs.next()){
                System.out.println(rs.getString(2));
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
