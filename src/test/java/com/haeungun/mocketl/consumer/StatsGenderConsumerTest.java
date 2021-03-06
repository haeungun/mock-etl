package com.haeungun.mocketl.consumer;

import com.haeungun.mocketl.ApplicationContext;
import com.haeungun.mocketl.database.ConnectionManager;
import com.haeungun.mocketl.database.StatsGenderPaymentDao;
import com.haeungun.mocketl.enums.ExecuteState;
import com.haeungun.mocketl.model.UserLog;
import com.haeungun.mocketl.queue.MemoryQueue;
import com.haeungun.mocketl.queue.TopicQueue;
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

public class StatsGenderConsumerTest {

    private static final String CREATE_STAT_GENDER = "CREATE TABLE statsPaymentPerGender ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "ts DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "gender VARCHAR(30), "
            + "userCount BIGINT, "
            + "payment BIGINT);";
    private static final String SELECT_STAT_GENDER = "SELECT SUM(payment) AS payment, SUM(userCount) AS userCount "
            + "FROM statsPaymentPerGender "
            + "WHERE gender = ?;";

    private final ApplicationContext context;
    private final MockUserLog mockUserLog;
    private final int queueCapacity;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private String dbUrl;
    private String topicName;
    private StatsGenderPaymentDao dao;
    private TopicQueue<UserLog> queue;

    public StatsGenderConsumerTest() throws Exception {
        this.context = new ApplicationContext();
        this.mockUserLog = new MockUserLog();
        this.queueCapacity = 50;
        this.topicName = "testTopic";
    }

    @Before
    public void setUp() throws Exception {
        this.temporaryFolder.create();
        this.dbUrl = "jdbc:sqlite:" + this.temporaryFolder.getRoot().getAbsolutePath() + "/test.db";
        this.dao = new StatsGenderPaymentDao(this.dbUrl);
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
            pstmt = conn.prepareStatement(CREATE_STAT_GENDER);
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
    public void 성별별_저장된_payment값_확인() throws InterruptedException {
        this.context.setConsumerState(ExecuteState.RUNNING);
        Consumer consumer = new StatsGenderConsumer(this.context, this.queue, this.topicName, this.dao);

        Thread thread = new Thread(consumer);
        thread.start();
        Thread.sleep(500); // wait to consuming ..
        this.context.setConsumerState(ExecuteState.STOP);
        thread.join();

        ConnectionManager cm = new ConnectionManager(this.dbUrl);

        int payment = 0;
        int count = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = cm.getConnection();
            pstmt = conn.prepareStatement(SELECT_STAT_GENDER);
            pstmt.setString(1, "Female");

            rs = pstmt.executeQuery();
            payment = rs.getInt("payment");
            count = rs.getInt("userCount");
        } catch (Exception e) {
            // TODO handling error
            e.printStackTrace();
        } finally {
            cm.close(conn, pstmt, rs);
        }

        assertEquals(1405100, payment);
        assertEquals(5, count);
    }

}