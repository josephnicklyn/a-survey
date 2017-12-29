package org.sourcebrew.surveys.utilities;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/24/2017.
 */

public class ContentRangeSelectLayout  extends ContentLayout {

    JSONObject jsonResult;
    public ContentRangeSelectLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();
    }

    @Override
    public void build() {
        try {
            jsonResult = new JSONObject("{}");
        } catch (JSONException e) {
        }
        getRangeSelect().setFromJSON(getJSON());

        getRangeSelect().setOnRangeSelectChange(new RangeSelect.RangeSelectInterface() {
            @Override
            public void itemSelected(View oldView, View newView, String oldValue, String newValue) {
                if (oldView != null) {
                    Object sOld = oldView.getTag(R.string.JSON_OBJECT);
                    if (sOld != null) {
                        //onChangeMade(false, sOld);
                        updateJSON(oldValue, false);
                    }
                }
                if (newView != null) {
                    Object sNew = newView.getTag(R.string.JSON_OBJECT);
                    if (sNew != null) {
                        updateJSON(newValue, true);
                        //onChangeMade(true, sNew);
                    }
                }
            }
        });
    }

    private Object updateJSON(String value, boolean isNewValue) {

        try {
            String id = SourceHelper.getString(getJSON(), "id", "");
            jsonResult.put("id", id);
            jsonResult.put("responce_item", id + "." + value);
            jsonResult.put("value", value);
            onChangeMade(isNewValue, jsonResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    private RangeSelect getRangeSelect() {
        return (RangeSelect)getContentContainer();
    }

    @Override
    public void onClick(View v) {

    }

}