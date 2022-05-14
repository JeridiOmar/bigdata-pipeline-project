package com.example.twitterstats.dao;

import com.example.twitterstats.models.Tweet;
import com.example.twitterstats.models.TwitterStat;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.*;
import org.bson.Document;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service()
public class MongoTwitterClient {
    private MongoClient mongoClient = new MongoClient(new MongoClientURI("mongodb://omar:secret@127.0.0.1:27017"));
    private DB database = mongoClient.getDB("test");
    private DBCollection collection = database.getCollection("tweets");
    DBCollection statsCollection = database.getCollection("tweetsStats");
    DBCollection globalStatsCollection = database.getCollection("globalTweetsStats");

    public long countTweets(){
        return collection.count();
    };

    public Tweet[] latestTweets(){
        DBCursor dbCursor= collection.find().sort(new BasicDBObject("timestamp",-1)).limit(5);
        Tweet[] tweets=new Tweet[5];
        int i=0;
        while (dbCursor.hasNext()){
            Tweet tweet = new Tweet();
            DBObject dbObject= dbCursor.next();
            if(dbObject.containsField("text")){
                tweet.setText((String) dbObject.get("text"));
            }
            if(dbObject.containsField("tag")){
                tweet.setTag((String) dbObject.get("tag"));
            }
            tweets[i]=tweet;
            i=i+1;
        }
        return tweets;
    };
    public TwitterStat getGlobalStats(){
        //String day=java.time.LocalDate.now().toString();
        DBObject dayObject = globalStatsCollection.findOne(1);
        if(dayObject == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        return getTwitterStatFromDbObject(dayObject);

    }
    public DBObject[] getAllStats(){
        //String day=java.time.LocalDate.now().toString();
        DBCursor dbCursor = statsCollection.find();

        return dbCursor.toArray().toArray(new DBObject[0]);

    }
    public TwitterStat getCurrentDayStats(){
        String day=java.time.LocalDate.now().toString();
        DBObject dayObject = statsCollection.findOne(day);
        if(dayObject == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        return getTwitterStatFromDbObject(dayObject);

    }
    public TwitterStat getStatsByDate(String day){
        DBObject dayObject = statsCollection.findOne(day);
        if(dayObject == null){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        return getTwitterStatFromDbObject(dayObject);

    }
    public TwitterStat getTwitterStatFromDbObject(DBObject dbObject){
        TwitterStat twitterStat = new TwitterStat();
        if(dbObject.containsField("ETHEREUM POSITIVE")){
            twitterStat.setEthereumPositive((double) dbObject.get("ETHEREUM POSITIVE"));
        }
        if(dbObject.containsField("ETHEREUM NEGATIVE")){
            twitterStat.setEthereumNegative((double) dbObject.get("ETHEREUM NEGATIVE"));
        }
        if(dbObject.containsField("BITCOIN POSITIVE")){
            twitterStat.setBitcoinPositive((double) dbObject.get("BITCOIN POSITIVE"));
        }
        if(dbObject.containsField("BITCOIN NEGATIVE")){
            twitterStat.setBitcoinNegative((double) dbObject.get("BITCOIN NEGATIVE"));
        }
        return twitterStat;
    };
}
