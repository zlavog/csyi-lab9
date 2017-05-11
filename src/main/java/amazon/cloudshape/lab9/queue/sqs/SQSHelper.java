package amazon.cloudshape.lab9.queue.sqs;

import java.lang.UnsupportedOperationException;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.sqs.model.*;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQS;

public class SQSHelper {

    private static final Logger LOG = LoggerFactory.getLogger(SQSHelper.class);

    private AmazonSQS sqsClient;

    /**
     * @return The list of all queue URLs for the AWS account in use.
     */
    public List<String> listQueues() {
        return sqsClient.listQueues().getQueueUrls();
    }

    /**
     * @param queueName the name of the queue for which to obtain the queue URL
     * @return the URL for the given queue
     */
    public String getQueueUrl(String queueName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(queueName), "The queue name cannot be null or empty!");
        LOG.debug("enter getQueueUrl({})", queueName);

        GetQueueUrlRequest request = new GetQueueUrlRequest(queueName);
        GetQueueUrlResult result = sqsClient.getQueueUrl(request);

        String queueUrl = result.getQueueUrl();
        LOG.debug("exiting getQueueUrl with result {}", queueUrl);

        return queueUrl;
    }

    /**
     * @param queueUrl the URL of an SQS queue
     * @return a map with <b>all</b> the attributes and values of the given queue.
     */
    public Map<String, String> getQueueAttributes(String queueUrl) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(queueUrl), "The queue URL cannot be null or empty!");

        //TODO This needs to be implemented
        throw new UnsupportedOperationException("not implemented; remember to retrieve All attributes ;)");
    }

    /**
     * Creates a queue with the given queue name and default attributes
     *
     * @param queueName the name for the queue
     * @param attributes the map of attributes for the new queue
     * @return the URL of the newly created queue
     */
    public String createQueue(String queueName, Map<String, String> attributes) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(queueName), "The queue name cannot be null or empty!");

        LOG.debug("enter createQueue({}, {})", queueName, Joiner.on(", ").withKeyValueSeparator("=").join(attributes));

        CreateQueueRequest createRequest = new CreateQueueRequest();
        createRequest.setQueueName(queueName);
        createRequest.setAttributes(attributes);
        String queueUrl = sqsClient.createQueue(createRequest).getQueueUrl();

        LOG.debug("exit createQueue({}) -> {}", queueName, queueUrl);

        return queueUrl;
    }

    /**
     * Deletes the queue identified by the specified url
     *
     * @param queueUrl the url for the queue to delete
     */
    public void deleteQueue(String queueUrl) {
        LOG.debug("enter deleteQueue({})", queueUrl);
        DeleteQueueRequest request = new DeleteQueueRequest(queueUrl);
        sqsClient.deleteQueue(request);
        LOG.debug("exit deleteQueue({})", queueUrl);
    }

    public void setSqsClient(AmazonSQS sqsClient) {
        this.sqsClient = sqsClient;
    }
}
