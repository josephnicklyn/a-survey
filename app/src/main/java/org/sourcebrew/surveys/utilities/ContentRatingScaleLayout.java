package org.sourcebrew.surveys.utilities;

import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by John on 12/23/2017.
 */

public class ContentRatingScaleLayout   extends ContentLayout {

    public ContentRatingScaleLayout(QuestionLayout questionLayout) {
        super(questionLayout);
        build();

    }
    JSONArray jsonColumns, jsonRows;
    JSONObject jsonResult;

    @Override
    public void build() {

        JSONObject contents = SourceHelper.getObject(getJSON(), "contents");

        if (contents != null) {

            jsonColumns = SourceHelper.getArray(contents, "columns");
            jsonRows = SourceHelper.getArray(contents, "rows");

            try {
                jsonResult = new JSONObject("{}");
            } catch (JSONException e) {
            }

            if (jsonColumns != null && jsonRows != null) {

                for (int i = 0; i < jsonColumns.length(); i++) {
                    JSONObject column = SourceHelper.getArrayObject(jsonColumns, i);
                    if (column == null)
                        break;
                    String columnLabel = SourceHelper.getString(column, "value", ("col-" + (i+1)));

                    getOptionsTableLayout().getHeader().addLabel(columnLabel);
                }

                for (int i = 0; i < jsonRows.length(); i++) {
                    JSONObject row = SourceHelper.getArrayObject(jsonRows, i);
                    if (jsonRows == null)
                        break;
                    String rowLabel = SourceHelper.getString(row, "value", ("col-" + (i+1)));

                    getOptionsTableLayout().addRow(rowLabel, jsonColumns.length(), onOptionsTableItemSelectedInterface);
                }

            }
        }
    }

    private OptionsTableItemSelectedInterface onOptionsTableItemSelectedInterface = new OptionsTableItemSelectedInterface() {
        @Override
        public void selectedItem(int row, int oldColumn, int newColumn) {
            Log.e("CHANGED", row + ", " + newColumn + " && " + oldColumn);
            if (oldColumn != -1) {
                onChangeMade(false, updateJSON(row, oldColumn));
            }
            onChangeMade(true, updateJSON(row, newColumn));


        }
    };

    private Object updateJSON(int row, int column) {

        try {
            int y = jsonColumns.length();
            int lPos = (y * row)+column;
            String id = SourceHelper.getString(getJSON(), "id", "");
            jsonResult.put("responce_item", id + "." + (lPos));
            jsonResult.put("id", id);
            jsonResult.put("row", SourceHelper.getString(jsonRows, "value", row));
            jsonResult.put("column", SourceHelper.getString(jsonColumns, "value", column));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonResult;
    }

    public OptionsTableLayout getOptionsTableLayout() {
        return (OptionsTableLayout)getContentContainer();
    }

    @Override
    public void onClick(View v) {

    }

}
