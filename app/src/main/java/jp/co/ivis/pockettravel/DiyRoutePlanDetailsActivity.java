//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-作成計画具体画面
//  名称：作成計画具体画面プログラム
//  説明：作成計画具体画面を表示するクラス
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
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import static jp.co.ivis.pockettravel.MessageList.TB_M_005;
import static jp.co.ivis.pockettravel.MessageList.TB_M_006;

/**
 * 作成計画具体画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */
public class DiyRoutePlanDetailsActivity extends Activity {

    private Button addBtn;
    private Button rtnBtn;
    private TextView titleText;
    private ListView listView;
    private DetailsAdapter adapter;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private String cityName;
    private int scheduleId;
    private String startDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diy_route_plan_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        addBtn = (Button) findViewById(R.id.diy_route__details_next_btn);
        rtnBtn = (Button) findViewById(R.id.diy_route__details_rtn_btn);
        titleText = (TextView) findViewById(R.id.details_title);

        listView = (ListView) findViewById(R.id.diy_details_layout);

        dbUnit = new DBUnit(this);

        intent = getIntent();
        bundle = intent.getExtras();

        scheduleId = bundle.getInt("schedule_id");

        cityName = bundle.getString("city_name");

        startDate = bundle.getString("start_date");

        titleText.setText(startDate + "   " + cityName);

        cursor = dbUnit.getSubSchedule(scheduleId);

        adapter = new DetailsAdapter(this,cursor);

        listView.setAdapter(adapter);

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(DiyRoutePlanDetailsActivity.this,PlaceSelectActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDiaglog(DiyRoutePlanDetailsActivity.this,TB_M_005,"注意");
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
            detailsImage = convertView.findViewById(R.id.details_image);

            cursor.moveToPosition(position);

            String placeName = cursor.getString(cursor.getColumnIndex("place_name"));

            String textContent = new String("DAY" + (position + 1) + ":" + placeName);
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
                toast = toast.makeText(DiyRoutePlanDetailsActivity.this,TB_M_006, Toast.LENGTH_SHORT);
                toast.show();
                intent = new Intent(DiyRoutePlanDetailsActivity.this,MainActivity.class);
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

}
