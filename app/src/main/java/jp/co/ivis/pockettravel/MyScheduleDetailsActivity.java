package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Context;
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

/**
 * Created by shao.wenbo on 2017/06/20.
 */

public class MyScheduleDetailsActivity extends Activity {

    private ListView lv_layout;
    private Button rtnBtn;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private DetailsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_schedule_details);

        hideBottomUIMenu();

        lv_layout = (ListView) findViewById(R.id.lv_layout);
        rtnBtn = (Button) findViewById(R.id.schedule__details_rtn_btn);

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MyScheduleDetailsActivity.this,MyScheduleActivity.class);
                startActivity(intent);
            }
        });

        dbUnit = new DBUnit(this);
        intent = getIntent();
        bundle = intent.getExtras();
        String data = bundle.getString("data");
        int id = bundle.getInt("schedule_id");

        System.out.println(id + "-----------------------------");

        cursor = dbUnit.getSubSchedule(id);

        System.out.println(cursor.getCount() + "----------------------");

        adapter = new DetailsAdapter(this,cursor);

        lv_layout.setAdapter( adapter);
        TextView schedule = (TextView) findViewById(R.id.schedule);
        schedule.setText(data);
    }

    class DetailsAdapter extends BaseAdapter{
        private Context context;
        private Cursor cursor;
        private View array;

        DetailsAdapter(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }
        public int getCount(){
            return  cursor.getCount();
        }
        public Object getItem(int position){
            return null;
        }
        public long getItemId(int position){
            return position;
        }
        public View getView(final int position,View view,ViewGroup positon2) {
            if (view == null) {
                view = LayoutInflater.from(MyScheduleDetailsActivity.this).inflate(
                        R.layout.schedule_details_layout, null);
            }
            final TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            array = (View) view.findViewById(R.id.array);
            cursor.moveToPosition(position);
            String sub_schedule_date = cursor.getString(cursor.getColumnIndex("sub_schedule_date"));
            final String place_name = cursor.getString(cursor.getColumnIndex("place_name"));
            final String textContent = new String(sub_schedule_date + ":" + place_name);
            tv_name.setText(textContent);
            if (position == (cursor.getCount() - 1)) {
                array.setVisibility(View.INVISIBLE);
            }
            return view;
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
