package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import org.sourcebrew.surveys.R;

/**
 * <p>
 * A <code>FlowLayout</code> is a layout which allows children to wrap to a new row, all children
 * are of equal width; depending on the number of columns desired, with a minimum width of a
 * column (250dp).  If the columns are limited to 1 either by using the <code>setMaxColumns</code>
 * method or because the width is too small to contain 2 columns; the FlowLayout acts much like a
 * <code>LinearLayout</code> with its orientation set to vertical.
 * A <code>FlowLayout</code> provides automatic layout adjustments based on the width of the device.
 * </p>
 *
 * Created by John on 12/9/2017.
 */

public class FlowLayout extends ViewGroup {

    /**
     * The desired minimum width of a column, it is set at an arbitrary number which seems to
     * work well for 2 columns on 5" screens when orientation is in landscape
     */

    protected int nominalColumnWidth = 250;

    /**
     * The column width set with display metrics density
     */
    protected int columnWidth;

    /**
     * Vertical and Horizontal gaps between rows and columns, generally under utilized
     */
    protected int hGap, vGap;



    public FlowLayout(Context context) {
        this(context, null, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        setColumnWidth(nominalColumnWidth);


    }

    public void onViewAdded(View child) {
        if (child.getLayoutParams().width == LayoutParams.WRAP_CONTENT)
            child.setTag(R.string.WAS_WRAP_CONTENT, true);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int count = getChildCount();

        final int left = this.getPaddingLeft();
        final int top = this.getPaddingTop();

        int x = 0, y = 0, w = 0, h = 0;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            w = child.getMeasuredWidth();
            h = child.getMeasuredHeight();


            child.layout(0, 0, w, h);

        }
    }
    private int maxColumns = 4;
    public void setMaxColumns(int value) {
        if (value < 1) value = 4;
        if (value > 8) value = 8;
        maxColumns = value;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        width-=(getPaddingLeft()+getPaddingRight());

        int columns = width / columnWidth;

        int vc = 0;
        for (int i = 0; i < count; i++) {

            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;
            vc++;
        }

        if (columns > vc)
            columns = vc;
        if (columns < 1) columns = 1;
        if (columns > maxColumns) columns = maxColumns;

        int actualColumnWidth = (width+(columns >1?hGap:0))/columns;
        int viewWidth = actualColumnWidth - (columns >1?hGap:0);
        //int xWidth = actualColumnWidth;
        int mostY = 0, childState = 0, rowHeight = 0, onCol = 0;
        int x = getPaddingLeft(), y = getPaddingTop();
        int vW = 0, vH = 0;

        boolean doWrap = false;

        boolean flaggedForAdd = false;

        for (int i = 0; i < count; i++) {

            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;


            if (columns == 1) {
                child.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                vW = child.getMeasuredWidth();
                vH = child.getMeasuredHeight();
                actualColumnWidth = (doWrap?vW+hGap:actualColumnWidth);
            } else {
                child.getLayoutParams().width = doWrap?LayoutParams.WRAP_CONTENT:viewWidth;
                if (!doWrap)
                    measureChild(child, MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
                else
                    measureChild(child, MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.AT_MOST), heightMeasureSpec);

                vW = child.getMeasuredWidth();
                vH = child.getMeasuredHeight();
                actualColumnWidth = (doWrap?vW+hGap:actualColumnWidth);
            }


            if (rowHeight < vH)
                rowHeight = vH;

            child.setX(x);
            child.setY(y);

            if (++onCol>=columns) {
                onCol = 0;
                y+=(vGap+rowHeight);
                x = getPaddingLeft();
                rowHeight = 0;
                mostY = y;
                flaggedForAdd = false;
            } else {
                x+=actualColumnWidth;
                flaggedForAdd = true;
            }

            childState = combineMeasuredStates(childState, child.getMeasuredState());

        }

        if (flaggedForAdd)
            mostY += rowHeight;
        setMeasuredDimension(
                widthMeasureSpec,
                resolveSizeAndState(mostY, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }


    public void setColumnWidth(int value) {
        if (value < 120) value = 120;
        hGap = (int)(getResources().getDisplayMetrics().density * 8);
        vGap = (int)(getResources().getDisplayMetrics().density * 4);

        this.columnWidth = (int)(getResources().getDisplayMetrics().density * value);
    }
}