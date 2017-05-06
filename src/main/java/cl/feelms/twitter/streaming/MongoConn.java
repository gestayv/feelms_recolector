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

public class MongoConn
{

    private MongoCredential credential;
    private MongoClient mongoClient;
    private MongoDatabase db;
    private MongoCollection<Document> coll;

    //	Setters
    public void setMCl(MongoClient mongocl)
    {
        this.mongoClient = mongocl;
    }

    public void setMC(MongoCredential mongocr)
    {
        this.credential = mongocr;
    }

    public void setMDB(MongoDatabase mdb)
    {
        this.db = mdb;
    }

    public void setMColl(MongoCollection<Document> mongoColl)
    {
        this.coll = mongoColl;
    }

    //	Getters
    public MongoClient getMCl()
    {
        return this.mongoClient;
    }

    public MongoCredential getMC()
    {
        return this.credential;
    }

    public MongoDatabase getMDB()
    {
        return this.db;
    }

    public MongoCollection<Document> getMColl()
    {
        return this.coll;
    }

}
