package org.sourcebrew.surveys.utilities;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/23/2017.
 */

public class ContentSingleChoiceLayout  extends ContentLayout {

    private ArrayList<RadioButton> radioButtons;
    JSONObject jsonResult;
    private ArrayList<RadioButton> getRadioButtons() {
        if (radioButtons == null) {
            radioButtons = new ArrayList<RadioButton>();
        }
        return  radioButtons;
    }

    public ContentSingleChoiceLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();
    }

    @Override
    public void build() {
        try {
            jsonResult = new JSONObject("{}");
        } catch (JSONException e) {
        }
        JSONArray contents = SourceHelper.getArray(getJSON(), "contents");
        String groupID = SourceHelper.getString(getJSON(), "id", "");
        int index = 0;

        if (contents != null) {

            LinearLayout.LayoutParams ll = null;

            for (int i = 0; i < contents.length(); i++) {
                JSONArray group = SourceHelper.getArray(contents, i);
                if (group != null) {
                    LinearLayout xGroup = new LinearLayout(getContext());
                    xGroup.setOrientation(LinearLayout.VERTICAL);
                    if (ll == null) {
                        ll = (
                            new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                        );
                    }
                    setPadding(xGroup, 4, 0, 4, 0);

                    xGroup.setLayoutParams(ll);

                    for(int j = 0; j < group.length(); j++) {

                        JSONObject b = SourceHelper.getArrayObject(group, j);

                        if (b == null)
                            break;
                        String responceID = SourceHelper.getString(b, "responce_item", groupID + "." + String.valueOf(index++));

                        RadioButton r = new RadioButton(getContext());

                        r.setTextSize(getContext().getResources().getInteger(R.integer.survey_default_font_size));
                        r.setText(SourceHelper.getString(b, "value", "empty"));

                        xGroup.addView(r);
                        r.setTag(R.string.JSON_OBJECT,b);
                        r.setTag(R.string.JSON_ITEM_INDEX, responceID);
                        r.setOnClickListener(this);
                        getRadioButtons().add(r);
                    }

                    if (xGroup.getChildCount() != 0) {
                        getContentContainer().addView(xGroup);
                    }

                }

            }
        }

    }
    private Object updateJSON(String groupItemID, Object value, boolean isNewValue) {

        try {
            jsonResult.put("responce_item", groupItemID);
            jsonResult.put("id", SourceHelper.getString(getJSON(), "id", ""));
            jsonResult.put("value", SourceHelper.getString((JSONObject)value, "value", ""));
            onChangeMade(isNewValue, jsonResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    @Override
    public void onClick(View v) {

        for(RadioButton b: getRadioButtons()) {
            if (b == v) {
                updateJSON((String)b.getTag(R.string.JSON_ITEM_INDEX), b.getTag(R.string.JSON_OBJECT), true);
                //onChangeMade(true, b.getTag(R.string.JSON_OBJECT));
                continue;
            }
            if (b.isChecked())
                updateJSON((String)b.getTag(R.string.JSON_ITEM_INDEX), b.getTag(R.string.JSON_OBJECT), false);
                //onChangeMade(false, b.getTag(R.string.JSON_OBJECT));

            b.setChecked(false);

        }



    }

}
