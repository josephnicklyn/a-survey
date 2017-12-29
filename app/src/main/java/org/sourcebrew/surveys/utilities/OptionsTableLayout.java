package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import org.sourcebrew.surveys.R;

import java.util.ArrayList;

/**
 * Created by John on 12/23/2017.
 */

public class OptionsTableLayout extends HorizontalScrollView {

    private final OptionsTableRowLayout tableHeader;
    LinearLayout container;

    public OptionsTableLayout(Context context) {
        super(context);
        super.setFillViewport(true);
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(
                new HorizontalScrollView.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
        );

        container.setLayoutParams(
                new HorizontalScrollView.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT
                )
        );
        addView(container);
        tableHeader = new OptionsTableRowLayout(getContext(), R.drawable.border_for_row_title);
        container.addView(tableHeader);
    }

    public OptionsTableRowLayout getHeader() {
        return tableHeader;
    }

    private int row = 0;

    public void addRow(String value, int count, OptionsTableItemSelectedInterface listener) {
        OptionsTableRowLayout tr = new OptionsTableRowLayout(getContext(), value, count, (row++));
        tr.setOptionsTableItemSelectedInterface(listener);
        boolean isEven = (getChildCount() % 2) == 0;
        if (isEven) {
            tr.setBackgroundColor(0xFFf4f4fa);
        } else {
            tr.setBackgroundColor(0xFFfafaff);
        }
        container.addView(tr);

    }


}
