package amazon.cloudshape.lab9.queue.sqs;

import amazon.cloudshape.lab9.model.CatInfo;
import amazon.cloudshape.lab9.queue.CloudQueue;
import amazon.cloudshape.lab9.queue.QueueMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/appcontext/CloudQueueSqsImplIntegrationTest-context.xml"})
public class CloudQueueSqsImplSimpleIntegrationTest {

    private static final Logger LOG = LoggerFactory.getLogger(CloudQueueSqsImplSimpleIntegrationTest.class);

    @Autowired
    @Qualifier(value = "catsSqsQueue")
    private CloudQueue<CatInfo> catInfoQueue;

    @Test
    public void testPutPeekDelete() throws Exception {
        CatInfo info = new CatInfo();
        info.setName("Ginger");
        info.setAge(5);
        info.setDescription("A nice orange cat");
        catInfoQueue.put(info);

        info.setName("Bizucu");
        info.setAge(7);
        info.setDescription("Very naughty cat.");
        catInfoQueue.put(info);

        pause();

        List<QueueMessage<CatInfo>> catMessages;
        do {
            catMessages = catInfoQueue.peek(10);
            LOG.info("Retrieved {} messages from queue", catMessages.size());

            for (QueueMessage<CatInfo> msg : catMessages) {
                catInfoQueue.delete(msg.getIdForDelete());
            }
        } while (!catMessages.isEmpty());

        pause();

        // is the queue truly empty
        catMessages = catInfoQueue.peek(10);
        assertTrue(catMessages.isEmpty());
    }

    private static void pause() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOG.warn("Interrupted", e);
            Thread.interrupted();
        }
    }
}