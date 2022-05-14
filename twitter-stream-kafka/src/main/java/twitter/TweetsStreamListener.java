package twitter;

import com.twitter.clientlib.model.FilteredStreamingTweet;
import com.twitter.clientlib.model.StreamingTweet;

public interface TweetsStreamListener {
    void actionOnTweetsStream(FilteredStreamingTweet streamingTweet);
}