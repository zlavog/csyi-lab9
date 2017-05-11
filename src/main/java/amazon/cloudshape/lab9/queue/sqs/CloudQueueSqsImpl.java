package amazon.cloudshape.lab9.queue.sqs;

import amazon.cloudshape.lab9.queue.CloudQueue;
import amazon.cloudshape.lab9.queue.QueueMessage;
import amazon.cloudshape.lab9.serialization.ItemSerializationException;
import amazon.cloudshape.lab9.serialization.PayloadConverter;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.UnsupportedOperationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CloudQueueSqsImpl<T> implements CloudQueue<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CloudQueueSqsImpl.class);

    protected static final String RECEIVE_COUNT_ATTRIBUTE_NAME = "ApproximateReceiveCount";
    protected static final String SENT_TIMESTAMP_ATTRIBUTE_NAME = "SentTimestamp";

    private PayloadConverter<T> payloadConverter;
    private String queueUrl;
    private AmazonSQS sqsClient;


    @Override
    public void put(T item) {
        //TODO This needs to be implemented
        throw new UnsupportedOperationException("not implemented; hint: don't forget about serializing the object before ...");
    }

    @Override
    public QueueMessage<T> peek() {
        List<QueueMessage<T>> items = peek(1);
        if (items == null || items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    @Override
    public List<QueueMessage<T>> peek(int maxOutputSize) {
        LOG.trace("Enter peek({})", maxOutputSize);
        try {
            ReceiveMessageRequest request = new ReceiveMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withAttributeNames(ImmutableList.of(RECEIVE_COUNT_ATTRIBUTE_NAME, SENT_TIMESTAMP_ATTRIBUTE_NAME))
                    .withMaxNumberOfMessages(maxOutputSize);

            ReceiveMessageResult result = sqsClient.receiveMessage(request);
            List<com.amazonaws.services.sqs.model.Message> messages = result.getMessages();
            if (messages.isEmpty()) {
                return Collections.emptyList();
            }

            LOG.trace("Found {} messages", messages.size());
            List<QueueMessage<T>> queueItems = new ArrayList<>(messages.size());
            for (com.amazonaws.services.sqs.model.Message sqsMsg : messages) {
                LOG.trace("Returning message Id <{}> for queue {}", sqsMsg.getMessageId(), queueUrl);

                T item = null;
                try {
                    item = payloadConverter.deserialize(sqsMsg.getBody());
                } catch (Throwable e) {
                    throw new ItemSerializationException(e, sqsMsg.getReceiptHandle());
                }
                QueueMessage<T> queueItem = new QueueMessage<T>(item, sqsMsg.getReceiptHandle());
                queueItems.add(queueItem);
            }

            LOG.trace("Exit peek()");
            return queueItems;
        } catch (OverLimitException e) {
            LOG.error("Could not read messages from queue {}", queueUrl, e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public void delete(String id) {
        //TODO This needs to be implemented
        throw new UnsupportedOperationException("not implemented; it's more simple than DeleteMessageBatch");
    }

    @Override
    public void delete(List<String> ids)  {
        try {
            DeleteMessageBatchRequest request = new DeleteMessageBatchRequest().withQueueUrl(queueUrl);
            List<DeleteMessageBatchRequestEntry> entryList = new ArrayList<>(ids.size());
            for (String id : ids) {
                entryList.add(new DeleteMessageBatchRequestEntry().withReceiptHandle(id)
                        .withId(UUID.randomUUID().toString()));
            }
            request.setEntries(entryList);

            sqsClient.deleteMessageBatch(request);

        } catch (AmazonServiceException e) {
            LOG.error("Could not delete messages batch from {}", queueUrl, e);
            Throwables.propagate(e);
        }
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public void setPayloadConverter(PayloadConverter<T> payloadConverter) {
        this.payloadConverter = payloadConverter;
    }

    public void setSqsClient(AmazonSQS messageQueue) {
        this.sqsClient = messageQueue;
    }
}
