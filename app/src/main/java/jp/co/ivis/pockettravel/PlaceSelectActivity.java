//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-日程DIY場所選択画面
//  名称：日程DIY場所選択画面プログラム
//  説明：日程DIY場所選択画面を表示するクラス
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
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static jp.co.ivis.pockettravel.MessageList.TB_M_008;
import static jp.co.ivis.pockettravel.MessageList.TB_M_009;
import static jp.co.ivis.pockettravel.MessageList.TB_M_011;
import static jp.co.ivis.pockettravel.MessageList.TB_M_019;
import static jp.co.ivis.pockettravel.MessageList.TB_M_022;

/**
 * 日程DIY場所選択画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */
public class PlaceSelectActivity extends Activity implements View.OnClickListener {

    private View fondView;
    private View allView;
    private View downView1;
    private View downView2;
    private LinearLayout fondLayout;
    private LinearLayout allLayout;
    private LinearLayout btnLayout;
    private ViewPager upViewPager;
    private ViewPager downViewPager;
    private ListView fondListView;
    private ListView allListView;
    private Button[] buttons;
    private Button rtnBtn;
    private Button nextBtn;
    private Button dateBtn;
    private List<View> upViews = new ArrayList<View>();
    private List<View> downViews = new ArrayList<View>();
    private Cursor fondCursor;
    private Cursor allCursor;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private FondAdpater fondAdapter;
    private AllAdpater allAdapter;
    private PagerAdapter upPagerAdapter;
    private PagerAdapter downPagerAdapter;
    private HashMap<Integer,Boolean> isFondSelected = new HashMap<Integer, Boolean>();
    private HashMap<Integer,Boolean> isAllSelected = new HashMap<Integer, Boolean>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private String cityName;
    private String startDate;
    private String placeName;
    private String subScheduleDate;
    private int scheduleId;
    private int peroid;
    private List<String> fondPlaceNameList = new ArrayList<String>();
    private List<String> allPlaceNameList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_select);

        hideBottomUIMenu();

        dbUnit = new DBUnit(this);

        fondView = LayoutInflater.from(this).inflate(R.layout.fond_tab,null);
        allView = LayoutInflater.from(this).inflate(R.layout.all_tab,null);
        downView1 = LayoutInflater.from(this).inflate(R.layout.down_tab_1,null);
        downView2 = LayoutInflater.from(this).inflate(R.layout.down_tab_2,null);
        fondLayout = (LinearLayout) findViewById(R.id.fond_tab_btn);
        allLayout = (LinearLayout) findViewById(R.id.all_tab_btn);
        upViewPager = (ViewPager) findViewById(R.id.place_select_view);
        downViewPager = (ViewPager) findViewById(R.id.btn_view);
        rtnBtn = (Button) downView1.findViewById(R.id.place_select_rtn_btn);
        nextBtn = (Button) downView1.findViewById(R.id.place_select_next_btn);
        dateBtn = (Button) downView1.findViewById(R.id.place_select_sure_date_btn);
        btnLayout = (LinearLayout) downView2.findViewById(R.id.date_select_btn_layout);

        fondLayout.setOnClickListener(this);
        allLayout.setOnClickListener(this);
        rtnBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        dateBtn.setOnClickListener(this);

        fondListView = (ListView) fondView.findViewById(R.id.fond_list);
        allListView = (ListView) allView.findViewById(R.id.all_list);

        intent = getIntent();
        bundle = intent.getExtras();

        cityName = bundle.getString("city_name");
        startDate = bundle.getString("start_date");
        scheduleId = bundle.getInt("schedule_id");
        peroid = bundle.getInt("peroid");

        buttons = new Button[peroid];
        for (int i = 0; i < peroid; i++) {
            Date date = null;
            try {
                date = sdf.parse(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR,i);
            date = calendar.getTime();
            subScheduleDate = sdf.format(date);

            cursor = dbUnit.getSubScheduleBySubScheduleDate(subScheduleDate);

            buttons[i] = new Button(PlaceSelectActivity.this);
            buttons[i].setId(i);
            if (cursor.getCount() == 0) {
                buttons[i].setText("DAY" + (i + 1));
            }
            else {
                buttons[i].setText("DAY" + (i + 1)+"   ○");
            }
            buttons[i].setOnClickListener(this);
            btnLayout.addView(buttons[i]);
        }

        cursor = dbUnit.getCityIdByName(cityName);
        cursor.moveToFirst();
        int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
        fondCursor = dbUnit.getFondByCityID(cityId);
        allCursor = dbUnit.getPlaceByCityId(cityId);

        fondAdapter = new FondAdpater(this,fondCursor,isFondSelected,fondPlaceNameList);
        allAdapter = new AllAdpater(this,allCursor,isAllSelected,allPlaceNameList);
        fondListView.setAdapter(fondAdapter);
        allListView.setAdapter(allAdapter);

        upViews.add(allView);
        upViews.add(fondView);

        if (fondCursor.getCount() == 0) {
            TextView textView = new TextView(this);
            textView.setText("気に入る場所がない");
            textView.setTextSize(15);
            textView.setGravity(Gravity.CENTER);

            fondListView.addFooterView(textView);
        }

        downViews.add(downView1);
        downViews.add(downView2);

        upPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return upViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            public void destoryItem(ViewGroup container, int position, Object object) {
                container.removeView(upViews.get(position));
            }

            public Object instantiateItem(ViewGroup container, int position) {
                View view = upViews.get(position);
                container.addView(view);
                return view;
            }

        };

        downPagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return downViews.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view==object;
            }

            public void destoryItem(ViewGroup container, int position, Object object) {
                container.removeView(downViews.get(position));
            }

            public Object instantiateItem(ViewGroup container, int position) {
                View view = downViews.get(position);
                container.addView(view);
                return view;
            }

        };

        upViewPager.setAdapter(upPagerAdapter);
        downViewPager.setAdapter(downPagerAdapter);

        isAllSelected = allAdapter.getIsSelected();
        allPlaceNameList = allAdapter.getPlaceNameList();
        isFondSelected = fondAdapter.getIsSelected();
        fondPlaceNameList = fondAdapter.getPlaceNameList();

        upViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                int item = upViewPager.getCurrentItem();
                switch (item) {
                    case 1:
                        for (int i = 0; i < isAllSelected.size(); i++) {
                            isAllSelected.put(i,false);
                        }
                        allAdapter.notifyDataSetChanged();
                        fondLayout.setBackgroundColor(Color.parseColor("#FFDAB9"));
                        allLayout.setBackgroundColor(Color.parseColor("#AFEEEE"));
                        break;
                    case 0:
                        for (int i = 0; i < isFondSelected.size(); i++ ) {
                            isFondSelected.put(i,false);
                        }
                        fondAdapter.notifyDataSetChanged();
                        allLayout.setBackgroundColor(Color.parseColor("#FFDAB9"));
                        fondLayout.setBackgroundColor(Color.parseColor("#AFEEEE"));
                        break;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fond_tab_btn:
                for (int i = 0; i < isAllSelected.size(); i++) {
                    isAllSelected.put(i,false);
                }
                allAdapter.notifyDataSetChanged();
                upViewPager.setCurrentItem(1);
                fondLayout.setBackgroundColor(Color.parseColor("#FFDAB9"));
                allLayout.setBackgroundColor(Color.parseColor("#AFEEEE"));
                break;
            case R.id.all_tab_btn:
                for (int i = 0; i < isFondSelected.size(); i++ ) {
                    isFondSelected.put(i,false);
                }
                fondAdapter.notifyDataSetChanged();
                upViewPager.setCurrentItem(0);
                allLayout.setBackgroundColor(Color.parseColor("#FFDAB9"));
                fondLayout.setBackgroundColor(Color.parseColor("#AFEEEE"));
                break;
            case R.id.place_select_sure_date_btn:
                downViewPager.setCurrentItem(1);
                break;
            case R.id.place_select_rtn_btn:
                showAlertDiaglog(PlaceSelectActivity.this,TB_M_009,"注意",1);
                break;
            case R.id.place_select_next_btn:
                cursor = dbUnit.getSubSchedule(scheduleId);
                if (cursor.getCount() == 0) {
                    toast = toast.makeText(PlaceSelectActivity.this,TB_M_022,Toast.LENGTH_SHORT);
                    toast.show();
                }
                else if (cursor.getCount() < peroid) {
                    showAlertDiaglog(PlaceSelectActivity.this,TB_M_019,"注意",2);
                }
                else {
                    intent = new Intent(PlaceSelectActivity.this,DiyRoutePlanDetailsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            default:
                for (int i = 0; i < buttons.length; i++) {
                    if (v.getId() == buttons[i].getId()) {
                        boolean result = false;
                        for (int z = 0; z < isFondSelected.size(); z++ ) {
                            if (isFondSelected.get(z)) {
                                result = true;
                            }
                        }
                        for (int z = 0; z < isAllSelected.size(); z++) {
                            if (isAllSelected.get(z)) {
                                result = true;
                            }
                        }
                        if (result) {
                            placeName = "";

                            int item = upViewPager.getCurrentItem();

                            switch (item) {
                                case 1:
                                    int ii = 0;
                                    for (int j = 0; j < isFondSelected.size(); j++) {
                                        if (isFondSelected.get(j)) {
                                            placeName = placeName + fondPlaceNameList.get(j);
                                            ii = j + 1;
                                            isFondSelected.put(j, false);
                                            break;
                                        }
                                    }
                                    for (int k = ii; k < isFondSelected.size(); k++) {
                                        if (isFondSelected.get(k)) {
                                            placeName = placeName + "、" + fondPlaceNameList.get(k);
                                        }
                                        isFondSelected.put(k,false);
                                    }
                                    fondAdapter.notifyDataSetChanged();
                                    break;
                                case 0:
                                    int jj = 0;
                                    for (int j = 0; j < isAllSelected.size(); j++) {
                                        if (isAllSelected.get(j)) {
                                            placeName = placeName + allPlaceNameList.get(j);
                                            jj = j + 1;
                                            isAllSelected.put(j, false);
                                            break;
                                        }
                                    }
                                    for (int k = jj; k < isAllSelected.size(); k ++) {
                                        if (isAllSelected.get(k)) {
                                            placeName = placeName + "、" + allPlaceNameList.get(k);
                                        }
                                        isAllSelected.put(k,false);
                                    }
                                    allAdapter.notifyDataSetChanged();
                                    break;
                            }

                            Date date = null;
                            try {
                                date = sdf.parse(startDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR,i);
                            date = calendar.getTime();
                            subScheduleDate = sdf.format(date);

                            cursor = dbUnit.getSubScheduleBySubScheduleDate(subScheduleDate);

                            if (cursor.getCount() == 0) {
                                dbUnit.addSubSchedule(subScheduleDate,placeName,scheduleId);
                                String content = buttons[i].getText().toString();
                                buttons[i].setText(content + "   ○");
                                downViewPager.setCurrentItem(0);
                            }
                            else {
                                String message = "DAY" + (i + 1) + TB_M_011;
                                showAlertDiaglog(PlaceSelectActivity.this,message,"注意",3);
                            }
                        }
                        else {
                            toast = toast.makeText(PlaceSelectActivity.this,TB_M_008,Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                break;

        }
    }

    private class FondAdpater extends BaseAdapter {

        private Context context;
        private Cursor cursor;
        private LayoutInflater layoutInflater;
        private HashMap<Integer,Boolean> isSelected;
        private List<String> placeNameList;

        public FondAdpater(Context context,Cursor cursor,HashMap<Integer,Boolean> isSelected,List<String> placeNameList) {
            this.context = context;
            this.cursor = cursor;
            this.isSelected = isSelected;
            this.placeNameList = placeNameList;
            initData();
        }

        private void initData() {
            for (int i = 0; i < cursor.getCount(); i++) {
                getIsSelected().put(i,false);
                placeNameList.add(i,"");
            }
        }

        class ViewHolder {
            TextView tv;
            CheckBox cb;
            LinearLayout ll;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.moveToPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            cursor.moveToPosition(position);
            layoutInflater = LayoutInflater.from(context);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_with_box,null);
                viewHolder = new ViewHolder();
                viewHolder.tv = (TextView) convertView.findViewById(R.id.item_row);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.item_box);
                viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.item_box_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final int placeId = cursor.getInt(cursor.getColumnIndex("place_id"));
            Cursor placeCursor = dbUnit.getPlaceNameByPlaceId(placeId);
            placeCursor.moveToFirst();

            String content = placeCursor.getString(placeCursor.getColumnIndex("place_name"));

            placeNameList.set(position,content);

            viewHolder.tv.setText(content);

            viewHolder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position,false);
                        setIsSelected(isSelected);
                    }
                    else {
                        isSelected.put(position,true);
                        setIsSelected(isSelected);
                    }
                    notifyDataSetChanged();
                }
            });

            viewHolder.cb.setChecked(getIsSelected().get(position));
            return convertView;
        }

        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
            this.isSelected = isSelected;
        }

        public List<String> getPlaceNameList() {
            return placeNameList;
        }

        public void setPlaceNameList(List<String> placeNameList) {
            this.placeNameList = placeNameList;
        }
    }

    private class AllAdpater extends BaseAdapter {

        private Context context;
        private Cursor cursor;
        private LayoutInflater layoutInflater;
        private HashMap<Integer,Boolean> isSelected;
        private List<String> placeNameList;

        public AllAdpater(Context context,Cursor cursor,HashMap<Integer,Boolean> isSelected,List<String> placeNameList) {
            this.context = context;
            this.cursor = cursor;
            this.isSelected = isSelected;
            this.placeNameList = placeNameList;
            initData();
        }

        private void initData() {
            for (int i = 0; i < cursor.getCount(); i++) {
                getIsSelected().put(i,false);
                placeNameList.add(i,"");
            }
        }

        class ViewHolder {
            TextView tv;
            CheckBox cb;
            LinearLayout ll;
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public Object getItem(int position) {
            return cursor.moveToPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            cursor.moveToPosition(position);
            layoutInflater = LayoutInflater.from(context);

            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_row_with_box,null);
                viewHolder = new ViewHolder();
                viewHolder.tv = (TextView) convertView.findViewById(R.id.item_row);
                viewHolder.cb = (CheckBox) convertView.findViewById(R.id.item_box);
                viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.item_box_layout);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String content = cursor.getString(cursor.getColumnIndex("place_name"));

            placeNameList.set(position,content);

            viewHolder.tv.setText(content);

            viewHolder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelected.get(position)) {
                        isSelected.put(position,false);
                        setIsSelected(isSelected);
                    }
                    else {
                        isSelected.put(position,true);
                        setIsSelected(isSelected);
                    }
                    notifyDataSetChanged();
                }
            });

            viewHolder.cb.setChecked(getIsSelected().get(position));
            return convertView;
        }

        public HashMap<Integer, Boolean> getIsSelected() {
            return isSelected;
        }

        public void setIsSelected(HashMap<Integer, Boolean> isSelected) {
            this.isSelected = isSelected;
        }

        public List<String> getPlaceNameList() {
            return placeNameList;
        }

        public void setPlaceNameList(List<String> placeNameList) {
            this.placeNameList = placeNameList;
        }
    }

    private void showAlertDiaglog(final Context context, String message, String title, final int flag) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (flag) {
                    case 1:
                        dbUnit.deleteSchedule(scheduleId);
                        intent = new Intent(PlaceSelectActivity.this,DiyRouteActivity.class);
                        bundle.remove("schedule_id");
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(PlaceSelectActivity.this,DiyRoutePlanDetailsActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 3:
                        dbUnit.updateSubSchedule(placeName,subScheduleDate);
                        downViewPager.setCurrentItem(0);
                        break;
                }

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
