package org.sourcebrew.surveys.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/25/2017.
 */

public class ContentTextLayout extends ContentLayout {
    private JSONObject jsonResult;
    private TextView textView;
    String responseID = "";
    public ContentTextLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();
    }

    @Override
    protected void setValue(String value) {
        textView.setText(value);
    }

    @Override
    public void build() {
        try {
            jsonResult = new JSONObject("{}");
        } catch (JSONException e) {
        }
        String inputType = SourceHelper.getString(getJSON(), "input_type", "text");
        String hint = SourceHelper.getString(getJSON(), "hint", inputType);

        String thisID = SourceHelper.getString(getJSON(), "id", "") + ".0";

        responseID = SourceHelper.getString(getJSON(), "responce_id", thisID);

        switch (inputType.toLowerCase()) {
            case "time_picker":
            case "time":
            case "date_picker":
            case "date":
                textView = new TextView(getContext());
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                int p = (int)(getContext().getResources().getDisplayMetrics().density*6);
                textView.setPadding(p, p, p, p);
                textView.setHint(hint);
                textView.setBackgroundResource(R.drawable.border_for_view);
                break;
            default:
                textView = new EditText(getContext());
                textView.setInputType(inputTypeMask(inputType));

                int multiLine = SourceHelper.getInt(getJSON(), "lines", 0);
                if (multiLine > 0) {
                    if (multiLine < 4) multiLine = 4;
                    textView.setMaxLines(multiLine);
                    textView.setMinLines(multiLine);
                    textView.setGravity(Gravity.START);
                }

                break;
        }
        textView.setTextSize(getContext().getResources().getInteger(R.integer.survey_default_font_size));

        switch (inputType.toLowerCase()) {
            case "time_picker":
            case "time":
                showTimePicker(textView);
                break;
            case "date_picker":
            case "date":
                showDatePicker(textView);
                break;
        }
        getContentContainer().addView(textView);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateJSON(s.toString(), s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private Object updateJSON(String value, boolean isNewValue) {

        try {
            jsonResult.put("responce_item", responseID);
            jsonResult.put("id", SourceHelper.getString(getJSON(), "id", ""));
            jsonResult.put("value", value);
            onChangeMade(isNewValue, jsonResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    @Override
    public void onClick(View v) {


    }


}