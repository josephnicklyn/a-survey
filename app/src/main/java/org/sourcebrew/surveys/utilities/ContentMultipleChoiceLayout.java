package org.sourcebrew.surveys.utilities;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by John on 12/23/2017.
 */

public class ContentMultipleChoiceLayout  extends ContentLayout {

    private ArrayList<CheckBox> checkBoxes;
    private JSONObject jsonResult;

    private int max_select = 0;

    private ArrayList<CheckBox> getCheckBoxes() {
        if (checkBoxes == null) {
            checkBoxes = new ArrayList<CheckBox>();
        }
        return  checkBoxes;
    }

    public ContentMultipleChoiceLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();
    }
    JSONArray contents;
    @Override
    public void build() {
        try {
            jsonResult = new JSONObject("{}");
        } catch (JSONException e) {
        }
        contents = SourceHelper.getArray(getJSON(), "contents");

        max_select = SourceHelper.getInt(getJSON(), "max_select", 0);
        if (contents != null) {
            String groupID = SourceHelper.getString(getJSON(), "id", "");
            LinearLayout.LayoutParams ll = null;
            int index = 0;
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
                        CheckBox r = new CheckBox(getContext());
                        r.setTag(R.string.JSON_OBJECT,b);
                        r.setTag(R.string.JSON_ITEM_INDEX, responceID);
                        r.setTextSize(getContext().getResources().getInteger(R.integer.survey_default_font_size));
                        r.setText(SourceHelper.getString(b, "value", "empty"));
                        xGroup.addView(r);
                        r.setOnClickListener(this);
                        addToToggleGroups(r, SourceHelper.getString(b, "toggle_group", ""));
                    }

                    if (xGroup.getChildCount() != 0) {
                        getContentContainer().addView(xGroup);
                    }
                }
            }
        }
    }

    private void addToToggleGroups(CheckBox r, String toggle_group) {
        if (!toggle_group.isEmpty())
            r.setTag(R.string.TOGGLE_GROUP, toggle_group);
        getCheckBoxes().add(r);
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
    public void onClick(View buttonView) {
        //if (buttonView.getTag(R.string.TOGGLE_GROUP) != null)
         {
            String tgName = (buttonView.getTag(R.string.TOGGLE_GROUP) != null?
                (String)buttonView.getTag(R.string.TOGGLE_GROUP):
                "");
            for(CheckBox b: getCheckBoxes()) {
                if (b == buttonView) {
                    updateJSON((String)b.getTag(R.string.JSON_ITEM_INDEX), b.getTag(R.string.JSON_OBJECT), b.isChecked());
                    continue;
                }
                if (b.getTag(R.string.TOGGLE_GROUP) != null) {
                    String btgName = (String) b.getTag(R.string.TOGGLE_GROUP);
                    if (tgName.equalsIgnoreCase(btgName)) {
                        if (b.isChecked()) {
                            updateJSON((String)b.getTag(R.string.JSON_ITEM_INDEX), b.getTag(R.string.JSON_OBJECT), false);
                        }
                        b.setChecked(false);
                    }
                }
            }
        }
        if (!canSelect((CompoundButton)buttonView)) {
            Toast.makeText(
                    getContext(),
                    "Limited to [" + max_select + "] items.",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private boolean canSelect(CompoundButton v) {
        boolean result = true;
        int selected = 0;
        if (max_select > 0) {
            for(CheckBox b: getCheckBoxes()) {
                if (b.isChecked()) {
                    selected++;
                }
            }
            result = (selected<=max_select);
            if (!result) {
                v.setChecked(false);
            }
        }
        return result;
    }
}