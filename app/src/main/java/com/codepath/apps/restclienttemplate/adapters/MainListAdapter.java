package com.codepath.apps.restclienttemplate.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.dao.Twitter;
import com.codepath.apps.restclienttemplate.dao.User;
import com.codepath.apps.restclienttemplate.helper.RoundedTransformation;
import com.codepath.apps.restclienttemplate.helper.Utils;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by jonaswu on 2015/8/10.
 */
public class MainListAdapter extends CustomizedAdapter {


    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView screenname;
        TextView text;
        TextView retweet_count;
        ImageView profile_image;
        TextView createTime;
        public TextView favorite_count;
    }

    public MainListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Twitter twitter = (Twitter) getItem(position);
        User user = twitter.getUser();
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.post, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.screenname = (TextView) convertView.findViewById(R.id.screenname);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.createTime = (TextView) convertView.findViewById(R.id.time);
            viewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
            viewHolder.retweet_count = (TextView) convertView.findViewById(R.id.retweet_count);
            viewHolder.favorite_count = (TextView) convertView.findViewById(R.id.favorite_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Date createTime = twitter.getCreated_at();
        viewHolder.createTime.setText(Utils.getBestTimeDiff(createTime));
        viewHolder.name.setText(Html.fromHtml(user.getName()));
        viewHolder.screenname.setText("@" + user.getScreen_name());
        viewHolder.text.setText(twitter.getText());
        viewHolder.text.setMovementMethod(null);
        viewHolder.retweet_count.setText(String.valueOf(twitter.getRetweet_count()));
        viewHolder.favorite_count.setText(String.valueOf(twitter.getFavorite_count()));
        Picasso.with(context)
                .load(user.getProfile_image_url())
                .transform(new RoundedTransformation(15, 1))
                .error(R.drawable.images)
                .placeholder(R.drawable.placeholder)
                .centerInside()
                .noFade()
                .fit()
                .into(viewHolder.profile_image);

        // Return the completed view to render on screen
        return convertView;
    }
}
