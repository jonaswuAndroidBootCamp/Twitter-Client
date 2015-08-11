package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(9, "com.codepath.apps.restclienttemplate.dao");
        Entity twitter = schema.addEntity("Twitter");
        twitter.addLongProperty("internalId").primaryKey().autoincrement();
        twitter.addLongProperty("id");
        twitter.addDateProperty("created_at");
        twitter.addLongProperty("retweet_count");
        twitter.addLongProperty("favorite_count");
        twitter.addStringProperty("text");


        Entity user = schema.addEntity("User");
        user.addLongProperty("internalId").primaryKey().autoincrement();
        user.addLongProperty("id");
        user.addStringProperty("name");
        user.addStringProperty("screen_name");
        user.addStringProperty("profile_image_url");
        user.addStringProperty("location");


        Entity CurrentUser = schema.addEntity("CurrentUser");
        CurrentUser.addLongProperty("internalId").primaryKey().autoincrement();
        CurrentUser.addLongProperty("id");
        CurrentUser.addStringProperty("name");
        CurrentUser.addStringProperty("screen_name");
        CurrentUser.addStringProperty("profile_image_url");
        CurrentUser.addStringProperty("location");


        Entity media = schema.addEntity("Media");
        media.addLongProperty("internalId").primaryKey().autoincrement();
        media.addLongProperty("id");
        media.addStringProperty("type");
        media.addStringProperty("media_url");
        Property inTweets = media.addLongProperty("inTweets").getProperty();
        twitter.addToMany(media, inTweets);

        // twitter to user => one to one rel
        Property twitterUserIdMapping = twitter.addLongProperty("twitterUserIdMapping").getProperty();
        twitter.addToOne(user, twitterUserIdMapping);

        // twit replied to twit
        Property in_reply_to_user_id = twitter.addLongProperty("in_reply_to_user_id").getProperty();
        twitter.addToMany(twitter, in_reply_to_user_id);

        new DaoGenerator().generateAll(schema, args[0]);
    }
}
