package com.example.mocketl.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class StatsCountryDaoTest {

    private static final String CREATE_STAT_COUNTRY = "CREATE TABLE statsPaymentPerCountry ("
                                                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                                                        + "ts DATETIME DEFAULT CURRENT_TIMESTAMP, "
                                                        + "country VARCHAR(255), "
                                                        + "payment BIGINT);"
                                                    + "CREATE INDEX idxTs ON statsPaymentPerCountry(ts);";
    private static final String SELECT_STAT_COUNTRY = "SELECT SUM(payment) AS payment "
                                                        + "FROM statsPaymentPerCountry "
                                                        + "WHERE country = ?;";
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String dbUrl;

    @Before
    public void setUp() throws Exception {
        this.temporaryFolder.create();
        this.dbUrl = "jdbc:sqlite:" + this.temporaryFolder.getRoot().getAbsolutePath() + "/test.db";

        ConnectionManager cm = new ConnectionManager(this.dbUrl);

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = cm.getConnection();
            pstmt = conn.prepareStatement(CREATE_STAT_COUNTRY);
            pstmt.executeUpdate();
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        } finally {
            cm.close(conn, pstmt);
        }
    }

    @After
    public void tearDown() throws Exception {
        this.temporaryFolder.delete();
    }

    @Test
    public void insertCountryStats_테스트() throws SQLException {
        String[] expectedCountries = {"MyCountry1", "MyCountry2"};
        int[] expectedCount = {10, 50};

        StatsCountryPaymentDao dao = new StatsCountryPaymentDao(this.dbUrl);
        dao.insertOne(expectedCountries[0], 5);
        dao.insertOne(expectedCountries[1], 25);
        dao.insertOne(expectedCountries[0], 5);
        dao.insertOne(expectedCountries[1], 25);

        ConnectionManager cm = new ConnectionManager(this.dbUrl);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection();
            for (int i = 0; i < expectedCountries.length; i++) {
                pstmt = conn.prepareStatement(SELECT_STAT_COUNTRY);
                pstmt.setString(1, expectedCountries[i]);
                rs = pstmt.executeQuery();

                int count = rs.getInt("payment");
                assertEquals(expectedCount[i], count);
            }
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        } finally {
            cm.close(conn, pstmt, rs);
        }
    }

}