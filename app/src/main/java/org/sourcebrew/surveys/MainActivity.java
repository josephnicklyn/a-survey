package org.sourcebrew.surveys;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.sourcebrew.surveys.utilities.ResponseLayoutsContainers;
import org.sourcebrew.surveys.utilities.ResponseManager;
import org.sourcebrew.surveys.utilities.SourceHelper;
import org.sourcebrew.surveys.widgets.DockBuddy;
import org.sourcebrew.surveys.widgets.DockOrientation;
import org.sourcebrew.surveys.widgets.ToolbarHelper;
import org.sourcebrew.surveys.widgets.ToolbarItemInterface;

public class MainActivity extends Activity implements ToolbarItemInterface{

    private ScrollView scrollView;
    private FrameLayout sideBar, rootView;
    private LinearLayout target;
    private DockBuddy dockBuddy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);
        sideBar = findViewById(R.id.sideBar);
        scrollView = findViewById(R.id.scrollView);
        target = findViewById(R.id.target);

        initalizeToolbar();

        dockBuddy = new DockBuddy(this);
        dockBuddy.addDockable(scrollView, DockOrientation.DOCK_CENTER, 0);
        dockBuddy.addDockable(sideBar, DockOrientation.DOCK_END, 180)
            .setFractionalSize(0.33f);
        rootView.addView(dockBuddy);
        getPermission();
    }

    private void initalizeToolbar() {

        View ersaToolbar = findViewById(R.id.ersaToolbar);

        if (ersaToolbar  == null) return;

        View btnHideShow = findViewById(R.id.btnHideShow);

        ToolbarHelper th = new ToolbarHelper(ersaToolbar);
        th.setToolbarItemInterface(this);
        th.add(
                btnHideShow,
                R.drawable.ic_frame_split,
                R.drawable.ic_frame
        );
    }

    private void getPermission() {
        int result =
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) +
            checkSelfPermission(Manifest.permission.INTERNET);

        if (result == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            requestPermissions(
                new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.INTERNET
                },
                101
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialize();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initialize() {
        new ResponseManager(target, SourceHelper.getRaw(this)).
            setContainer(ResponseLayoutsContainers.RESPONSE_LAYOUTS_PRIMAY_CONTAINER, sideBar);
    }

    @Override
    public void onSelected(int id, int state) {
        dockBuddy.hideShowPanels(sideBar, state);
    }

}
