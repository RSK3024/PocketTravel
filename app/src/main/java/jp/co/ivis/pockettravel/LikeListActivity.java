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
 * Created by shao.wenbo on 2017/06/21.
 */

public class LikeListActivity extends Activity {
    private Set<SwipeListLayout> sets = new HashSet<>();

    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Button rtnBtn;
    private Toast toast;
    private ListView like_list;
    private LikeListActivity.LikeAdapter adapter;
    private String place_name;
    private String city_name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.like_list);

        like_list = (ListView) findViewById(R.id.like_list);
        rtnBtn = (Button) findViewById(R.id.like_list_ret_btn);

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(LikeListActivity.this,CityActivity.class);
                startActivity(intent);
            }
        });

        dbUnit = new DBUnit(this);
        cursor = dbUnit.getFond();

        adapter = new LikeAdapter(this,cursor);

        like_list.setAdapter( adapter);
        like_list.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                //若有其他的item的状态为Open，则Close，然后移除
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

    class LikeAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater layoutInflater;
        private Cursor cursor;

        LikeAdapter(Context context, Cursor cursor) {
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
                view = LayoutInflater.from(LikeListActivity.this).inflate(
                        R.layout.swipe_layout, null);
            }
            final TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            cursor.moveToPosition(position);
            final int place_id = cursor.getInt(cursor.getColumnIndex("place_id"));
            Cursor cursor1 = dbUnit.getPlaceNameByPlaceId(place_id);
            cursor1.moveToFirst();
            place_name = cursor1.getString(cursor1.getColumnIndex("place_name"));
            cursor1 = dbUnit.getPlaceByName(place_name);
            cursor1.moveToFirst();
            int cityId = cursor1.getInt(cursor1.getColumnIndex("city_id"));
            cursor1 = dbUnit.getCityById(cityId);
            cursor1.moveToFirst();
            city_name = cursor1.getString(cursor1.getColumnIndex("city_name"));
            final String textContent = new String(city_name + "-" + place_name);
            tv_name.setText(textContent);
            final SwipeListLayout sll_main = (SwipeListLayout) view
                    .findViewById(R.id.sll_main);
            TextView tv_delete = (TextView) view.findViewById(R.id.tv_delete);
            sll_main.setOnSwipeStatusListener(new LikeListActivity.MyOnSlipStatusListener(
                    sll_main));
            tv_delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    sll_main.setStatus(SwipeListLayout.Status.Close, true);
                    String message = "この項目" + TB_M_002;
                    showAlertDiaglog(LikeListActivity.this,message,"注意",place_id);

                }
            });
            return view;
        }
    }
    private void showAlertDiaglog(final Context context, String message, String title,int place_id) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        final int placeId = place_id;
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dbUnit.deleteFond(placeId);
                cursor = dbUnit.getFond();
                adapter = new LikeAdapter(LikeListActivity.this,cursor);
                like_list.setAdapter(adapter);
                toast = toast.makeText(LikeListActivity.this,TB_M_003,Toast.LENGTH_LONG);
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
