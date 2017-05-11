package amazon.cloudshape.lab9.queue.sqs;

import amazon.cloudshape.lab9.model.CatInfo;
import amazon.cloudshape.lab9.queue.CloudQueue;
import amazon.cloudshape.lab9.queue.QueueMessage;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import org.joda.time.DateTimeUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/appcontext/CloudQueueSqsImplIntegrationTest-context.xml"})
public class CloudQueueSqsImplComplexIntegrationTest {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd_HH:mm:ss");

    private static final Logger LOG = LoggerFactory.getLogger(CloudQueueSqsImplComplexIntegrationTest.class);
    private static final int TEST_TIME = 30000;
    private Random random;
    private AtomicInteger numOfProducedCatMessages;
    private AtomicInteger numOfConsumedCatMessages;

    private String testId;

    @Autowired
    @Qualifier(value = "queueUrl")
    private String queueUrl;

    @Autowired
    @Qualifier(value = "sqsHelper")
    private SQSHelper sqsHelper;

    @Autowired
    @Qualifier(value = "catsSqsQueue")
    private CloudQueue<CatInfo> catInfoQueue;

    @Before
    public void setUp() throws Exception {
        random = new Random();
        numOfProducedCatMessages = new AtomicInteger(0);
        numOfConsumedCatMessages = new AtomicInteger(0);
        testId = DATE_FORMAT.print(DateTimeUtils.currentTimeMillis());
    }

    @Test(timeout = TEST_TIME + 40000)
    public void testMoreComplexScenario() throws Exception {
        LOG.info("Starting the test");

        ScheduledExecutorService producerExecutor = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("Producer-"));
        producerExecutor.scheduleAtFixedRate(new Producer(), 0, 1, TimeUnit.SECONDS);

        ScheduledExecutorService consumerExecutor = Executors.newScheduledThreadPool(1, new CustomizableThreadFactory("Consumer-"));
        consumerExecutor.scheduleAtFixedRate(new Consumer(), 0, 10, TimeUnit.SECONDS);

        // let the workers do their job
        Thread.sleep(TEST_TIME);

        destroyExecutor(producerExecutor, 5, TimeUnit.SECONDS);
        destroyExecutor(consumerExecutor, 30, TimeUnit.SECONDS);

        // let's see what's the state of the queue
        Map<String, String> attributes = sqsHelper.getQueueAttributes(queueUrl);
        LOG.info("Queue attributes:\n{}", Joiner.on("\n").withKeyValueSeparator("=").join(attributes));
        LOG.info("Produced messages:{}", numOfProducedCatMessages.get());
        LOG.info("Consumed messages:{}", numOfConsumedCatMessages.get());

        LOG.info("Test completed");
    }

    private class Producer implements Runnable {
        @Override
        public void run() {
            try {
                doRun();
            } catch(Exception ex) {
                LOG.error("error in producer", ex);
                Throwables.propagate(ex);
            }
        }

        private void doRun() {
            CatInfo info = createCatInfo();
            LOG.info("Putting new message in queue: {}", info);

            //TODO This needs to be implemented
            throw new UnsupportedOperationException("needs one more line of code");
        }
    }

    private class Consumer implements Runnable {
        @Override
        public void run() {
            LOG.info("Running Consumer");

            try {
                doRun();
            } catch(Exception ex) {
                LOG.error("error in consumer", ex);
                Throwables.propagate(ex);
            }

            LOG.info("Consumer ended successfully");
        }

        private void doRun() {
            //TODO This needs to be implemented
            throw new UnsupportedOperationException("not implemented");
        }

        private boolean processMessage(QueueMessage<CatInfo> catMsg) {
            simulateWork(random.nextInt(500));
            return true;
        }
    }

    private static void simulateWork(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOG.warn("Interrupted", e);
        }
    }

    private CatInfo createCatInfo() {
        String name = String.format("Cat-%s-%04d", testId, numOfProducedCatMessages.getAndIncrement());
        CatInfo info = new CatInfo();
        info.setName(name);
        info.setAge(random.nextInt(20));
        info.setDescription("My lovely cat. It's called " + name);
        return info;
    }

    public void destroyExecutor(ExecutorService executor, long timeout, TimeUnit timeUnit) {
        LOG.debug("Destroying executor...");

        // nothing to be done, executor is terminated already
        if (executor == null || executor.isTerminated()) {
            LOG.debug("Already terminated");
            return;
        }

        executor.shutdown();

        try {
            if ( ! executor.awaitTermination(timeout, timeUnit)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ie) {
            LOG.error("Await interrupted!", ie);
        }
        LOG.debug("Finished destroy");
    }
}