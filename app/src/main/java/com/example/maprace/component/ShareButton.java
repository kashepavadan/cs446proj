package com.example.maprace.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public abstract class ShareButton extends androidx.appcompat.widget.AppCompatButton {

    protected String best_time = "";
    protected String longest_distance = "";
    protected String text = "";
    protected String url = "";

    public ShareButton(final Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(url)) {
                    Log.i("Share Button", "URL is empty");
                    return;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
        this.setURL();
    }

    public void setBestTime(String best_time) {
        this.best_time = best_time;
        this.setURL();
    }

    public void setLongestDistance(String longest_distance) {
        this.longest_distance = longest_distance;
        this.setURL();
    }

    protected abstract void setURL();

}

