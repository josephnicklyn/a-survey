package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 12/27/2017.
 */

public class ResponceProgress extends LinearLayout {

    ResponseManager responseManager;

    TextView survey_progress_title;
    LinearLayout survey_progress_list;
    ProgressBar survey_progress_bar;
    LinearLayout survey_progress_button_bar;

    public ResponceProgress(Context context, ResponseManager responseManager, JSONObject json) {
        super(context);
        this.responseManager = responseManager;
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.template_response_progress, this, true);

        survey_progress_title = findViewById(R.id.survey_progress_title);
        survey_progress_list = findViewById(R.id.survey_progress_list);
        survey_progress_bar = findViewById(R.id.survey_progress_bar);
        survey_progress_button_bar = findViewById(R.id.survey_progress_button_bar);

        initialize(json);
        setTitle(responseManager.getSurveyTitle());


    }

    private void initialize(JSONObject json) {

        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        int p = (int)(getResources().getDisplayMetrics().density * 4);

        ll.setMargins(0, p, 0, p);
        JSONArray arr = SourceHelper.getArray(json, "questions");
        for(int v = 0; v < arr.length(); v++) {
            JSONObject obj = SourceHelper.getArrayObject(arr, v);
            String id = SourceHelper.getString(obj, "id", "");
            if (id.isEmpty()) {
                continue;
            }
            TextView tv = new TextView(getContext());
            tv.setText(id + ": " + SourceHelper.getString(obj, "value", "The Question"));
            tv.setLayoutParams(ll);
            survey_progress_list.addView(tv);
            listItems.put(id, tv);
        }

        survey_progress_bar.setMax(arr.length());


    }

    public void setTitle(String title) {
        survey_progress_title.setText(title);
    }

    private final HashMap<String, TextView> listItems = new HashMap<>();

    public void updateResponse(HashMap<String, ArrayList<JSONObject>> activeResults) {
        int completed = 0;
        for(String key: activeResults.keySet()) {
            ArrayList<JSONObject> list = activeResults.get(key);

            TextView tv = listItems.get(key);

            if (tv != null) {
                if (list.isEmpty()) {
                    tv.setTextColor(0xFFE00000);
                } else {
                    tv.setTextColor(0xFF008000);
                    completed++;
                }
            }
        }
        survey_progress_bar.setProgress(completed);
        if (survey_progress_bar.getMax() == completed) {
            survey_progress_button_bar.setVisibility(View.VISIBLE);
        } else {
            survey_progress_button_bar.setVisibility(View.GONE);
        }

    }
}
