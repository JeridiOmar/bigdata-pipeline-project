package kafka;

import com.google.common.collect.Lists;
import com.twitter.hbc.core.Client;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter.TwitterStreaming;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterProducer {

    final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);

    private Client client;
    private TwitterStreaming twitterStreaming;
    private KafkaProducer<String, String> producer;
    private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(30);
    private List<String> trackTerms = Lists.newArrayList("#coronavirus");

    public static void main(String[] args) { new TwitterProducer().run(); }
    //Kafka Producer
    private KafkaProducer<String, String> createKafkaProducer() {
        // Create producer properties
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConfig.BOOTSTRAPSERVERS);
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create safe Producer
        prop.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        prop.setProperty(ProducerConfig.ACKS_CONFIG, KafkaConfig.ACKS_CONFIG);
        prop.setProperty(ProducerConfig.RETRIES_CONFIG, KafkaConfig.RETRIES_CONFIG);
        prop.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, KafkaConfig.MAX_IN_FLIGHT_CONN);

        // Additional settings for high throughput producer
        prop.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, KafkaConfig.COMPRESSION_TYPE);
        prop.setProperty(ProducerConfig.LINGER_MS_CONFIG, KafkaConfig.LINGER_CONFIG);
        prop.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, KafkaConfig.BATCH_SIZE);

        // Create producer
        return new KafkaProducer<String, String>(prop);
    }
    // connect to kafka and twitter client and then start producing to kafka topic
    private void run(){
        logger.info("Setting up");


        // 2. Create Kafka Producer
        producer = createKafkaProducer();

        // Shutdown Hook
//        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
//            logger.info("Application is not stopping!");
//            client.stop();
//            logger.info("Closing Producer");
//            producer.close();
//            logger.info("Finished closing");
//        }));
        twitterStreaming = new TwitterStreaming(producer);
        twitterStreaming.launch();
        // 3. Send Tweets to Kafka

        logger.info("\n Application Started");
    }
}