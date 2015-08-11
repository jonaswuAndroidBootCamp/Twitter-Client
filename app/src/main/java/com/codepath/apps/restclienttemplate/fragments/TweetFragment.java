package com.codepath.apps.restclienttemplate.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.RestApplication;
import com.codepath.apps.restclienttemplate.TwitterRestClient;
import com.codepath.apps.restclienttemplate.dao.CurrentUser;
import com.codepath.apps.restclienttemplate.lib.MyJsonHttpResponseHandler;
import com.codepath.apps.restclienttemplate.lib.RoundedTransformation;
import com.codepath.apps.restclienttemplate.lib.SaveDataToDB;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;


public class TweetFragment extends DialogFragment implements TextView.OnKeyListener {


    private CurrentUser currentUser;
    private EventBus bus;
    private Long replyTo;
    private TextView screenname;
    private TextView name;
    private EditText body;
    private TextView length;
    private ImageView image;


    public static TweetFragment newInstanceAsReply(CurrentUser currentUser, EventBus bus, Long replyTo) {
        TweetFragment frag = new TweetFragment();
        frag.setCurrentUser(currentUser);
        frag.setBus(bus);
        frag.setReplyTo(replyTo);
        return frag;
    }

    public static TweetFragment newInstanceAsPostNewTweet(CurrentUser currentUser, EventBus bus) {
        TweetFragment frag = new TweetFragment();
        frag.setCurrentUser(currentUser);
        frag.setBus(bus);
        return frag;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_new_tweet, null);
        CurrentUser user = getCurrentUser();

        screenname = (TextView) view.findViewById(R.id.screenname);
        name = (TextView) view.findViewById(R.id.name);
        length = (TextView) view.findViewById(R.id.length);
        body = (EditText) view.findViewById(R.id.body);
        image = (ImageView) view.findViewById(R.id.profile_image);

        screenname.setText("@" + user.getScreen_name());
        name.setText(user.getName());
        body.setOnKeyListener(this);
        Picasso.with(getActivity())
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(image);

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
                .setTitle("Write down your feeling")
                .setPositiveButton("Tweet",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                TwitterRestClient client = RestApplication.getRestClient();
                                if (replyTo == null) {
                                    client.postTweet(body.getText().toString(),
                                            new MyJsonHttpResponseHandler(getActivity()) {
                                                @Override
                                                public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                    bus.post(new SaveDataToDB(true, null, null));
                                                }

                                                @Override
                                                public void errorCallBack() {

                                                }
                                            }
                                    );
                                } else {
                                    client.postReply(body.getText().toString(), getReplyTo(),
                                            new MyJsonHttpResponseHandler(getActivity()) {

                                                @Override
                                                public void successCallBack(int statusCode, Header[] headers, Object data) {
                                                    bus.post(new SaveDataToDB(true, null, null));
                                                }

                                                @Override
                                                public void errorCallBack() {

                                                }
                                            }
                                    );
                                }
                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                ).setView(view);

        return b.create();
    }


    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    public EventBus getBus() {
        return bus;
    }

    public void setBus(EventBus bus) {
        this.bus = bus;
    }

    public Long getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Long replyTo) {
        this.replyTo = replyTo;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        length.setText(String.valueOf(body.getText().toString().length()) + "/" + getActivity().getResources().getString(R.string.maxlenghtofatweet));
        return false;
    }
}