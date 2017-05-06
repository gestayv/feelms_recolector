package cl.feelms.twitter.streaming;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.MongoCredential;
import com.mongodb.MongoClientOptions;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;

import org.bson.Document;
import java.util.Arrays;
import com.mongodb.Block;

import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import org.bson.Document;

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
				/*
				System.out.println(status.getId());
				System.out.println(status.getText());
				*/

				/* 	Usar esto para filtrar países, por ahora solo se obtienen tweets en
					español, independiente de la ubicación.
				if(status.getPlace() != null)
				{
					System.out.println(status.getPlace().getCountryCode());
				}
				*/
				if(status.getLang().equals("es"))
				{

					Document tweet = new Document("id", status.getId())
										.append("user", status.getUser().getScreenName())
										.append("name", status.getUser().getName())
	               						.append("text", status.getText())
										.append("rt_count", status.getRetweetCount())
                                                                .append("fecha", status.getCreatedAt().toString());

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

                //  Usuario
		String user = "";
                //  BD donde está el usuario
 		String database = "";
                //  Pass como arreglo de caracteres, ej: 
                //  si la contraseña es pass, se escribe como {'p', 'a', 's', 's'}
 		char[] password = {};

 		mongoConn.setMC(MongoCredential.createCredential(user, database, password));

 		mongoConn.setMCl(new MongoClient(new ServerAddress("localhost", 27017),
								Arrays.asList(mongoConn.getMC())));

		mongoConn.setMDB(mongoConn.getMCl().getDatabase("feelms"));

		mongoConn.setMColl(mongoConn.getMDB().getCollection("tweets"));

		new TwitterStreaming().init();
	}

}
