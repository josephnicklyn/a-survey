package org.sourcebrew.surveys.widgets;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 12/27/2017.
 */

public class DockBuddy extends ViewGroup {

    private int hViewSize = 0;
    private int vViewSize = 0;
    private float scale;

    //private final ArrayList<DockInfo> dockInfoArrayList = new ArrayList<>();

    private final HashMap<DockOrientation, DockInfo> dockInfoHashMap = new HashMap<>();

    private final int gutter[] = {0, 0, 0, 0};

    public final static int
            GUTTER_LEFT = 0,
            GUTTER_TOP = 1,
            GUTTER_RIGHT = 2,
            GUTTER_BOTTOM = 3;

    public DockBuddy(Context context) {
        super(context);
        hViewSize = (int)(getResources().getDisplayMetrics().density * 280);
        vViewSize = (int)(getResources().getDisplayMetrics().density * 280);
        scale = getResources().getDisplayMetrics().density;
        //int p = toDP(8);
        //setPadding(p, p, p, p);
    }

    private int toDP(int v) {

        return (int)(getResources().getDisplayMetrics().density * v);
    }

    public DockInfo addDockable(ViewGroup v, DockOrientation dockOrientation, int dockSize) {
        DockInfo di = getDockable(v);
        if (di == null) {

            di = new DockInfo(v, dockOrientation, dockSize);
            //dockInfoArrayList.add(di);
            dockInfoHashMap.put(dockOrientation, di);
            di.setParent(this);
            if (di.getDockOrientation() != DockOrientation.DOCK_CENTER) {
                di.setBorder(0x80e8f0ff, 0x804060A0, 2);
            }
        }
        return di;
    }

    private DockInfo getDockable(ViewGroup v) {
        //for(DockInfo di: dockInfoArrayList) {
        for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {
            DockInfo di = dockInfoHashMap.get(dockOrientation);
            if (di.getViewGroup() == v)
                return di;
        }
        return null;
    }

    private boolean containsDockable(ViewGroup v) {
        //for(DockInfo di: dockInfoArrayList) {
        for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {
            DockInfo di = dockInfoHashMap.get(dockOrientation);
            if (di.getViewGroup() == v)
                return true;
        }
        return false;
    }

    private void setBorder(View v, int backColor, int lineColor, int lineWidth) {
        GradientDrawable bkg = new GradientDrawable();
        bkg.setColor(backColor);
        bkg.setStroke(lineWidth, lineColor);
        v.setBackground(bkg);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft(), top = getPaddingTop();
        int right = r-getPaddingRight(), bottom = b-getPaddingBottom();

        //for(DockInfo di: dockInfoArrayList) {
        for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {
            DockInfo di = dockInfoHashMap.get(dockOrientation);
            if (di.getViewGroup().getVisibility() == View.GONE)
                continue;

            ViewGroup vg = di.getViewGroup();
            switch (di.getDockOrientation()) {

                case DOCK_CENTER:
                    int gl = gutter[GUTTER_LEFT];
                    int gt = gutter[GUTTER_TOP];
                    int gr = gutter[GUTTER_RIGHT];
                    int gb = gutter[GUTTER_BOTTOM];
                    vg.layout(left+gl, top+gt, right-gr, bottom-gb);
                    break;
                case DOCK_LEFT:
                    vg.layout(left, top, vg.getMeasuredWidth(), bottom);
                    break;
                case DOCK_TOP:
                    vg.layout(left, top, right, top+vg.getMeasuredHeight() );
                    break;
                case DOCK_RIGHT:
                    vg.layout(right-vg.getMeasuredWidth(), top, right, bottom);
                    break;
                case DOCK_BOTTOM:
                    vg.layout(left, bottom-vg.getMeasuredHeight(), right, bottom);
                    break;
                case DOCK_START:
                case DOCK_END:
                    di.layout(left, top, right, bottom);
                    break;
            }
        }


    }//(411 : 603),(600 : 752),(768 : 888)

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int vPadding = (getPaddingBottom() + getPaddingTop());
        int hPadding = (getPaddingLeft() + getPaddingRight());

        int wd = (int)(width-(getPaddingLeft() + getPaddingRight()));
        int ht = (int)(height-(getPaddingTop() + getPaddingBottom()));


        int rx = 0, ry = 0;

        resetGutters();

