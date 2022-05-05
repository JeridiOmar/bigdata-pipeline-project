package twitter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.reflect.TypeToken;

import com.twitter.clientlib.JSON;
import com.twitter.clientlib.model.FilteredStreamingTweet;
import com.twitter.clientlib.model.StreamingTweet;
import com.twitter.clientlib.model.SingleTweetLookupResponse;

public class TweetsStreamListenersExecutor {
    private final ITweetsQueue tweetsQueue;
    private final List<TweetsStreamListener> listeners = new ArrayList<>();
    private final InputStream stream;
    private volatile boolean isRunning = true;

    public TweetsStreamListenersExecutor(InputStream stream) {
        this.tweetsQueue = new LinkedListTweetsQueue();
        this.stream = stream;
    }

    public TweetsStreamListenersExecutor(ITweetsQueue tweetsQueue, InputStream stream) {
        this.tweetsQueue = tweetsQueue;
        this.stream = stream;
    }

    public void addListener(TweetsStreamListener toAdd) {
        listeners.add(toAdd);
    }

    public void executeListeners() {
        if (stream == null) {
            System.out.println("Error: stream is null.");
            return;
        } else if (this.tweetsQueue == null) {
            System.out.println("Error: tweetsQueue is null.");
            return;
        }

        TweetsQueuer tweetsQueuer = new TweetsQueuer();
        TweetsListenersExecutor tweetsListenersExecutor = new TweetsListenersExecutor();
        tweetsListenersExecutor.start();
        tweetsQueuer.start();
    }

    public synchronized void shutdown() {
        isRunning = false;
        System.out.println("TweetsStreamListenersExecutor is shutting down.");
    }

    private class TweetsListenersExecutor extends Thread {
        @Override
        public void run() {
            processTweets();
        }

        private void processTweets() {
            FilteredStreamingTweet streamingTweet;
            try {
                while (isRunning) {
                    streamingTweet = tweetsQueue.poll();
                    if (streamingTweet == null) {
                        Thread.sleep(100);
                        continue;
                    }
                    for (TweetsStreamListener listener : listeners) {
                        listener.actionOnTweetsStream(streamingTweet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TweetsQueuer extends Thread {
        @Override
        public void run() {
            queueTweets();
        }

        public void queueTweets() {
            JSON json = new JSON();
            Type localVarReturnType = new TypeToken<SingleTweetLookupResponse>() {}.getType();

            String line = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                while (isRunning) {
                    line = reader.readLine();
                    if(line == null || line.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }
                    try {
                        tweetsQueue.add(FilteredStreamingTweet.fromJson(line));
                    } catch (Exception interExcep) {
                        interExcep.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                shutdown();
            }
        }
    }
}

interface ITweetsQueue {
    FilteredStreamingTweet poll();
    void add(FilteredStreamingTweet streamingTweet);
}

class LinkedListTweetsQueue implements ITweetsQueue {
    private final Queue<FilteredStreamingTweet> tweetsQueue = new LinkedList<>();

    @Override
    public FilteredStreamingTweet poll() {
        return tweetsQueue.poll();
    }

    @Override
    public void add(FilteredStreamingTweet streamingTweet) {
        tweetsQueue.add(streamingTweet);
    }
}