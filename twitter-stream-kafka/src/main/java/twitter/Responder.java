package twitter;


import com.twitter.clientlib.model.FilteredStreamingTweet;
import com.twitter.clientlib.model.FilteredStreamingTweetOneOf;
import kafka.KafkaConfig;
import kafka.TwitterProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

class Responder implements TweetsStreamListener {
    final Logger logger = LoggerFactory.getLogger(TwitterProducer.class);
    KafkaProducer<String,String> kafkaProducer;
    public Responder(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer=kafkaProducer;
    }

    @Override
    public void actionOnTweetsStream(FilteredStreamingTweet filteredStreamingTweet) {
        FilteredStreamingTweetOneOf streamingTweet = filteredStreamingTweet.getFilteredStreamingTweetOneOf();
        if (streamingTweet == null) {
            System.err.println("Error: actionOnTweetsStream - streamingTweet is null ");
            return;
        }

//        if(streamingTweet != null) {
////            streamingTweet.getErrors().forEach(System.out::println);
////            streamingTweet.
//        }
        if (streamingTweet.getData() != null) {
            logger.info("New streaming tweet: " + streamingTweet.getData().getText() + "@@type@@" + streamingTweet.getMatchingRules().get(0).getTag().trim().toUpperCase(Locale.ROOT));
            this.kafkaProducer.send(new ProducerRecord<String, String>(KafkaConfig.TOPIC, null, streamingTweet.getData().getText()+ "@@type@@" + streamingTweet.getMatchingRules().get(0).getTag().trim().toUpperCase(Locale.ROOT)), new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        logger.error("Some error OR something bad happened", e);
                    }
                }
            });
        }
    }
}
