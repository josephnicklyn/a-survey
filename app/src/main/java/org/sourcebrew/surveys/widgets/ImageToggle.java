package org.sourcebrew.surveys.widgets;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;

import org.sourcebrew.surveys.R;

/**
 * Created by John on 12/27/2017.
 */

public class ImageToggle {
    private View targetView;
    private final int[] resourceImages;
    private int state = 0;



    public ImageToggle(View targetView, int ... resImages) {
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
    public ImageToggle(View targetView) {
        this(targetView, null);
    }

    private View.OnClickListener clickListener = null;

    private View.OnClickListener getClickListener() {
        if (clickListener == null) {
            clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (resourceImages != null) {
                        if (++state >= resourceImages.length) state = 0;
                        try {
                            targetView.setBackgroundResource(resourceImages[state]);
                        } catch (Resources.NotFoundException exp) {

                        }
                    }
                }
            };
        }
        return clickListener;
    }
}
