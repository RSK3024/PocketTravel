//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-勧め路線画面
//  名称：勧め路線画面プログラム
//  説明：勧め路線画面を表示するクラス
//　
//　作成：2017/06/20 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.makeText;
import static jp.co.ivis.pockettravel.MessageList.TB_M_013;

/**
 * 勧め路線画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */

public class RecommendRouteActivity extends Activity implements View.OnClickListener{

    String cityName;
    Date startDate;
    private Button[] recoRouteBtns;
    private Button rtnBtn;
    private LinearLayout linearLayout;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_route);

        hideBottomUIMenu();

        linearLayout = (LinearLayout) findViewById(R.id.recommend_route_button_layout);
        rtnBtn = (Button) findViewById(R.id.recommend_route_rtn_btn);

        dbUnit = new DBUnit(this);

        intent = getIntent();
        bundle = intent.getExtras();

        try {
            cityName = bundle.getString("city_name");
            startDate = sdf.parse(bundle.getString("start_date"));
            cursor = dbUnit.getCityIdByName(cityName);
            cursor.moveToFirst();
            int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
            cursor = dbUnit.getRoute(cityId);

            recoRouteBtns = new Button[cursor.getCount()];

            for (int i = 0; i < cursor.getCount(); i++ ) {
                cursor.moveToPosition(i);
                recoRouteBtns[i] = new Button(RecommendRouteActivity.this);
                linearLayout.addView(recoRouteBtns[i]);
                recoRouteBtns[i].setId(i);
                recoRouteBtns[i].setText(cursor.getString(cursor.getColumnIndex("route_name")));
                recoRouteBtns[i].setOnClickListener(this);
            }

            rtnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(RecommendRouteActivity.this,CreateModeActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDateVaild(Date date1,Date date2) {
        boolean result = true;

        Date startDate;

        cursor = dbUnit.getSchedule();

        while (cursor.moveToNext()) {

            try {
                startDate = sdf.parse(cursor.getString(cursor.getColumnIndex("start_date")));

                Calendar start = Calendar.getInstance();
                start.setTime(startDate);
                start.add(Calendar.DAY_OF_YEAR,-1);
                startDate = start.getTime();

                if (date1.before(startDate) && startDate.before(date2)) {
                    result = false;
                    return result;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < recoRouteBtns.length; i++) {
            if (v.getId() == recoRouteBtns[i].getId()) {
                cursor.moveToPosition(i);
                int peroid = cursor.getInt(cursor.getColumnIndex("peroid"));
                int routeId = cursor.getInt(cursor.getColumnIndex("route_id"));

                Calendar end = Calendar.getInstance();
                end.setTime(startDate);
                end.add(Calendar.DAY_OF_YEAR,peroid-1);
                Date endDate = end.getTime();

                if (checkDateVaild(startDate,endDate)) {

                    dbUnit.addSchedule(sdf.format(startDate),peroid,sdf.format(endDate),cityName);
                    intent = new Intent(RecommendRouteActivity.this,RecommendRouteDetailsActivity.class);
                    bundle.putInt("route_id",routeId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    toast = makeText(RecommendRouteActivity.this,TB_M_013, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }

    }

    private void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}
