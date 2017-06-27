//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-勧め路線詳細画面
//  名称：勧め路線詳細画面プログラム
//  説明：勧め路線詳細画面を表示するクラス
//　
//　作成：2017/06/20 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static jp.co.ivis.pockettravel.MessageList.TB_M_005;
import static jp.co.ivis.pockettravel.MessageList.TB_M_006;

/**
 * 勧め路線詳細画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */

public class RecommendRouteDetailsActivity extends Activity {

    private Button addBtn;
    private Button rtnBtn;
    private ListView listView;
    private DetailsAdapter adapter;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private String cityName;
    private int routeID;
    private int scheduleId;
    private String startDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_route_details);

        hideBottomUIMenu();

        addBtn = (Button) findViewById(R.id.recommend_route__details_next_btn);
        rtnBtn = (Button) findViewById(R.id.recommend_route__details_rtn_btn);

        listView = (ListView) findViewById(R.id.recommend_details_layout);

        dbUnit = new DBUnit(this);

        intent = getIntent();
        bundle = intent.getExtras();

        cityName = bundle.getString("city_name");

        routeID = bundle.getInt("route_id");

        startDate = bundle.getString("start_date");

        cursor = dbUnit.getSubRoute(routeID);

        adapter = new DetailsAdapter(this,cursor);

        listView.setAdapter(adapter);

        System.out.println(startDate);

        cursor = dbUnit.getScheduleIdByStartDate(startDate);

        cursor.moveToFirst();

        System.out.println(cursor.getCount());

        scheduleId = cursor.getInt(cursor.getColumnIndex("schedule_id"));

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUnit.deleteSchedule(scheduleId);
                intent = new Intent(RecommendRouteDetailsActivity.this,RecommendRouteActivity.class);
                bundle.remove("route_id");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDiaglog(RecommendRouteDetailsActivity.this,TB_M_005,"注意");
            }
        });
    }

    private class DetailsAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private Cursor cursor;
        private TextView content;
        private View detailsImage;

        DetailsAdapter(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.details_row,null);
            content = (TextView) convertView.findViewById(R.id.details_row);
            detailsImage = (View) convertView.findViewById(R.id.details_image);
            cursor.moveToPosition(position);
            final int day  = cursor.getInt(cursor.getColumnIndex("sub_route_day"));
            String placeName = cursor.getString(cursor.getColumnIndex("place_name"));
            String textContent = new String("Day" + day + ":" + placeName);
            content.setText(textContent);
            if (position == (cursor.getCount() - 1)) {
                detailsImage.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }
    }

    private void showAlertDiaglog(final Context context, String message, String title) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursor = dbUnit.getSubRoute(routeID);
                while (cursor.moveToNext()) {

                    int days = cursor.getInt(cursor.getColumnIndex("sub_route_day"));
                    String subScheduleDate = null;
                    try {
                        Date date = sdf.parse(startDate);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.DAY_OF_YEAR,days - 1);
                        date = calendar.getTime();
                        subScheduleDate = sdf.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    String placeName = cursor.getString(cursor.getColumnIndex("place_name"));
                    dbUnit.addSubSchedule(subScheduleDate,placeName,scheduleId);
                }
                toast = toast.makeText(RecommendRouteDetailsActivity.this,TB_M_006,Toast.LENGTH_LONG);
                toast.show();
                intent = new Intent(RecommendRouteDetailsActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        alertDialogbuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = alertDialogbuilder.create();
        alertDialog.show();

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
