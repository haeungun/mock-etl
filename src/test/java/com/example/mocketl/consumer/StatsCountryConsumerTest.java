package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.ConnectionManager;
import com.example.mocketl.database.StatsCountryPaymentDao;
import com.example.mocketl.model.UserLog;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;

public class StatsCountryConsumerTest {

    private static final String CREATE_STAT_COUNTRY = "CREATE TABLE statsPaymentPerCountry ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "ts DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "country VARCHAR(255), "
            + "userCount BIGINT, "
            + "payment BIGINT);"
            + "CREATE INDEX idxTs ON statsPaymentPerCountry(ts);";
    private static final String SELECT_STAT_COUNTRY = "SELECT SUM(payment) AS payment, SUM(userCount) AS userCount "
            + "FROM statsPaymentPerCountry "
            + "WHERE country = ?;";

    private final ApplicationContext context;
    private final MockUserLog mockUserLog;
    private final int queueCapacity;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String dbUrl;
    private StatsCountryPaymentDao dao;
    private BlockingQueue<UserLog> queue;

    public StatsCountryConsumerTest() throws Exception {
        this.context = new ApplicationContext();
        this.mockUserLog = new MockUserLog();
        this.queueCapacity = 50;
    }

    @Before
    public void setUp() throws Exception {
        this.temporaryFolder.create();
        this.dbUrl = "jdbc:sqlite:" + this.temporaryFolder.getRoot().getAbsolutePath() + "/test.db";
        this.dao = new StatsCountryPaymentDao(this.dbUrl);
        this.queue = new ArrayBlockingQueue<>(this.queueCapacity);

        List<UserLog> mock = this.mockUserLog.getMock();
        for (UserLog m : mock) {
            this.queue.put(m);
        }

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
    }

    @Test
    public void savePaymentState() throws InterruptedException {
        Consumer consumer = new StatsCountryConsumer(this.context, this.queue, this.dao);

        Thread thread = new Thread(consumer);
        thread.start();
        thread.join();

        ConnectionManager cm = new ConnectionManager(this.dbUrl);

        int payment = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection();
            pstmt = conn.prepareStatement(SELECT_STAT_COUNTRY);
            pstmt.setString(1, "China");

            rs = pstmt.executeQuery();
            payment = rs.getInt("payment");
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        } finally {
            cm.close(conn, pstmt, rs);
        }

        assertEquals(2111300, payment);
    }

}