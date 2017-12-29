package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/23/2017.
 */

public class OptionsTableRowLayout extends LinearLayout {

    private int rowIndex = 0;

    public int childMinWidth = 0;
    public int titleWidth = 0;
    public int padding = 0;
    public TextView rowTitle;
    public int borderR = 0;
    int wd = 120;

    public OptionsTableRowLayout(Context context, int rowIndex) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);

        setTitleWidth(wd);
        setChildMinWidth(64);
        setPadding(4);
        int p = padding/4;
        setPadding(0, p, 0, p);
        setTitle("");
        this.rowIndex = rowIndex;
    }

    public OptionsTableRowLayout(Context context, int r, int rowIndex) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        borderR = r;
        setTitleWidth(wd);
        setChildMinWidth(64);
        setPadding(4);
        int p = padding/4;
        setPadding(0, p, 0, p);
        setTitle("");
        this.rowIndex = rowIndex;
    }

    public OptionsTableRowLayout(Context context, String title, int columns, int rowIndex) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);

        setTitleWidth(wd);
        setChildMinWidth(64);
        setPadding(4);
        int p = padding/4;
        setPadding(0, p, 0, p);

        setTitle(title);
        addOptions(columns);
        this.rowIndex = rowIndex;
    }

    public void setTitleWidth(int dp) {
        this.titleWidth = (int)(getResources().getDisplayMetrics().density * dp);
    }

    public void setChildMinWidth(int dp) {
        this.childMinWidth = (int)(getResources().getDisplayMetrics().density * dp);
    }

    public void setPadding(int dp) {
        this.padding = (int)(getResources().getDisplayMetrics().density * dp);
    }

    public void addLabel(String value) {
        if (value == null || value.isEmpty())
            return;

        TextView t = getTextView(value, 1);

        t.setTypeface(null, Typeface.BOLD);

        addView(t);
    }

    public void setTitle(String value) {

        if (value == null)
            value = "";

        if (rowTitle == null) {
            rowTitle = getTextView(value, 0);
            rowTitle.setPadding(padding, padding, padding, padding);
            rowTitle.setMinWidth(titleWidth);
            rowTitle.setMaxWidth(titleWidth);
            rowTitle.setBackgroundResource(R.drawable.border_for_row_title);
            rowTitle.setMaxLines(4);
            rowTitle.setEllipsize(TextUtils.TruncateAt.END);
            addView(rowTitle);
            if (value.isEmpty())
                rowTitle.setVisibility(View.INVISIBLE);

        } else {
            rowTitle.setText(value);
        }



    }

    private TextView getTextView(String value, int w) {
        TextView t = new TextView(getContext());
        t.setText(value);
        t.setGravity(w==1?Gravity.CENTER:(Gravity.CENTER_VERTICAL|Gravity.LEFT));

        LayoutParams ll = new LayoutParams(
                w == 1?LayoutParams.MATCH_PARENT:LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
        );

        t.setBackgroundResource(
                borderR == 0?
                R.drawable.border_for_view_transparent:
                borderR);
        t.setMinWidth(this.childMinWidth);
        if (w != 0)
            ll.setMargins(padding/2, 0, 0, 0);
        t.setPadding(padding+padding, padding, padding+padding, padding);

        //ll.weight = w;
        t.setLayoutParams(ll);
        return t;
    }

    private ArrayList<RadioButton> radioButtonArrayList;
    private int columnIndex = 0;
    private FrameLayout getRadioButton() {
        FrameLayout t = new FrameLayout(getContext());
        RadioButton r = new RadioButton(getContext());


        LayoutParams ll = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        );

        FrameLayout.LayoutParams rl = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        );

        rl.gravity = Gravity.CENTER;

        t.addView(r);
        r.setLayoutParams(rl);
        t.setBackgroundResource(R.drawable.border_for_view_transparent);

        ll.setMargins(padding/2, 0, 0, 0);
        t.setPadding(padding, padding, padding, padding);
        r.setTag((columnIndex++));
        getRadioButtons().add(r);
        r.setOnClickListener(getOnClickListener());

       // ll.weight = 1;
        t.setLayoutParams(ll);
        return t;
    }

    public ArrayList<RadioButton> getRadioButtons() {
        if (radioButtonArrayList == null) {
            radioButtonArrayList = new ArrayList<>();
        }
        return radioButtonArrayList;
    }

    public void addOptions(int count) {
        for(int i = 0; i < count; i++) {
            addView(getRadioButton());

        }
    }

    private OnClickListener clickListener;
    private int oldColumn = -1;
    private OnClickListener getOnClickListener() {
        if (clickListener == null) {
            clickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    for(RadioButton r: getRadioButtons()) {
                        if (v == r) {
                            if (optionsTableItemSelectedInterface != null) {
                                int newColumn = (int)v.getTag();
                                optionsTableItemSelectedInterface.selectedItem(rowIndex, oldColumn, newColumn);
                                oldColumn = newColumn;
                            }
                            continue;
                        }
                        r.setChecked(false);
                    }
                }
            };
        }
        return clickListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int vc = getChildCount();
            int ch = heightMeasureSpec;
            int cw = widthMeasureSpec;
            if (vc > 1) {
                int h = MeasureSpec.getSize(heightMeasureSpec);
                int w = MeasureSpec.getSize(widthMeasureSpec);
                measureChild(rowTitle, widthMeasureSpec, heightMeasureSpec);
                ch = rowTitle.getMeasuredHeight();
                int rw = rowTitle.getMeasuredWidth();
                cw = 0;
                w -= rw;
                int pw = w / (vc-1);
                pw -=  (padding/2);
                Log.e("MEASURE", pw + ", " + childMinWidth + ", " + w + ", " + vc + ", " + rw);

                if (pw < childMinWidth)
                    pw = childMinWidth;
                Log.e("MEASURE", " -> " + pw);

                for(int i = 1; i < vc; i++) {
                    View v = getChildAt(i);
                    v.getLayoutParams().width = pw;
                    measureChild(v,widthMeasureSpec, heightMeasureSpec );
                    int t = v.getMeasuredHeight();
                    cw+=v.getMeasuredWidth();
                    if (t > ch)
                        ch = t;

                }
            }
        super.onMeasure(resolveSize(cw, widthMeasureSpec), resolveSize(ch, heightMeasureSpec));
    }

    private OptionsTableItemSelectedInterface optionsTableItemSelectedInterface;

    public void setOptionsTableItemSelectedInterface(OptionsTableItemSelectedInterface listener) {
        optionsTableItemSelectedInterface = listener;
    }

}
