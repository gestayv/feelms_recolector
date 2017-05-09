package cl.feelms.twitter.streaming;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;

import java.util.Arrays;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;

import org.apache.commons.io.IOUtils;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import org.bson.Document;
import twitter4j.TweetEntity;

public class TwitterStreaming {

    private final TwitterStream twitterStream;
    private Set<String> keywords;
    public static MongoConn mongoConn = new MongoConn();
    

    private TwitterStreaming() {
        this.twitterStream = new TwitterStreamFactory().getInstance();
        this.keywords = new HashSet<>();
        loadKeywords();
    }

    private void loadKeywords() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            keywords.addAll(IOUtils.readLines(classLoader.getResourceAsStream("words.dat"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        StatusListener listener = new StatusListener() {

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                    System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                    System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            public void onScrubGeo(long userId, long upToStatusId) {
                    System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            public void onException(Exception ex) {
                    ex.printStackTrace();
            }

            @Override
            public void onStallWarning(StallWarning arg0) {

            }

            @Override
            public void onStatus(Status status) {
                /* 	Usar esto para filtrar países, por ahora solo se obtienen tweets en
                        español, independiente de la ubicación.
                if(status.getPlace() != null)
                {
                        System.out.println(status.getPlace().getCountryCode());
                }
                */
                if(status.getLang().equals("es"))
                {
                    Calendar cal = Calendar.getInstance();
                    int yearT = cal.get(Calendar.YEAR);
                    int monthT = cal.get(Calendar.MONTH) + 1;
                    int dayT = cal.get(Calendar.DAY_OF_MONTH);
                    String mString = Integer.toString(monthT);
                    String dString = Integer.toString(dayT);
                    
                    if(monthT < 10)
                    {
                        mString = "0"+mString;
                    }
                    
                    if(dayT < 10)
                    {
                        dString = "0"+dString;
                    }

                    int htNumber = status.getHashtagEntities().length;
                    int htCounter = 1;
                    String ht = " ";
                    
                    
                    if(htNumber > 0)
                    {
                        ht = "";
                        for (TweetEntity e : status.getHashtagEntities()) 
                        {
                            if(htCounter == 1)
                            {
                                ht = e.getText();
                            }
                            else if(htCounter ==  status.getHashtagEntities().length - 1)
                            {
                                ht = ht+e.getText();
                                htCounter++;
                            }
                            else
                            {
                                ht = ht+" "+e.getText()+" ";
                                htCounter++;
                            }
                        }
                    }
                    
                    Document tweet = new Document("id", status.getId())
                                        .append("user", status.getUser().getScreenName())
                                        .append("name", status.getUser().getName())
                                        .append("text", status.getText())
                                        .append("rt_count", status.getRetweetCount())
                                        .append("fecha", yearT+"-"+mString+"-"+dString)
                                        .append("hashtag", ht);

                    mongoConn.getMColl().insertOne(tweet);
                }
            }
        };

        FilterQuery fq = new FilterQuery();

        fq.track(keywords.toArray(new String[0]));

        this.twitterStream.addListener(listener);
        this.twitterStream.filter(fq);
    }

    public static void main(String[] args) {
        
        int i = 0;
        
        KeywordRetrieval kr = new KeywordRetrieval();
        kr.conn();
        
        mongoConn.setMCl(new MongoClient(new ServerAddress("localhost", 27017)));

        mongoConn.setMDB(mongoConn.getMCl().getDatabase("feelms"));

        mongoConn.setMColl(mongoConn.getMDB().getCollection("tweets"));

        new TwitterStreaming().init();
    }

}
