package com.example.maprace.component;
import android.content.Context;
import android.util.AttributeSet;

import com.example.maprace.R;

public class FacebookButton extends ShareButton {

    public FacebookButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundDrawable(getContext().getDrawable(R.drawable.facebook));
    }

    protected void setURL() {
        this.text = "My best time is " + best_time + " and longest distance is " + longest_distance + " %23MapRace";
        this.url = "http://www.facebook.com/share.php?u=https://github.com/kashepavadan/cs446proj&quote=" + text;
    }
}