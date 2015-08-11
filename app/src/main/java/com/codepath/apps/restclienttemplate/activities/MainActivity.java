package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.adapters.MainListAdapter;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.dao.CurrentUserDao;
import com.codepath.apps.restclienttemplate.dao.DaoMaster;
import com.codepath.apps.restclienttemplate.dao.DaoSession;
import com.codepath.apps.restclienttemplate.dao.Media;
import com.codepath.apps.restclienttemplate.dao.MediaDao;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.TwitterDao;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.dao.UserDao;
import com.codepath.apps.restclienttemplate.fragments.TweetFragment;
import com.codepath.apps.restclienttemplate.lib.EndlessScrollListener;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.codepath.apps.restclienttemplate.lib.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/9.
 */
public class MainActivity extends BaseActivity {


    private SwipeRefreshLayout swipeContainer;
    private ListView mainListView;
    private TwitterDao twitterDao;
    private boolean fetchOnRefresh = true;
    private UserDao userDao;
    private MainListAdapter adapter;
    private CurrentUserDao currentUserDao;
    private MediaDao mediaDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDb();
        initView();
        initData();
        getUserCredential();
        updateListView(null);
    }

    private void getUserCredential() {
        TwitterRestClient client = RestApplication.getRestClient();
        client.getcredentials(
                new MyJsonHttpResponseHandler(this) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        swipeContainer.setRefreshing(false);
                        try {
                            JSONObject userJSON = (JSONObject) data;
                            Utils.setCurrentUserId(userJSON.getLong("id"));
                            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                            CurrentUser currentUser = gson.fromJson(userJSON.toString(), CurrentUser.class);
                            currentUserDao.insert(currentUser);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void errorCallBack() {
                        swipeContainer.setRefreshing(false);
                    }
                }
        );
    }

    private void initDb() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "greendao", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        twitterDao = daoSession.getTwitterDao();
        userDao = daoSession.getUserDao();
        currentUserDao = daoSession.getCurrentUserDao();
        mediaDao = daoSession.getMediaDao();
    }

    private void initView() {
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mainListView = (ListView) findViewById(R.id.mainlist);

        adapter = new MainListAdapter(this);
        mainListView.setAdapter(adapter);

        mainListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Twitter lastItem = (Twitter) adapter.getLastItem();
                Long maxId = lastItem.getId();
                fetchData(maxId);
            }
        });

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Twitter twitter = (Twitter) adapter.getItem(position);
                Intent intent = new Intent(MainActivity.this
                        , DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("id", twitter.getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initData() {
        // SomeActivity.java
        if (fetchOnRefresh) {
            TwitterRestClient client = RestApplication.getRestClient();
            client.getHomeTimeline(null,
                    new MyJsonHttpResponseHandler(this) {
                        @Override
                        public void successCallBack(int statusCode, Header[] headers, Object data) {
                            swipeContainer.setRefreshing(false);
                            processDataToDB(true, (JSONArray) data, null);
                        }

                        @Override
                        public void errorCallBack() {
                            swipeContainer.setRefreshing(false);
                        }
                    }
            );
        } else {
            processDataToDB(true, null, null);
        }
    }


    private void fetchData(final Long maxId) {
        TwitterRestClient client = RestApplication.getRestClient();
        client.getHomeTimeline(maxId,
                new MyJsonHttpResponseHandler(this) {
                    @Override
                    public void successCallBack(int statusCode, Header[] headers, Object data) {
                        swipeContainer.setRefreshing(false);
                        processDataToDB(false, (JSONArray) data, maxId);
                    }

                    @Override
                    public void errorCallBack() {
                        swipeContainer.setRefreshing(false);
                    }
                }
        );

    }

    public void onEventMainThread(SaveDataToDB saveDataToDB) {
        initData();
    }

    private void processDataToDB(boolean clean, JSONArray data, Long lessThen) {
        if (data != null) {
            if (clean) {
                adapter.deleteAll();
                twitterDao.deleteAll();
                userDao.deleteAll();
            }
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject post = data.getJSONObject(i);
                    JSONObject userJSON = post.getJSONObject("user");
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
                    Twitter twitterPost = gson.fromJson(Utils.twitterPostPreProcessForDao(post).toString(), Twitter.class);
                    User user = gson.fromJson(userJSON.toString(), User.class);
                    userDao.insert(user);

                    // user already contain id after insert to db
                    twitterPost.setUser(user);


                    try {
                        if (post.getLong("in_reply_to_status_id") != 0) {
                            Log.e("in_reply_to_status_id", post.getString("in_reply_to_status_id"));
                            twitterPost.setIn_reply_to_user_id(post.getLong("in_reply_to_status_id"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    twitterDao.insert(twitterPost);

                    try {
                        JSONArray medias = post.getJSONObject("entities").getJSONArray("media");
                        for (int j = 0; j < medias.length(); j++) {
                            Media media = gson.fromJson(medias.getJSONObject(j).toString(), Media.class);
                            media.setInTweets(twitterPost.getInternalId());
                            mediaDao.insert(media);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        updateListView(lessThen);

    }

    private void updateListView(Long lessThen) {
        Query query;
        if (lessThen != null) {
            query = twitterDao.queryBuilder().where(TwitterDao.Properties.Id.lt(lessThen))
                    .build();
        } else {
            query = twitterDao.queryBuilder()
                    .build();
        }
        List<Twitter> list = query.list();
        for (Twitter twitter : list) {
            adapter.addItem(twitter);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.write) {
            showPostDialog();
            return true;
        }

        if (id == R.id.logout) {
            TwitterRestClient client = RestApplication.getRestClient();
            client.clearAccessToken();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    private void showPostDialog() {
        if (Utils.getCurrentUserId() != null) {
            FragmentManager fm = getSupportFragmentManager();
            Query query = currentUserDao.queryBuilder().where(UserDao.Properties.Id.eq(Utils.getCurrentUserId())).build();
            List<CurrentUser> currentUsers = query.list();
            if (currentUsers.size() > 0) {
                CurrentUser user = currentUsers.get(0);
                TweetFragment alertDialog = TweetFragment.newInstanceAsPostNewTweet(user, this.getEventBus());
                alertDialog.show(fm, "filter");
            }
        } else {
            getUserCredential();
        }
    }
}
