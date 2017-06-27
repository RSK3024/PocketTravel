//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-日程DIY画面
//  名称：日程DIY画面プログラム
//  説明：日程DIY画面を表示するクラス
//　
//　作成：2017/06/20 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.makeText;
import static jp.co.ivis.pockettravel.MessageList.TB_M_007;
import static jp.co.ivis.pockettravel.MessageList.TB_M_013;
import static jp.co.ivis.pockettravel.MessageList.TB_M_014;
import static jp.co.ivis.pockettravel.MessageList.TB_M_021;

/**
 * 日程DIY画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */

public class DiyRouteActivity extends Activity {

    private EditText editText;
    private Button nextBtn;
    private Button rtnbtn;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private String cityName;
    private String startDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_route);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        editText = (EditText) findViewById(R.id.diy_days_edit);
        nextBtn = (Button) findViewById(R.id.diy_route_next_btn);
        rtnbtn = (Button) findViewById(R.id.diy_route_rtn_btn);

        dbUnit = new DBUnit(this);

        intent = getIntent();
        bundle = intent.getExtras();

        cityName = bundle.getString("city_name");
        startDate = bundle.getString("start_date");

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String peroid = editText.getText().toString();

                if (peroid == null || peroid.equals("")) {
                    toast = toast.makeText(DiyRouteActivity.this,TB_M_014,Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    int peroidInt = Integer.parseInt(peroid);

                    if (peroidInt <= 5) {

                        if (peroidInt == 0) {
                            toast = makeText(DiyRouteActivity.this,TB_M_021,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        else {
                            Date date1 = null ,date2 = null;

                            try {
                                date1 = sdf.parse(startDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date1);
                            calendar.add(Calendar.DAY_OF_YEAR,peroidInt - 1);
                            date2 = calendar.getTime();
                            String endDate = sdf.format(date2);

                            if (checkDateVaild(date1,date2)) {
                                dbUnit.addSchedule(startDate,peroidInt,endDate,cityName);
                                cursor = dbUnit.getScheduleIdByStartDate(startDate);
                                cursor.moveToFirst();
                                int scheduleId = cursor.getInt(cursor.getColumnIndex("schedule_id"));
                                bundle.putInt("schedule_id",scheduleId);
                                bundle.putInt("peroid",peroidInt);
                                intent = new Intent(DiyRouteActivity.this,PlaceSelectActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                            else {
                                toast = makeText(DiyRouteActivity.this,TB_M_013,Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                    else {
                        toast = makeText(DiyRouteActivity.this,TB_M_007,Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });

        rtnbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(DiyRouteActivity.this,CreateModeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }

    private boolean checkDateVaild(Date date1,Date date2) {
        boolean result = true;

        Date startDate = null;

        Cursor cursor = dbUnit.getSchedule();

        while (cursor.moveToNext()) {

            try {
                startDate = sdf.parse(cursor.getString(cursor.getColumnIndex("start_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar start = Calendar.getInstance();
            start.setTime(startDate);
            start.add(Calendar.DAY_OF_YEAR,-1);
            startDate = start.getTime();

            if (date1.before(startDate) && startDate.before(date2)) {
                result = false;
                return result;
            }

        }
        return result;
    }

}
