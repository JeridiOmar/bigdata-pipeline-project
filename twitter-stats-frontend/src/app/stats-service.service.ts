import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {StatsModel} from "./models/statsModel";
import {Observable} from "rxjs";
import {TweetModel} from "./models/tweetModel";
const API_LINK = 'http://localhost:8090';

@Injectable({
  providedIn: 'root'
})
export class StatsServiceService {

  constructor(private http: HttpClient) {
  }
  getGlobalStats(): Observable<StatsModel> {
    return this.http.get<StatsModel>(API_LINK+"/stats/global");
  }
  getTotalTweets(): Observable<number> {
    return this.http.get<number>(API_LINK+"/total");
  }
  getLatestTweets(): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>(API_LINK+"/tweets");
  }
}
