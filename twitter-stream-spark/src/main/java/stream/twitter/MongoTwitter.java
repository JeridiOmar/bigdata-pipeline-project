package stream.twitter;

import com.mongodb.*;

import java.util.Date;
import java.util.UUID;

public class MongoTwitter {
    private MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://omar:secret@127.0.0.1:27017"));
    private DB database = mongoClient.getDB("test");
    private DBCollection collection = database.getCollection("tweets");
    DBCollection statsCollection = database.getCollection("tweetsStats");
    DBCollection globalStatsCollection = database.getCollection("globalTweetsStats");

    public MongoTwitter() {

    }

    public void insertTweet(TweetModel tweetModel) {
        UUID uuid = UUID.randomUUID();
        DBObject document = new BasicDBObject("_id", uuid.toString())
                .append("text", tweetModel.getText())
                .append("tag", tweetModel.getTag())
                .append("timestamp", new Date().getTime());

        collection.insert(document);

    }

    public void updateStatByTag(String tag, double newValue) {
        String day = java.time.LocalDate.now().toString();
        DBObject dayObject = statsCollection.findOne(day);
        if (dayObject == null) {
            DBObject document = new BasicDBObject("_id", day)
                    .append(tag, newValue);
            statsCollection.insert(document);
        } else {
            double value = 0;
            if (dayObject.containsField(tag)) {
                value = (double) dayObject.get(tag);
            }
            value = value + newValue;
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put(tag, value);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", day);
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            statsCollection.update(query, updateObject);
        }
    }
    public void updateGlobalStats(String tag, double newValue) {
        DBObject dayObject = globalStatsCollection.findOne(1);
        if (dayObject == null) {
            DBObject document = new BasicDBObject("_id", 1)
                    .append(tag, newValue);
            globalStatsCollection.insert(document);
        } else {
            double value = 0;
            if (dayObject.containsField(tag)) {
                value = (double) dayObject.get(tag);
            }
            value = value + newValue;
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put(tag, value);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", 1);
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            globalStatsCollection.update(query, updateObject);
        }
    }
}
