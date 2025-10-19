package dev.cheezboi9.farmfix.managers;

import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.util.UUID;

public class DatabaseManager implements AutoCloseable{
  // MongoDB connection string
  private final static String URI = "";
  private final MongoClient mongoClient;
  private final MongoCollection<Document> collection;

  public DatabaseManager() {
    mongoClient = MongoClients.create(URI);
    MongoDatabase database = mongoClient.getDatabase("FarmFix");
    this.collection = database.getCollection("TrampleStates");
    // This should help with faster lookups later
    this.collection.createIndex(new Document("UUID", 1));
  }

  // Setting trample to true
  public void upsertTrample (UUID uuid) {
    Document filter = new Document("UUID", uuid.toString());
    Document update = new Document("$set", new Document().append("Trample", true));
    collection.updateOne(filter, update, new UpdateOptions().upsert(true));
  }

  // Setting Forced to true
  public void upsertForced(UUID uuid) {
    Document filter = new Document("UUID", uuid.toString());
    Document update = new Document("$set", new Document().append("Forced", true));
    collection.updateOne(filter, update, new UpdateOptions().upsert(true));
  }

  /**
   * Removes the ability to trample for the player
   */
  public void removeTrample(UUID uuid) {
    Document filter = new Document("UUID", uuid.toString());
    Document update = new Document("$unset", new Document().append("Trample", ""));

    collection.updateOne(filter,update);
  }

  /**
   * Removes the forced flag on the player
   */
  public void removeForced(UUID uuid) {
   Document filter = new Document("UUID", uuid.toString());
   Document update = new Document("$unset", new Document().append("Forced", ""));

   collection.updateOne(filter,update);
  }

  /**
   * Gets the trample state of the player
   */
  public boolean getTrampleState(UUID uuid) {
    Document filter = new Document("UUID", uuid.toString());
    Document getDoc = collection.find(filter).first();
    if (getDoc == null) {
      return false;
    }
    return getDoc.getBoolean("Trample", false);
  }

  /**
   *  Gets the current forced state of the player
    */
  public boolean getForcedState(UUID uuid) {
    Document filter = new Document("UUID", uuid.toString());
    Document getDoc = collection.find(filter).first();
    if (getDoc == null) {
      return false;
    }
    return getDoc.getBoolean("Forced", false);
  }

  @Override
  public void close() {
    mongoClient.close();
  }
}
