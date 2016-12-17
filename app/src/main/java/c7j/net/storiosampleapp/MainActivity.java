package c7j.net.storiosampleapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import java.util.ArrayList;
import java.util.List;

import c7j.net.storiosampleapp.model.Tweet;
import c7j.net.storiosampleapp.tables.TweetsTable;
import c7j.net.storiosampleapp.ui.TweetsAdapter;

import c7j.net.storiosampleapp.model.TweetSQLiteTypeMapping;

import static c7j.net.storiosampleapp.tables.TweetsTable.QUERY_ALL;


public class MainActivity extends AppCompatActivity {

    private StorIOSQLite storIOSQLite;

    private RecyclerView recyclerView;
    private TweetsAdapter tweetsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DbOpenHelper(this))
                .addTypeMapping(Tweet.class, new TweetSQLiteTypeMapping())
                .build();

        tweetsAdapter = new TweetsAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.tweets_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(tweetsAdapter);

        deleteAll();
        addTweets();
        updateQuery();
//        queryWithWhere();
//        deleteWhere();
    }


    private void addTweets() {
        final List<Tweet> tweets = new ArrayList<Tweet>();

        tweets.add(Tweet.newTweet( 1L, "artem_zin", "Checkout StorIO — modern API for SQLiteDatabase & ContentResolver"));
        tweets.add(Tweet.newTweet( 2L, "HackerNews", "It's revolution! Dolphins can write news on HackerNews with our new app!"));
        tweets.add(Tweet.newTweet( 3L, "AndroidDevReddit", "Awesome library — StorIO"));
        tweets.add(Tweet.newTweet( 4L, "Facebook", "Facebook community in Twitter is more popular than Facebook community in Facebook and Instagram!"));
        tweets.add(Tweet.newTweet( 5L, "Google", "Android be together not the same: AOSP, AOSP + Google Apps, Samsung Android"));
        tweets.add(Tweet.newTweet( 6L, "Reddit", "Now we can send funny gifs directly into your brain via Oculus Rift app!"));
        tweets.add(Tweet.newTweet( 7L, "ElonMusk", "Tesla Model S OTA update with Android Auto 7.2, fixes for memory leaks"));
        tweets.add(Tweet.newTweet( 8L, "AndroidWeekly", "Special issue #1: StorIO — forget about SQLiteDatabase, ContentResolver APIs, ORMs suck!"));
        tweets.add(Tweet.newTweet( 9L, "Apple", "Yosemite update: fixes for Wifi issues, yosemite-wifi-patch#142"));

        try {
            PutResults<Tweet> results = storIOSQLite
                    .put()
                    .objects(tweets)
                    .prepare()
                    .executeAsBlocking();

            Toast.makeText(this, getResources().getQuantityString(R.plurals.tweets_inserted, results.results().size()), Toast.LENGTH_LONG).show();

            List<Tweet> receivedTweets = storIOSQLite
                    .get()
                    .listOfObjects(Tweet.class)
                    .withQuery(QUERY_ALL)
                    .prepare()
                    .executeAsBlocking();

            Toast.makeText(this, getResources().getQuantityString(R.plurals.tweets_loaded, receivedTweets.size()), Toast.LENGTH_LONG).show();

//            tweetsAdapter.setTweets(receivedTweets);
        } catch (StorIOException e) {
            Toast.makeText(this, R.string.tweets_add_error_toast, Toast.LENGTH_LONG).show();
        }
    }



    private void queryWithWhere() {

        Query QUERY_WITHWHERE = Query.builder()
                .table(TweetsTable.TABLE)
                .where(TweetsTable.TABLE + "." + TweetsTable.COLUMN_AUTHOR + " = ?")
                .whereArgs("Apple")
                .build();

        List<Tweet> receivedTweets = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(QUERY_WITHWHERE)
                .prepare()
                .executeAsBlocking();

        Toast.makeText(this, getResources().getQuantityString(R.plurals.tweets_loaded, receivedTweets.size()), Toast.LENGTH_LONG).show();

        tweetsAdapter.setTweets(receivedTweets);
    }


    private void deleteAll() {
        DeleteQuery DELETEQUERY_ALL = DeleteQuery.builder()
                .table(TweetsTable.TABLE)
                .build();

        storIOSQLite.delete()
                .byQuery(DELETEQUERY_ALL)
                .prepare()
                .executeAsBlocking();
    }

    private void deleteWhere() {

        DeleteQuery DELETEQUERY_WITHWHERE = DeleteQuery.builder()
                .table(TweetsTable.TABLE)
                .where(TweetsTable.TABLE + "." + TweetsTable.COLUMN_AUTHOR + " = ?")
                .whereArgs("Apple")
                .build();

        storIOSQLite.delete()
                .byQuery(DELETEQUERY_WITHWHERE)
                .prepare()
                .executeAsBlocking();

        List<Tweet> receivedTweets = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        Toast.makeText(this, getResources().getQuantityString(
                R.plurals.tweets_loaded, receivedTweets.size()), Toast.LENGTH_LONG).show();

        tweetsAdapter.setTweets(receivedTweets);

    }

    private void updateQuery() {
        Query QUERY_TWEETS_TO_UPDATE = Query.builder()
                .table(TweetsTable.TABLE)
                .where(TweetsTable.TABLE + "." + TweetsTable.COLUMN_ID + " < ?")
                .whereArgs("5")
                .build();

        List<Tweet> receivedTweets = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(QUERY_TWEETS_TO_UPDATE)
                .prepare()
                .executeAsBlocking();

        List<Tweet> receivedTweetsUpdated = new ArrayList<>();

        for ( Tweet tweet : receivedTweets ) {
            receivedTweetsUpdated.add(Tweet.newTweet(
                    tweet.id(), tweet.author(), "modified content"));
        }

        storIOSQLite
                .put()
                .objects(receivedTweetsUpdated)
                .prepare()
                .executeAsBlocking();

        List<Tweet> receivedTweetsRead = storIOSQLite
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        Toast.makeText(this, getResources().getQuantityString(
                R.plurals.tweets_loaded, receivedTweetsRead.size()), Toast.LENGTH_LONG).show();

        tweetsAdapter.setTweets(receivedTweetsRead);

    }

}