       for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {
            DockInfo di = dockInfoHashMap.get(dockOrientation);

            if (di.getViewGroup().getVisibility() == View.GONE)
                continue;

            ViewGroup vg = di.getViewGroup();

            boolean onSW = di.setDockPosition(wd, ht);

            measureChild(vg, widthMeasureSpec, heightMeasureSpec);

            int h = vg.getMeasuredHeight();
            int w = vg.getMeasuredWidth();

            switch (di.getDockOrientation()) {

                case DOCK_CENTER:
                    break;
                case DOCK_LEFT:
                    rx-=w;
                    updateGutter(GUTTER_LEFT, w);
                    break;
                case DOCK_RIGHT:
                    rx-=w;
                    updateGutter(GUTTER_RIGHT, w);
                    break;
                case DOCK_BOTTOM:
                    ry-=h;
                    updateGutter(GUTTER_BOTTOM, h);
                    break;
                case DOCK_TOP:
                    ry-=h;
                    updateGutter(GUTTER_TOP, h);
                    break;
                case DOCK_START:
                    if (onSW) {
                        rx-=w;
                        updateGutter(GUTTER_LEFT, w);
                    } else {
                        ry-=h;
                        updateGutter(GUTTER_TOP, h);
                    }
                    break;
                case DOCK_END:
                    if (onSW) {
                        rx-=w;
                        updateGutter(GUTTER_RIGHT, w);
                    } else {
                        ry-=h;
                        updateGutter(GUTTER_BOTTOM, h);
                    }
                    break;
            }
        }

        marginalizeGutter(toDP(4));
        if (rx != 0 || ry != 0) {
            int cw = rx != 0?width - hGutter() - hPadding:LayoutParams.MATCH_PARENT;
            int ch = ry!=0?height - vGutter() - vPadding:LayoutParams.MATCH_PARENT;

            for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {

                DockInfo di = dockInfoHashMap.get(dockOrientation);

                if (di.getViewGroup().getVisibility() == View.GONE)
                    continue;

                ViewGroup vg = di.getViewGroup();
                switch (di.getDockOrientation()) {

                    case DOCK_CENTER:
                        vg.getLayoutParams().width = cw;
                        vg.getLayoutParams().height = ch;
                        measureChild(vg, widthMeasureSpec, heightMeasureSpec);
                        break;
                }
            }
        } else {
            for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {

                DockInfo di = dockInfoHashMap.get(dockOrientation);

                if (di.getViewGroup().getVisibility() == View.GONE)
                    continue;

                ViewGroup vg = di.getViewGroup();
                switch (di.getDockOrientation()) {

                    case DOCK_CENTER:
                        vg.getLayoutParams().width = LayoutParams.MATCH_PARENT;
                        vg.getLayoutParams().height = LayoutParams.MATCH_PARENT;
                        measureChild(vg, widthMeasureSpec, heightMeasureSpec);
                        break;
                }
            }
        }


        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    private void marginalizeGutter(int m) {
        for(int i = 0; i < gutter.length; i++) {
            if (gutter[i] != 0) {
                gutter[i]+=m;
            }
        }
    }

    private int hGutter() {
        return (gutter[GUTTER_LEFT] + gutter[GUTTER_RIGHT]);
    }

    private int vGutter() {
        return (gutter[GUTTER_TOP] + gutter[GUTTER_BOTTOM]);
    }

    private void updateGutter(int p, int value) {
        if (value < 0)
            value = 0;
        if (gutter[p] < value) {
            gutter[p] = value;
        }
    }

    private void resetGutters() {
        gutter[0] = gutter[1] = gutter[2] = gutter[3] = 0;
    }


    private boolean isWaiting = false;

    private void slideShow(final View view) {

        if (view == null)
            return;

        if (view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
        toggleView = view;
    }

    private View toggleView;
    private static String DEBUG_TAG = "X_SCALE";
    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                Log.d(DEBUG_TAG,"Action was DOWN");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                Log.d(DEBUG_TAG,"Action was MOVE");
                return true;
            case (MotionEvent.ACTION_UP) :
                Log.d(DEBUG_TAG,"Action was UP");
                return true;
            case (MotionEvent.ACTION_CANCEL) :
                Log.d(DEBUG_TAG,"Action was CANCEL");
                return true;
            case (MotionEvent.ACTION_OUTSIDE) :
                Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                        "of current screen element");
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    public void hideShowPanels(DockOrientation dockOrientation, int state) {
        DockInfo di = dockInfoHashMap.get(dockOrientation);
        if (di != null) {
            ViewGroup vg = di.getViewGroup();
            vg.setVisibility((state == 0 ? View.VISIBLE : View.GONE));
        }
    }

    public void hideShowPanels(View v, int state) {

        for(DockOrientation dockOrientation: dockInfoHashMap.keySet()) {
            DockInfo di = dockInfoHashMap.get(dockOrientation);

            if (di.getViewGroup() == v) {
                ViewGroup vg = di.getViewGroup();
                vg.setVisibility((state==0?View.VISIBLE:View.GONE));
            }
        }
        invalidate();
    }

}
