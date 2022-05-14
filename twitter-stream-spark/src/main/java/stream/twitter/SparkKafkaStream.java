package stream.twitter;


import com.google.gson.Gson;
import org.apache.spark.SparkConf;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import org.bson.Document;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SparkKafkaStream {
    private static final Pattern SPACE = Pattern.compile("@@type@@");

    private SparkKafkaStream() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: SparkKafkaWordCount <zkQuorum> <group> <topics> <numThreads>");
            System.exit(1);
        }
//        SparkSession sparkSession = SparkSession.builder()
//                .appName("SparkKafkaStream")
//                //.master("local")
//                .config("spark.mongodb.output.uri", "mongodb://omar:secret@127.0.0.1:27017/test.tweets").getOrCreate();
//        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());

        SparkConf sparkConf = new SparkConf().setAppName("SparkKafkaStream");
        // Creer le contexte avec une taille de batch de 2 secondes
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf,
                new Duration(2000));

        int numThreads = Integer.parseInt(args[3]);
        Map<String, Integer> topicMap = new HashMap<>();
        String[] topics = args[2].split(",");
        for (String topic : topics) {
            topicMap.put(topic, numThreads);
        }

        JavaPairReceiverInputDStream<String, String> tweets =
                KafkaUtils.createStream(jssc, args[0], args[1], topicMap);

        tweets.foreachRDD(rdd -> {

            if (!rdd.isEmpty()) {

                rdd.foreach(
                        e -> {
                            MongoTwitter mongoTwitter = new MongoTwitter();
                            TweetModel tweetModel = new TweetModel();
                            String[] tokens = e._2.split("@@type@@");
                            tweetModel.setText(tokens[0]);
                            tweetModel.setTag(tokens[1]);
                           mongoTwitter.insertTweet(tweetModel);
                        }
                );
            }


        });

        JavaDStream<String> tweetsContent = tweets.map(s -> s._2);
        tweetsContent.print();
        JavaDStream<String> tags =
                tweetsContent.map(x -> x.split("@@type@@")[1]);
        tags.print();
        JavaPairDStream<String, Integer> tagsCount =
                tags.mapToPair(s -> new Tuple2<>(s, 1))
                        .reduceByKey((i1, i2) -> i1 + i2);

        tagsCount.print();
        tagsCount.foreachRDD(rdd->{
            if(!rdd.isEmpty()){
                rdd.foreach(r ->{
                    MongoTwitter mongoTwitter = new MongoTwitter();
                    mongoTwitter.updateStatByTag(r._1,r._2);
                    mongoTwitter.updateGlobalStats(r._1,r._2);
                });
            }
        });
        jssc.start();
        jssc.awaitTermination();
    }
}
