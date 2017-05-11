package amazon.cloudshape.lab9.queue.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link SQSHelper}. What other tests should have been written?
 */
public class SQSHelperTest {

    private AmazonSQS sqsClient;
    private SQSHelper sqsHelper;

    @Before
    public void setUp() throws Exception {
        sqsClient = mock(AmazonSQS.class);

        sqsHelper = new SQSHelper();
        sqsHelper.setSqsClient(sqsClient);
    }

    @Test
    public void testListQueues() throws Exception {
        List<String> expectedList = ImmutableList.of("url1", "url2", "url3");
        ListQueuesResult result = new ListQueuesResult().withQueueUrls(ImmutableList.copyOf(expectedList));

        when(sqsClient.listQueues()).thenReturn(result);
        List<String> actualList = sqsHelper.listQueues();

        assertEquals(expectedList, actualList);
    }

    @Test
    public void testGetQueueUrl() throws Exception {
        String queueUrl = "queueUrl";
        String queueName = "queueName";
        GetQueueUrlRequest expectedRequest = new GetQueueUrlRequest().withQueueName(queueName);
        GetQueueUrlResult result = new GetQueueUrlResult().withQueueUrl(queueUrl);

        when(sqsClient.getQueueUrl(eq(expectedRequest))).thenReturn(result);
        String actualUrl = sqsHelper.getQueueUrl(queueName);

        assertEquals(queueUrl, actualUrl);
    }

    @Test(expected = QueueDoesNotExistException.class)
    public void testGetQueueUrlFail() throws Exception {
        String queueName = "queueName";
        GetQueueUrlRequest expectedRequest = new GetQueueUrlRequest().withQueueName(queueName);

        when(sqsClient.getQueueUrl(eq(expectedRequest))).thenThrow(new QueueDoesNotExistException("does not exist"));

        sqsHelper.getQueueUrl(queueName);
    }

    @Test
    public void testGetQueueAttributes() throws Exception {
        String queueUrl = "queueUrl";
        Map<String, String> attributes = new ImmutableMap.Builder<String, String>()
                .put("ApproximateNumberOfMessages", "1000")
                .put("ApproximateNumberOfMessagesNotVisible", "25")
                .put("ApproximateNumberOfMessagesDelayed", "5").build();

        GetQueueAttributesRequest expectedRequest = new GetQueueAttributesRequest()
                .withQueueUrl(queueUrl)
                .withAttributeNames("All");
        GetQueueAttributesResult result = new GetQueueAttributesResult()
                .withAttributes(ImmutableMap.copyOf(attributes));

        when(sqsClient.getQueueAttributes(eq(expectedRequest))).thenReturn(result);
        Map<String, String> actualAttributes = sqsHelper.getQueueAttributes(queueUrl);

        assertEquals(attributes, actualAttributes);
    }

    @Test
    public void testCreateQueue() throws Exception {
        String queueName = "queueName";
        String queueUrl = "queueUrl";
        Map<String, String> attributes = ImmutableMap.of(
                "VisibilityTimeout", "60",
                "MaximumMessageSize", "1024",
                "MessageRetentionPeriod", "120");
        CreateQueueResult createQueueResult = new CreateQueueResult().withQueueUrl(queueUrl);

        when(sqsClient.createQueue(any(CreateQueueRequest.class))).thenReturn(createQueueResult);
        String response = sqsHelper.createQueue(queueName, attributes);
        assertEquals(queueUrl, response);

        ArgumentCaptor<CreateQueueRequest> captor = ArgumentCaptor.forClass(CreateQueueRequest.class);
        verify(sqsClient).createQueue(captor.capture());

        CreateQueueRequest request = captor.getValue();
        assertEquals(queueName, request.getQueueName());
        assertEquals(attributes, request.getAttributes());
    }

    @Test
    public void testDeleteQueue() throws Exception {
        String queueUrl = "queueUrl";

        sqsHelper.deleteQueue(queueUrl);

        ArgumentCaptor<DeleteQueueRequest> captor = ArgumentCaptor.forClass(DeleteQueueRequest.class);
        verify(sqsClient).deleteQueue(captor.capture());

        DeleteQueueRequest request = captor.getValue();
        assertEquals(queueUrl, request.getQueueUrl());
    }
}