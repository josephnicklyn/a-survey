package org.sourcebrew.surveys.utilities;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;

/**
 * <p>
 * A <code>ContentLayout</code> is an abstract for maintaining view groups
 * for various types of survey input controls.
 *
 *
 * Created by John on 12/23/2017.
 */

public abstract class ContentLayout implements View.OnClickListener {

    private final QuestionLayout questionLayout;
    private ViewGroup contentContainer;

    public ContentLayout(QuestionLayout questionLayout) {

        this.questionLayout = questionLayout;
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );

        switch (SourceHelper.getString(questionLayout.getJSON(), "type", "text")) {
            case "range_select":
                contentContainer = new RangeSelect(questionLayout.getContext());
                break;
            case "rating_scale":
                contentContainer = new OptionsTableLayout(questionLayout.getContext());
                break;
            default:
              contentContainer = new FlowLayout(questionLayout.getContext());
              break;
        }

        contentContainer.setLayoutParams(ll);

        questionLayout.getTheContents().addView(contentContainer);

    }

    public abstract void build();

    protected final FrameLayout getTheContents() {
        return questionLayout.getTheContents();
    }

    protected final JSONObject getJSON() {
        return questionLayout.getJSON();
    }

    public final Context getContext() {
        return questionLayout.getContext();

    }


    protected static void setPadding(View v, int l, int t, int r, int b) {

        float d = Resources.getSystem().getDisplayMetrics().density;

        l*=d;
        t*=d;
        r*=d;
        b*=d;

        v.setPadding(l, t, r, b);

    }


    protected ViewGroup getContentContainer() {
        return contentContainer;
    }

    public static ContentLayout createContentLayout(JSONObject json, QuestionLayout questionLayout) {
        ContentLayout layout = null;
        try {
            Log.e("JSON", SourceHelper.getString(json, "type", "not found"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (SourceHelper.getString(json, "type", "text")) {
            case "range_select":
                layout = new ContentRangeSelectLayout(questionLayout);
                break;
            case "single_choice":
                layout = new ContentSingleChoiceLayout(questionLayout);
                break;
            case "multiple_choice":
                layout = new ContentMultipleChoiceLayout(questionLayout);
                break;
            case "rating_scale":
                layout = new ContentRatingScaleLayout(questionLayout);
                break;
            case "yes_no":
                layout = new ContentYesNoMaybeSoLayout(questionLayout);
                break;
            case "text":
                layout = new ContentTextLayout(questionLayout);
                break;
        }
        return layout;
    }

    public static int inputTypeMask(String value) {
        value = value.toLowerCase();
        switch (value) {
            case "phone":
                return InputType.TYPE_CLASS_PHONE;
            case "date":
            case "date_picker":
                return InputType.TYPE_CLASS_DATETIME
                        | InputType.TYPE_DATETIME_VARIATION_DATE;
            case "time":
            case "time_picker":
                return InputType.TYPE_CLASS_DATETIME
                        | InputType.TYPE_DATETIME_VARIATION_TIME;
            case "email":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            case "auto_complete":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
            case "password":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
            case "int":
            case "integer":
            case "number":
                return InputType.TYPE_CLASS_NUMBER;
            case "multiline":
                return InputType.TYPE_CLASS_TEXT |  InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }

    /**
     * Shows a date picker
     *
     * @param view the view whose click will be listened for
     */
    protected void showDatePicker(View view) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                ((DatePickerFragment)newFragment).setOnCallback(
                        new SimpleMessageInterface() {
                            @Override
                            public void OnSimpleMessage(String value) {
                                setValue(value);
                            }
                        }
                );
                Activity activity = (Activity)getContext();
                newFragment.show(activity.getFragmentManager(),"DatePicker");
            }
        });

    }

    /**
     * Shows a time picker
     *
     * @param view the view whose click will be listened for
     */
    protected void showTimePicker(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();

                ((TimePickerFragment)newFragment).setOnCallback(
                        new SimpleMessageInterface() {
                            @Override
                            public void OnSimpleMessage(String value) {
                                setValue(value);
                            }
                        }
                );
                Activity activity = (Activity)getContext();
                newFragment.show(activity.getFragmentManager(),"TimePicker");
            }
        });
    }

    protected void setValue(String value) {}

    protected  void onChangeMade(boolean selected, Object source) {
        if (source != null)
            questionLayout.getResponseManager().onQuestionValueChanged(questionLayout, selected, (JSONObject)source);
    }

}
