package com.example.maprace.component;

import android.content.Context;
import android.util.AttributeSet;

import com.example.maprace.R;

public class TwitterButton extends ShareButton {

    public TwitterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setBackgroundDrawable(getContext().getDrawable(R.drawable.twitter));
    }

    protected void setURL() {
        this.text = "My best time is " + this.best_time + " and longest distance is " + this.longest_distance + " %23MapRace";
        this.url = "https://twitter.com/intent/tweet?text=" + text;

    }

}
