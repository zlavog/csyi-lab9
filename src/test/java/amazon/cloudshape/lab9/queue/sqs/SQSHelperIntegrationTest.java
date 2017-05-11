package amazon.cloudshape.lab9.queue.sqs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SQSHelperIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(SQSHelperIntegrationTest.class);

    private static final String QUEUE_NAME = "qwikLABS-<your-user-name>-queue";

    private static AWSCredentialsProvider credentialsProvider;
    private static AmazonSQS sqsClient;

    private SQSHelper sqsHelper;

    @Test
    public void testListQueues() {
        List<String> queues = sqsHelper.listQueues();
        assertNotNull(queues);

        if (queues.isEmpty()) {
            LOG.info("There is no queue for your account.");
        } else {
            LOG.info("Found queues:\n\t{}", Joiner.on("\n\t").join(queues));
        }
    }

    @Test
    public void testGetQueueAttributes() {
        String queueName = QUEUE_NAME;

        String queueUrl = sqsHelper.getQueueUrl(queueName);
        assertNotNull(queueUrl);

        Map<String, String> attributes = sqsHelper.getQueueAttributes(queueUrl);
        assertNotNull(attributes);
        assertFalse(attributes.isEmpty());

        LOG.info("Queue {} has the following attributes:\n{}", queueName, Joiner.on("\n").withKeyValueSeparator("=").join(attributes));
    }

    @Ignore
    @Test
    public void testCreateQueue() {
        Map<String, String> attributes = ImmutableMap.<String, String>builder()
                .put("DelaySeconds", "0")
                .put("VisibilityTimeout", "10")
                .put("MessageRetentionPeriod", "1800")
                .put("ReceiveMessageWaitTimeSeconds", "20")
                .build();
        sqsHelper.createQueue(QUEUE_NAME, attributes);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        LOG.debug("Starting test setup");

        setupCredentialsProvider();
        setupSqsClient();

        LOG.debug("Test setup completed");
    }


    @Before
    public void setUp() throws Exception {
        sqsHelper = new SQSHelper();
        sqsHelper.setSqsClient(sqsClient);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        LOG.debug("Enter test tear down");

        if (sqsClient != null) {
            sqsClient.shutdown();
        }

        LOG.debug("Test tear down complete");
    }

    private static void setupCredentialsProvider() {
        // In this example we use a file based credentials provider,
        // but in a real life app you should consider implementing
        // a more secure way of storing, authorizing access and actually
        // accessing the credentials
        credentialsProvider = new ProfileCredentialsProvider(new ProfilesConfigFile(new File("credentials")), "lab9");
    }

    private static void setupSqsClient() {
        AWSCredentials credentials = credentialsProvider.getCredentials();

        // TODO Does it help playing with the client configuration settings?
        ClientConfiguration clientConfig = new ClientConfiguration();

        sqsClient = new AmazonSQSClient(credentials, clientConfig);

        // TODO: What happens if the region is not set? Would the tests still pass?
        Region awsRegionId = Region.getRegion(Regions.EU_WEST_1);
        sqsClient.setRegion(awsRegionId);
    }
}