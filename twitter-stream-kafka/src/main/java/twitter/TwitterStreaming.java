package twitter;

import java.util.HashSet;
import java.util.Set;
import java.io.InputStream;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import org.apache.kafka.clients.producer.KafkaProducer;

public class TwitterStreaming {
    KafkaProducer <String, String> kafkaProducer;
    public TwitterStreaming(KafkaProducer<String, String> producer) {
        this.kafkaProducer=producer;
    }

    public void launch() {
        System.out.println(System.getenv("TWITTER_BEARER_TOKEN"));
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(System.getenv("TWITTER_BEARER_TOKEN"));
        TwitterApi apiInstance = new TwitterApi();
        apiInstance.setTwitterCredentials(credentials);

        Set<String> tweetFields = new HashSet<>();
        tweetFields.add("author_id");
        tweetFields.add("id");
        tweetFields.add("created_at");

        try {
            InputStream streamResult = apiInstance.tweets().searchStream(null, tweetFields, null, null, null, null, 0);
            // sampleStream with TweetsStreamListenersExecutor
            Responder responder = new Responder(kafkaProducer);
            TweetsStreamListenersExecutor tsle = new TweetsStreamListenersExecutor(streamResult);
            tsle.addListener(responder);
            tsle.executeListeners();


        } catch (ApiException e) {
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
