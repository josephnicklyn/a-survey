package org.sourcebrew.surveys.utilities;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sourcebrew.surveys.widgets.DockBuddy;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by John on 12/25/2017.
 */

public class ResponseManager {

    private ViewGroup target;
    private JSONObject json;
    private final ArrayList<QuestionLayout> questionLayouts = new ArrayList<>();


    private final HashMap<String, ArrayList<JSONObject>> activeResults = new HashMap<>();

    public ResponseManager(
        ViewGroup target,
        JSONObject json
    ) {
        this.target = target;
        this.json = json;


        loadContent();

    }

    private void loadContent() {
        JSONArray arr = SourceHelper.getArray(json, "questions");
        for(int v = 0; v < arr.length(); v++) {
            JSONObject obj = SourceHelper.getArrayObject(arr, v);
            String id = SourceHelper.getString(obj, "id", "");
            if (id.isEmpty()) {
                continue;
            }
            activeResults.put(id, new ArrayList<JSONObject>());

            QuestionLayout ql = new QuestionLayout(this, target, obj, false);
            questionLayouts.add(ql);
        }
    }

    public final void onQuestionValueChanged(
            QuestionLayout ql,
            boolean selected,
            JSONObject object) {

        updateHash(ql, selected, object);
        if (responceProgress != null) {
            responceProgress.updateResponse(activeResults);
        }

        //printHash();

    }


    public void printHash() {
        StringBuilder b = new StringBuilder();
        for(String s: activeResults.keySet()) {
            ArrayList<JSONObject> target = activeResults.get(s);

            for(JSONObject o: target) {

                try {
                    b.append(o.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("HASH_OUT", b.toString());
    }

    public void updateHash( QuestionLayout ql,
                            boolean selected,
                            JSONObject object) {

        if (object == null) {
            return;
        }

        String id = SourceHelper.getString(object, "id", "");

        if (id.isEmpty())
            return;

        ArrayList<JSONObject> target = activeResults.get(id);
        if (target == null) {
            target = new ArrayList<JSONObject>();
            activeResults.put(id, target);
        }

        String responseID = SourceHelper.getString(object, "responce_item", "");
        if (responseID.isEmpty())
            return;

        int itemIndex = getItemIndex(target, responseID);

        if (!selected) {
            if (itemIndex != -1) {
                target.remove(itemIndex);
            }
        } else {
            if (itemIndex != -1) {
                target.remove(itemIndex);
            }
            try {
                target.add(new JSONObject(object.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private int getItemIndex(ArrayList<JSONObject> target, String responseID) {
        int result = -1;

        for(int i =  0; i < target.size(); i++) {
            JSONObject o = target.get(i);
            String id = SourceHelper.getString(o, "responce_item", "");

            if (responseID.equalsIgnoreCase(id)) {
                result = i;
                break;
            }
        }

        return result;

    }

    private ResponceProgress responceProgress;

    private HashMap<ResponseLayoutsContainers, ViewGroup> responseLayoutsContainers = new HashMap<>();

    public void setContainer(ResponseLayoutsContainers rlc, ViewGroup viewGroup) {

        //ViewGroup vg = null;

        if (!responseLayoutsContainers.containsKey(rlc)) {

            switch(rlc) {

                case RESPONSE_LAYOUTS_PRIMAY_CONTAINER:
                    if (responceProgress != null) {
                        ((ViewGroup)responceProgress.getParent()).removeView(responceProgress);
                    }
                    responceProgress = new ResponceProgress(viewGroup.getContext(), this, json);
                    viewGroup.addView(responceProgress);
                    responceProgress.updateResponse(activeResults);

                    break;
            }
        }
    }

    public String getSurveyTitle() {
        return SourceHelper.getString(json, "title", "Untitled Survey");
    }

    public HashMap<String, ArrayList<JSONObject>> getActiveResults() {
        return activeResults;
    }

    public JSONObject getJson() {
        return json;
    }
}
