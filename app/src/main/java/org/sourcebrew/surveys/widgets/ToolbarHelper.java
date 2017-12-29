package org.sourcebrew.surveys.widgets;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John on 12/27/2017.
 */

public class ToolbarHelper {

    private View toolbar;

    private ToolbarItemInterface toolbarItemInterface;

    public void setToolbarItemInterface(ToolbarItemInterface listener) {
        toolbarItemInterface = listener;
    }

    private ArrayList<XImageToggle> toggles = new ArrayList<>();

    public ToolbarHelper(View toolbar) {
        this.toolbar = toolbar;
    }

    public void add(View targetView, int ... resImages) {
        toggles.add(new XImageToggle(targetView, resImages));
    }

    public class XImageToggle {
        private View targetView;
        private final int[] resourceImages;
        private int state = 1;



        public XImageToggle(View targetView, int ... resImages) {
            this.targetView = targetView;
            if (resImages == null)
                resourceImages = null;
            else {
                resourceImages = new int[resImages.length];
                for(int i = 0; i < resImages.length; i++) {
                    resourceImages[i] = resImages[i];
                }
            }

            targetView.setOnClickListener(getClickListener());


        }
        public XImageToggle(View targetView) {
            this(targetView, null);
        }

        private View.OnClickListener clickListener = null;

        private View.OnClickListener getClickListener() {
            if (clickListener == null) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (resourceImages != null && v != null) {
                            if (++state >= resourceImages.length) state = 0;
                            try {
                                v.setBackgroundResource(resourceImages[state]);
                                if (toolbarItemInterface != null) {
                                    toolbarItemInterface.onSelected(v.getId(), state);
                                }
                            } catch (Resources.NotFoundException exp) {

                            }
                        }
                    }
                };
            }
            return clickListener;
        }
    }

}
