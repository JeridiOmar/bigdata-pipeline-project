import {Component, OnInit} from '@angular/core';
import {StatsModel} from "./models/statsModel";
import {StatsServiceService} from "./stats-service.service";
import {interval, Observable} from "rxjs";
import {TweetModel} from "./models/tweetModel";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  constructor(private statsService: StatsServiceService) {
  }
  totalTweets:number=0;
  tweetsStats = [
    {name: 'Bitcoin Positive', value: 0},
    {name: 'Bitcoin negative', value: 0},
    {name: 'Ethereum positive', value: 0},
    {name: 'Ethereum negative', value: 0}
  ];
  latestTweets:TweetModel[] =[];
  title = 'twitter-stats-frontend';
  colorScheme = {
    domain: ['#5AA454', '#C7B42C', '#AAAAAA']
  };
  ngOnInit(){
      this.statsService.getLatestTweets().subscribe(
        latest =>{
          this.latestTweets=latest;
        }
      );
      this.statsService.getTotalTweets().subscribe(
        total=>{
          this.totalTweets=total;
        }
      );
      this.statsService.getGlobalStats().subscribe(
        stats =>{
          this.tweetsStats[0].value=stats.bitcoinPositive;
          console.log(stats.ethereumNegative);
          this.tweetsStats[1].value=stats.bitcoinNegative;
          this.tweetsStats[2].value=stats.ethereumPositive;
          this.tweetsStats[3].value=stats.ethereumNegative;
          this.tweetsStats=[...this.tweetsStats];
        }

      );
    interval(2000).subscribe(x => {
      this.statsService.getGlobalStats().subscribe(
        stats =>{
          this.tweetsStats[0].value=stats.bitcoinPositive;
          console.log(stats.ethereumNegative);
          this.tweetsStats[1].value=stats.bitcoinNegative;
          this.tweetsStats[2].value=stats.ethereumPositive;
          this.tweetsStats[3].value=stats.ethereumNegative;
          this.tweetsStats=[...this.tweetsStats];
        }

      )
    });

    interval(2000).subscribe(x => {
      this.statsService.getLatestTweets().subscribe(
        latest =>{
          this.latestTweets=latest;
        }
      );
    });
    interval(2000).subscribe(x => {
      this.statsService.getTotalTweets().subscribe(
        total=>{
          this.totalTweets=total;
        }
      );
    });
  }
}
