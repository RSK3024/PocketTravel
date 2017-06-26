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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import static jp.co.ivis.pockettravel.MessageList.TB_M_002;
import static jp.co.ivis.pockettravel.MessageList.TB_M_003;

/**
 * Created by shao.wenbo on 2017/06/19.
 */

public class MyScheduleActivity extends Activity {


    private Set<SwipeListLayout> sets = new HashSet<>();

    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private Button rtnBtn;
    private ListView my_schedule;
    private ScheduleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_schedule_activity);

        my_schedule = (ListView) findViewById(R.id.my_schedule);
        rtnBtn = (Button) findViewById(R.id.my_schedule_rtn_btn);

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MyScheduleActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        dbUnit = new DBUnit(this);
        cursor = dbUnit.getSchedule();

        adapter = new ScheduleAdapter(this,cursor);

        my_schedule.setAdapter(adapter);
        my_schedule.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                switch (scrollState){
                    case SCROLL_STATE_TOUCH_SCROLL:
                        if(sets.size() > 0){
                            for (SwipeListLayout s : sets){
                                s.setStatus(SwipeListLayout.Status.Close, true);
                                sets.remove(s);
                            }
                        }
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }

    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout swipeListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.swipeListLayout = slipListLayout;
        }

        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(swipeListLayout);
            } else {
                if (sets.contains(swipeListLayout))
                    sets.remove(swipeListLayout);
            }
        }

        public void onStartOpenAnimation(){

        }

        public void onStartCloseAnimation(){

        }
    }

    private class ScheduleAdapter extends BaseAdapter{
        private Context context;
        private LayoutInflater layoutInflater;
        private Cursor cursor;

       ScheduleAdapter(Context context, Cursor cursor) {
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
        public View getView(final int position,View view,ViewGroup position2) {
            if (view == null) {
                layoutInflater = LayoutInflater.from(context);
                view = layoutInflater.inflate(R.layout.swipe_layout, null);
            }
            final TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            cursor.moveToPosition(position);
            String date  = cursor.getString(cursor.getColumnIndex("start_date"));
            String placeName = cursor.getString(cursor.getColumnIndex("destination"));
            final int schedule_id = cursor.getInt(cursor.getColumnIndex("schedule_id"));
            final String textContent = new String(date + " " + placeName);
            tv_name.setText(textContent);
            tv_name.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    intent = new Intent(MyScheduleActivity.this,MyScheduleDetailsActivity.class);
                    bundle = new Bundle();
                    bundle.putString("data",textContent);
                    bundle.putInt("schedule_id",schedule_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            });
            final SwipeListLayout sll_main = (SwipeListLayout) view
                    .findViewById(R.id.sll_main);
            TextView tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            sll_main.setOnSwipeStatusListener(new MyOnSlipStatusListener(
                    sll_main));
            tv_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    sll_main.setStatus(SwipeListLayout.Status.Close, true);
                    String message = "このスケジュール" + TB_M_002;
                    showAlertDiaglog(MyScheduleActivity.this,message,"注意",schedule_id);
                }
            });
            return view;
        }
    }
    private void showAlertDiaglog(final Context context, String message, String title,int schedule_id) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        final int scheduleId = schedule_id;
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbUnit.deleteSchedule(scheduleId);
                cursor = dbUnit.getSchedule();
                adapter = new ScheduleAdapter(MyScheduleActivity.this,cursor);
                my_schedule.setAdapter(adapter);
                toast = toast.makeText(MyScheduleActivity.this,TB_M_003,Toast.LENGTH_LONG);
                toast.show();
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
