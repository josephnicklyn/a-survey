package org.sourcebrew.surveys.widgets;

import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by John on 12/27/2017.
 */

public class DockInfo {

    private final ViewGroup view;
    private int dockSize;
    private DockOrientation dockOrientation;

    static int MIN_SIZE = 0;
    static int MAX_SIZE = 0;

    private float fractionalSize = 0.0f;
    private boolean hasFractionalSizeSet = false;

    public DockInfo(
            ViewGroup view,
            DockOrientation dockOrientation,
            int dockSize) {

        if (MIN_SIZE == 0) {
            MIN_SIZE = (int)(view.getResources().getDisplayMetrics().density * 32);
        }

        if (MAX_SIZE == 0) {
            MAX_SIZE = (int)(view.getResources().getDisplayMetrics().density * 180);
        }

        this.view = view;
        this.dockOrientation = dockOrientation;
        setDockSize(dockSize);

    }

    public int getScaledSize(int targetSize) {
        if (!hasFractionalSizeSet) {
            return dockSize;
        } else {
            int v = (int)(fractionalSize * targetSize);
            if (v < MIN_SIZE)
                v = MIN_SIZE;
            return v;
        }
    }

    public DockInfo setFractionalSize(float v) {

        if (v >= 0.10f && v <= 0.50f) {
            hasFractionalSizeSet = true;
            fractionalSize = v;
        }

        return DockInfo.this;
    }

    public void setParent(ViewGroup now) {
        if (now != null) {
            if (containsChild(now))
                return;
            if (view != null) {
                if (view.getParent() != null) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }
            now.addView(view);
        }

    }

    private boolean containsChild(ViewGroup now) {

        if (now == null)
            return false;

        for(int i = 0; i < now.getChildCount(); i++) {
            if (now.getChildAt(i) == view)
                return true;
        }
        return false;
    }

    public ViewGroup getViewGroup() {
        return view;
    }

    public int getDockSize() {
        return dockSize;
    }

    public DockOrientation getDockOrientation() {
        return dockOrientation;
    }

    public void setDockSize(int value) {

        value = (int)(view.getResources().getDisplayMetrics().density * value);

        if (value < MIN_SIZE) dockSize = MIN_SIZE;
        if (value > MAX_SIZE) dockSize = MAX_SIZE;

        switch (dockOrientation) {
            case DOCK_END:
            case DOCK_START:
                setLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                break;
            case DOCK_CENTER:
                setLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    );
                break;

            case DOCK_LEFT:
            case DOCK_RIGHT:
                setLayoutParams(
                        value,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    );
                break;
            case DOCK_TOP:
            case DOCK_BOTTOM:
                setLayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        value
                    );
                break;
        }
        dockSize = value;
    }

    public void setDockOrientation(DockOrientation value) {
        dockOrientation = value;
        setDockSize(dockSize);
    }

    private void setLayoutParams(int width, int height) {
        ViewGroup.MarginLayoutParams vl = new ViewGroup.MarginLayoutParams(
                width,
                height
        );
        int p = (int)(view.getResources().getDisplayMetrics().density * 8);
        vl.setMargins(p, p, p, p);
        view.setLayoutParams(vl);
    }

    public void setBorder(int backColor, int lineColor, int lineWidth) {
        GradientDrawable bkg = new GradientDrawable();
        bkg.setColor(backColor);
        bkg.setStroke(lineWidth, lineColor);
        view.setBackground(bkg);
    }

    public boolean setDockPosition(int w, int h) {
        switch (dockOrientation) {
            case DOCK_CENTER:
                //view.getLayoutParams().width = getScaledSize(w);
                //view.getLayoutParams().height = h;//ViewGroup.LayoutParams.MATCH_PARENT;
                break;
            case DOCK_START:
            case DOCK_END:
                if (w > h) {
                    view.getLayoutParams().width = getScaledSize(w);
                    view.getLayoutParams().height = h;//ViewGroup.LayoutParams.MATCH_PARENT;
                    return true;
                } else {
                    view.getLayoutParams().width = w;//ViewGroup.LayoutParams.MATCH_PARENT;
                    view.getLayoutParams().height = getScaledSize(h);
                    return false;
                }
            case DOCK_LEFT:
            case DOCK_RIGHT:
                view.getLayoutParams().width = getScaledSize(w);
                view.getLayoutParams().height = h;//ViewGroup.LayoutParams.MATCH_PARENT;

                break;
            case DOCK_TOP:
            case DOCK_BOTTOM:
                view.getLayoutParams().width = w;//ViewGroup.LayoutParams.MATCH_PARENT;
                view.getLayoutParams().height = getScaledSize(h);
                break;
        }
        return false;
    }

    public void layout(int left, int top, int right, int bottom) {
        boolean isHorz = (right-left)>(bottom-top);
        if (dockOrientation == DockOrientation.DOCK_START) {
            Log.e("X_SCALE", "ON LAYOUT: " + dockOrientation.toString() + " x " + isHorz);

            if (isHorz) {   // left
                view.layout(left, top, view.getMeasuredWidth(), bottom);
            } else {        // top
                view.layout(left, top, right, top+view.getMeasuredHeight() );
            }
        } else if (dockOrientation == DockOrientation.DOCK_END) {

            if (isHorz) {   // right
                view.layout(right-view.getMeasuredWidth(), top, right, bottom);
            } else {        // bottom
                view.layout(left, bottom-view.getMeasuredHeight(), right, bottom);
            }
        }
    }

}
