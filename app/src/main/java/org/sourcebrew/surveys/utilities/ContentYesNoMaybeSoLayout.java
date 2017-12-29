package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/25/2017.
 */

public class ContentYesNoMaybeSoLayout extends ContentLayout {

    private final ArrayList<ButtonStyles> buttonsList = new ArrayList<>();
    private Drawable defaultButtonStyle;
    JSONObject jsonResult;
    private TextView title;

    View selectedItem = null;
    int imgChecked, imgUnchecked;
    float cornerRadius = 0;

    public ContentYesNoMaybeSoLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();
    }

    @Override
    public void build() {

        try {
            jsonResult = new JSONObject("{}");
        } catch (JSONException e) {
        }


        defaultButtonStyle = getButtonBorder("FFEAEAEF");

        JSONArray contents = SourceHelper.getArray(getJSON(), "contents");
        JSONArray buttons = SourceHelper.getArray(getJSON(), "buttons");

        cornerRadius = (getContext().getResources().getDisplayMetrics().density*2);

        if (buttons != null) {
            for(int i = 0; i < buttons.length(); i++) {
                JSONObject button = SourceHelper.getArrayObject(buttons, i);
                String value = SourceHelper.getString(button, "value", "");
                String color = SourceHelper.getString(button, "color", "ffeaeaea");
                buttonsList.add(new ButtonStyles(value, color));
            }
        }

        if (contents != null) {

            int index = 0;
            String groupID = SourceHelper.getString(getJSON(), "id", "");


            LinearLayout.LayoutParams ll = null;

            for(int i = 0; i < contents.length(); i++) {

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
                        JSONObject bItem = SourceHelper.getArrayObject(group, j);

                        String responceID = SourceHelper.getString(bItem, "responce_item", groupID + "." + String.valueOf(index++));


                        String value = SourceHelper.getString(bItem, "value", "");
                        String inputType = SourceHelper.getString(bItem, "input_type", "");

                        if (bItem != null) {
                            xGroup.addView(new ButtonBar(getContext(), value, inputType, responceID));
                        }
                    }

                    if (xGroup.getChildCount() != 0) {
                        getContentContainer().addView(xGroup);
                    }

                } else {
                    Log.e("GROUP", "ERROR");
                }
            }
        } else {
            Log.e("GROUP", "CONTENTS ERROR");
        }

    }

    @Override
    public void onClick(View v) {

    }

    private Drawable getButtonBorder(String colorValue) {
        long color = 0xFFEAEAEA;
        if (!colorValue.isEmpty()) {

            try { color = Long.parseLong(colorValue, 16); } catch(NumberFormatException nexp) {}
        }

        GradientDrawable border = new GradientDrawable();
        border.setColor((int)color);
        border.setCornerRadius(cornerRadius);
        border.setStroke(1, 0xFF606060);

        return border;
    }

    private class ButtonBar extends LinearLayout implements View.OnClickListener {

        private ArrayList<TextView> buttonBarButtons = new ArrayList<>();

        private View selectedButton;
        private TextView textViewTitle;
        private String responceID = "";
        private boolean isEditable = false;
        public ButtonBar(
                Context context,
                String title,
                String inputType,
                String responseID) {

            super(context);

            int v = (int)(getContext().getResources().getDisplayMetrics().density*4);
            int h = (int)(getContext().getResources().getDisplayMetrics().density*1);
            int p = (int)(getContext().getResources().getDisplayMetrics().density*2);

            this.responceID = responseID;

            setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams l2 = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            l2.setMargins(p, p, p, p);
            setLayoutParams(l2);

            setPadding(p, p, p, p);
            setBackgroundResource(R.drawable.bottom_line);

            l2 = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            l2.weight = 1;
            l2.setMargins(h, v, h, v);


            if (!inputType.isEmpty()) {
                textViewTitle = new EditText(getContext());
                textViewTitle.setHint(title);
                textViewTitle.setInputType(inputTypeMask(inputType));
                isEditable = true;
                if (inputType.contains("multiline")) {
                    textViewTitle.setMaxLines(2);
                    textViewTitle.setMinLines(2);
                }
                textViewTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateJSON(responceID, s.toString(), s.length() != 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            } else {
                textViewTitle = new TextView(getContext());
                textViewTitle.setText(title);
                textViewTitle.setOnClickListener(ButtonBar.this);
            }

            textViewTitle.setLayoutParams(l2);
            textViewTitle.setPadding(v, v, v, v);
            textViewTitle.setSingleLine(false);
            textViewTitle.setTextSize(getContext().getResources().getInteger(R.integer.survey_default_font_size));
            addView(textViewTitle);


            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams (
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );

            ll.setMargins(p, v, p, v);
            ll.weight = 0;

            if (isEditable) {
                TextView tButton = new TextView(getContext());
                tButton.setText("clear");
                tButton.setBackground(getButtonBorder("FFbcd9e0"));
                tButton.setGravity(Gravity.CENTER);
                tButton.setMinWidth(v * 8);
                tButton.setLayoutParams(ll);
                tButton.setPadding(v, v, v, v);
                tButton.setTextSize(16);
                tButton.setOnClickListener(ButtonBar.this);
                addView(tButton);
                return;
            }

            for(ButtonStyles bs: buttonsList) {
                TextView tButton = new TextView(getContext());
                tButton.setText(bs.value);
                tButton.setBackground(defaultButtonStyle);
                tButton.setGravity(Gravity.CENTER);
                tButton.setMinWidth(v * 8);
                tButton.setLayoutParams(ll);
                tButton.setPadding(v, v, v, v);
                addView(tButton);
                tButton.setTag(R.string.YES_NO_TOGGLE_STYLE, bs);
                buttonBarButtons.add(tButton);
                tButton.setOnClickListener(ButtonBar.this);
                tButton.setTextSize(16);
            }
        }

        @Override
        public void onClick(View v) {
            if (isEditable) {
                textViewTitle.setText("");
                return;
            }
            if (textViewTitle == v) {
                int index = buttonBarButtons.indexOf(selectedButton);
                if (index == -1) {
                    index = 0;
                } else {
                    index++;
                }
                if (index >= buttonBarButtons.size()) {
                    index = -1;
                    setSelectedItem(null);
                } else {
                    setSelectedItem(buttonBarButtons.get(index));
                }
            } else {
                setSelectedItem(v);
            }
        }

        public void setSelectedItem(View v) {
            if (selectedButton != null) {
                if (v == selectedButton)
                    return;
            }
            for (TextView t : buttonBarButtons) {
                ButtonStyles bs = (ButtonStyles) t.getTag(R.string.YES_NO_TOGGLE_STYLE);
                if (t == v) {
                    t.setBackground(bs.buttonStyle);
                    updateJSON(responceID, textViewTitle.getText().toString(), t.getText().toString(), true);
                } else if (t == selectedButton) {
                    t.setBackground(defaultButtonStyle);
                    updateJSON(responceID, textViewTitle.getText().toString(), t.getText().toString(), false);
                }
            }
            if (!isEditable) {
                if (v != null) {
                    textViewTitle.setTypeface(null, Typeface.BOLD);
                } else {
                    textViewTitle.setTypeface(null, Typeface.NORMAL);
                }
            }
            selectedButton = v;
        }
    }

    private class ButtonStyles {
        Drawable buttonStyle;
        String value;
        public ButtonStyles(
            String value,
            String color
        ) {
            this.buttonStyle = getButtonBorder(color);
            this.value = value;
        }
    }

    private Object updateJSON(String responseID, String value, String buttonValue, boolean isNewValue) {

        try {
            jsonResult.put("responce_item", responseID);
            jsonResult.put("id", SourceHelper.getString(getJSON(), "id", ""));
            jsonResult.put("value", value);
            jsonResult.put("onButton", buttonValue);
            onChangeMade(isNewValue, jsonResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    private Object updateJSON(String responseID, String value, boolean isNewValue) {

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
}
