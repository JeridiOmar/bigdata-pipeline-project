package twitter;

import java.util.HashSet;
import java.util.Set;
import java.io.InputStream;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;

public class TwitterStreaming {
    public static void main(String[] args) {
        /**
         * Set the credentials for the required APIs.
         * The Java SDK supports TwitterCredentialsOAuth2 & TwitterCredentialsBearer.
         * Check the 'security' tag of the required APIs in https://api.twitter.com/2/openapi.json in order
         * to use the right credential object.
         */
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
            Responder responder = new Responder();
            TweetsStreamListenersExecutor tsle = new TweetsStreamListenersExecutor(streamResult);
            tsle.addListener(responder);
            tsle.executeListeners();

//      // Shutdown TweetsStreamListenersExecutor
//      try {
//        Thread.sleep(20000);
//        tsle.shutdown();
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }

//      // An example of how to use the streaming directly using the InputStream result (Without TweetsStreamListenersExecutor)
//      try{
//         JSON json = new JSON();
//         Type localVarReturnType = new TypeToken<StreamingTweet>(){}.getType();
//         BufferedReader reader = new BufferedReader(new InputStreamReader(streamResult));
//         String line = reader.readLine();
//         while (line != null) {
//           if(line.isEmpty()) {
//             System.err.println("==> " + line.isEmpty());
//             line = reader.readLine();
//             continue;
//            }
//           System.out.println(json.getGson().fromJson(line, localVarReturnType).toString());
//           line = reader.readLine();
//         }
//      }catch (Exception e) {
//        e.printStackTrace();
//        System.out.println(e);
//      }
        } catch (ApiException e) {
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}

class Responder implements TweetsStreamListener {
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
            System.out.println("New streaming tweet: " + streamingTweet.getData().getText() + " ==> " + streamingTweet.getMatchingRules());
        }
    }
}
