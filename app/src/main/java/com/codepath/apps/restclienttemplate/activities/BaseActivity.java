package com.codepath.apps.restclienttemplate.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import de.greenrobot.event.EventBus;


/**
 * Created by jonaswu on 2015/7/28.
 */
public abstract class BaseActivity extends ActionBarActivity {

    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        eventBus = new EventBus();
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        eventBus.register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        eventBus.unregister(this);
        super.onPause();
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}
