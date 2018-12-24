package com.example.mocketl.consumer;

import com.example.mocketl.ApplicationContext;
import com.example.mocketl.database.ConnectionManager;
import com.example.mocketl.database.StatsCountryPaymentDao;
import com.example.mocketl.enums.ExecuteState;
import com.example.mocketl.model.UserLog;
import com.example.mocketl.queue.MemoryQueue;
import com.example.mocketl.queue.TopicQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

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
    private String topicName;
    private StatsCountryPaymentDao dao;
    private TopicQueue<UserLog> queue;

    public StatsCountryConsumerTest() throws Exception {
        this.context = new ApplicationContext();
        this.mockUserLog = new MockUserLog();
        this.queueCapacity = 30;
        this.topicName = "testTopic";
    }

    @Before
    public void setUp() throws Exception {
        this.temporaryFolder.create();
        this.dbUrl = "jdbc:sqlite:" + this.temporaryFolder.getRoot().getAbsolutePath() + "/test.db";
        this.dao = new StatsCountryPaymentDao(this.dbUrl);
        this.queue = new MemoryQueue<>(this.queueCapacity);

        List<UserLog> mock = this.mockUserLog.getMock();
        for (UserLog m : mock) {
            this.queue.produce(this.topicName, m);
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
    public void 나라별_저장된_payment값_확인() throws InterruptedException {
        this.context.setConsumerState(ExecuteState.RUNNING);
        Consumer consumer = new StatsCountryConsumer(this.context, this.queue, this.topicName, this.dao);

        Thread thread = new Thread(consumer);
        thread.start();
        Thread.sleep(500); // wait to consuming ..
        this.context.setConsumerState(ExecuteState.STOP);
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