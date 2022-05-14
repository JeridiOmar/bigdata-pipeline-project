package com.example.twitterstats;

import com.example.twitterstats.dao.MongoTwitterClient;
import com.example.twitterstats.models.Tweet;
import com.example.twitterstats.models.TwitterStat;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {
    @Autowired
    MongoTwitterClient mongoTwitterClient;
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/total")
    public long dayStats() {
        return mongoTwitterClient.countTweets();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/tweets")
    public Tweet[] latestTweets() {
        return mongoTwitterClient.latestTweets();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/stats/global")
    public TwitterStat globalStats() {
        return mongoTwitterClient.getGlobalStats();
    }
    @CrossOrigin(origins = "http://localhost:4200")

    @GetMapping("/stats/all")
    public DBObject[] allStats() {
        return mongoTwitterClient.getAllStats();
    }
    @CrossOrigin(origins = "http://localhost:4200")

    @GetMapping("/stats")
    public TwitterStat todayStats() {
        return mongoTwitterClient.getCurrentDayStats();
    }
    @CrossOrigin(origins = "http://localhost:4200")

    @GetMapping("/stats/{day}")
    public TwitterStat dayStats(@PathVariable String day) {
        return mongoTwitterClient.getStatsByDate(day);
    }

}
