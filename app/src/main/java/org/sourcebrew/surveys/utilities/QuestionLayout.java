package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.HashMap;

/**
 * Created by John on 12/23/2017.
 */

public class QuestionLayout extends LinearLayout{

    private final JSONObject json;

    private TextView theQuestion;
    private FrameLayout theContents;
    private ResponseManager responseManager;

    public QuestionLayout(
            ResponseManager responseManager,
            ViewGroup target,
            JSONObject source) {
        this(responseManager, target, source, false);
    }

    public QuestionLayout(
            ResponseManager responseManager,
            ViewGroup target,
            JSONObject source,
            boolean clearContents) {

        super(target.getContext());
        this.responseManager = responseManager;
        this.json = source;

        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.question_layout, this, true);

        theQuestion = findViewById(R.id.theQuestion);
        theContents = findViewById(R.id.theContents);

        theQuestion.setText(SourceHelper.getString(json, "value", ""));
        if (clearContents) {
            target.removeAllViews();
        } else {
            int p = (int)(getResources().getDisplayMetrics().density * 4);
            setPadding(p, p+p, p, p+p);
        }
        target.addView(this);

        ContentLayout contentLayout = ContentLayout.createContentLayout(json, this);

    }


    public ResponseManager getResponseManager() {
        return responseManager;
    }

    public final FrameLayout getTheContents() {
        return theContents;
    }

    public final JSONObject getJSON() {
        return json;
    }


}
