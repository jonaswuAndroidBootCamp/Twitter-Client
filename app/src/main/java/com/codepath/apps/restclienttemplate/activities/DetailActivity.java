package com.codepath.apps.restclienttemplate.activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.adapters.CustomizedAdapter;
import com.codepath.apps.restclienttemplate.adapters.ReplyAdapter;
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
import com.codepath.apps.restclienttemplate.helper.RoundedTransformation;
import com.codepath.apps.restclienttemplate.helper.SaveDataToDB;
import com.codepath.apps.restclienttemplate.helper.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by jonaswu on 2015/8/10.
 */
public class DetailActivity extends BaseActivity {
    private TwitterDao twitterDao;
    private UserDao userDao;
    private CurrentUserDao currentUserDao;
    private MediaDao mediaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Bundle bundle = getIntent().getExtras();
        Long id = bundle.getLong("id");
        initDb();
        createView(id);
    }

    private void createView(Long id) {
        Query query = twitterDao.queryBuilder().where(TwitterDao.Properties.Id.eq(id)).build();
        final Twitter twitter = (Twitter) query.list().get(0);
        final User user = twitter.getUser();


        TextView text = (TextView) findViewById(R.id.text);
        TextView screenname = (TextView) findViewById(R.id.screenname);
        TextView name = (TextView) findViewById(R.id.name);
        TextView time = (TextView) findViewById(R.id.time);
        ImageView image = (ImageView) findViewById(R.id.profile_image);
        ImageView reply = (ImageView) findViewById(R.id.reply);
        ImageView large_image = (ImageView) findViewById(R.id.large_image);

        text.setText(twitter.getText());
        screenname.setText("@" + user.getScreen_name());
        name.setText(user.getName());
        time.setText(Utils.displayTimeToTweet(twitter.getCreated_at()));


        Query queryReply = twitterDao.queryBuilder().where(TwitterDao.Properties.In_reply_to_user_id.eq(twitter.getId())).build();
        List<Twitter> replyTwitters = (List<Twitter>) queryReply.list();
        if (replyTwitters.size() > 0) {
            ReplyAdapter replyAdapter = new ReplyAdapter(this);
            for (Twitter replyTwitter : replyTwitters) {
                replyAdapter.addItem(replyTwitter);
            }
            ListView listView = (ListView) findViewById(R.id.replies);
            listView.setAdapter(replyAdapter);
            LinearLayout ll = (LinearLayout) findViewById(R.id.replies_section);
            ll.setVisibility(View.VISIBLE);
        }

        Picasso.with(this)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(image);

        if (twitter.getMediaList().size() > 0) {
            Media media = twitter.getMediaList().get(0);
            large_image.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(media.getMedia_url())
                    .error(R.drawable.images)
                    .placeholder(R.drawable.placeholder)
                    .centerInside()
                    .noFade()
                    .resize(600, 600)
                    .into(large_image);

        }

        reply.setOnClickListener(new View.OnClickListener()

                                 {
                                     @Override
                                     public void onClick(View v) {
                                         showReplyDialog(twitter.getId());
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

    private void showReplyDialog(Long replyTo) {
        FragmentManager fm = getSupportFragmentManager();
        Query query = currentUserDao.queryBuilder().where(UserDao.Properties.Id.eq(Utils.getCurrentUserId())).build();
        CurrentUser user = (CurrentUser) query.list().get(0);
        TweetFragment alertDialog = TweetFragment.newInstanceAsReply(user, this.getEventBus(), replyTo);
        alertDialog.show(fm, "filter");
    }

    public void onEventMainThread(SaveDataToDB saveDataToDB) {

    }

}
