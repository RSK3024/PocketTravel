//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-Myプラン画面
//  名称：Myプラン画面プログラム
//  説明：Myプラン画面を表示するクラス
//　
//　作成：2017/06/19 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.widget.Toast.makeText;
import static jp.co.ivis.pockettravel.MessageList.TB_M_008;
import static jp.co.ivis.pockettravel.MessageList.TB_M_010;
import static jp.co.ivis.pockettravel.MessageList.TB_M_013;
import static jp.co.ivis.pockettravel.MessageList.TB_M_020;
import static jp.co.ivis.pockettravel.MessageList.TB_M_021;
import static jp.co.ivis.pockettravel.MessageList.TB_M_022;

/**
 * Myプラン画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 *
 */
public class CreatePlanActivity extends Activity {

    private View dropDownLayout;
    private TextView destinationText;
    private TextView startDateText;
    private Button dropDownBtn;
    private Button startDateBtn;
    private Button rtnBtn;
    private Button nextBtn;
    private PopupWindow pop;
    private DropDownAdapter dropDownAdapter;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private Toast toast;
    private String startDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 画面を初期表示し、各コンポーネントのイベントを設定する。
     *
     * @param savedInstanceState 遷移前のページからのIntent
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_plan);

        dropDownLayout = findViewById(R.id.dropdown_layout);
        destinationText = (TextView) findViewById(R.id.destination_text);
        startDateText = (TextView) findViewById(R.id.startDate_text);
        startDateBtn = (Button) findViewById(R.id.startDate_btn);
        rtnBtn = (Button) findViewById(R.id.create_plan_rtn_btn);
        nextBtn = (Button) findViewById(R.id.create_plan_next_btn);
        dropDownBtn = (Button) findViewById(R.id.dropdown_btn);

        dbUnit = new DBUnit(this);

        cursor = dbUnit.getCity();

        dropDownAdapter = new DropDownAdapter(this,cursor);

        final ListView listView = new ListView(this);

        listView.setAdapter(dropDownAdapter);
        listView.setBackgroundColor(Color.parseColor("#FFFAFA"));

        dropDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop == null) {
                    pop = new PopupWindow(listView,dropDownLayout.getWidth(),cursor.getCount()*dropDownLayout.getHeight());
                    pop.showAsDropDown(dropDownLayout);
                } else {
                    if (pop.isShowing()) {
                        pop.dismiss();
                    }
                    else {
                        pop.showAsDropDown(destinationText);
                    }
                }
            }
        });

//        TBD
//
//        destinationEdit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();

                final int y = calendar.get(Calendar.YEAR);
                final int m = calendar.get(Calendar.MONTH);
                final int d = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(CreatePlanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (year < y) {
                            toast = toast.makeText(CreatePlanActivity.this,TB_M_020,Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if (year == y && monthOfYear < m) {
                            toast = toast.makeText(CreatePlanActivity.this,TB_M_021,Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else if (year == y && monthOfYear == m && dayOfMonth < d) {
                            toast = toast.makeText(CreatePlanActivity.this,TB_M_022,Toast.LENGTH_LONG);
                            toast.show();
                        }
                        else {
                            Date date = new Date();
                            date.setYear(year-1900);
                            date.setMonth(monthOfYear);
                            date.setDate(dayOfMonth);

                            startDate = sdf.format(date);
                            startDateText.setText(startDate);
                        }
                    }
                }, y, m, d).show();
            }
        });

        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreatePlanActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = destinationText.getText().toString();
                String startDate = startDateText.getText().toString();

                if (cityName == null || cityName.equals("")) {
                    toast = Toast.makeText(CreatePlanActivity.this,TB_M_008,Toast.LENGTH_LONG);
                    toast.show();
                }
                else if (startDate == null || startDate.equals("")) {
                    toast = makeText(CreatePlanActivity.this,TB_M_010,Toast.LENGTH_LONG);
                    toast.show();
                }
                else {

                    Date date = null;
                    try {
                        date = sdf.parse(startDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (checkDateVaild(date)) {
                        intent = new Intent(CreatePlanActivity.this,CreateModeActivity.class);
                        bundle = new Bundle();
                        bundle.putString("start_date",startDate);
                        bundle.putString("city_name",cityName);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    else {
                        toast = makeText(CreatePlanActivity.this,TB_M_013,Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        });

    }

    private boolean checkDateVaild(Date date) {
        boolean result = true;

        Date startDate = null,endDate =null;

        cursor = dbUnit.getSchedule();

        while (cursor.moveToNext()) {

            try {
                startDate = sdf.parse(cursor.getString(cursor.getColumnIndex("start_date")));
                endDate = sdf.parse(cursor.getString(cursor.getColumnIndex("end_date")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
                Calendar start = Calendar.getInstance();
                start.setTime(startDate);
                start.add(Calendar.DAY_OF_YEAR,-1);
                startDate = start.getTime();
                Calendar end = Calendar.getInstance();
                end.setTime(endDate);
                end.add(Calendar.DAY_OF_YEAR,1);
                endDate = end.getTime();

                if (date.before(endDate) && startDate.before(date)) {
                    result = false;
                    return result;
                }

        }
        return result;
    }

    private class DropDownAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater layoutInflater;
        private Cursor cursor;
        private TextView content;

        DropDownAdapter(Context context, Cursor cursor) {
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
            convertView = layoutInflater.inflate(R.layout.list_row,null);
            content = (TextView) convertView.findViewById(R.id.text_row);
            cursor.moveToPosition(position);
            final String editContent = cursor.getString(cursor.getColumnIndex("city_name"));
            content.setText(editContent);
            content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    destinationText.setText(editContent);
                    pop.dismiss();
                }
            });
            return convertView;
        }
    }

//    private class MyDatePicker extends DatePickerDialog {
//
//        public MyDatePicker(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
//            super(context, callBack, year, monthOfYear, dayOfMonth);
//        }
//
//        @Override
//        public void onDateChanged(DatePicker view, int year, int month, int day) {
//            Date date = new Date();
//            Date nextDate = new Date();
//            nextDate.setYear(year - 1900);
//            nextDate.setMonth(month);
//            nextDate.setDate(day);
//            if (view != null) {
//                if (nextDate.getTime() - date.getTime() <= 0) {
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(0)).getChildAt(2).setEnabled(false);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(1)).getChildAt(2).setEnabled(false);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(2)).getChildAt(2).setEnabled(false);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(0)).getChildAt(1).setEnabled(false);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(1)).getChildAt(1).setEnabled(false);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(2)).getChildAt(1).setEnabled(false);
//                    super.updateDate(date.getYear() + 1900, date.getMonth(),(date.getDate()));  //更新picker
//                    super.onDateChanged(view, year, month, day);
//                } else {
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(0)).getChildAt(2).setEnabled(true);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(1)).getChildAt(2).setEnabled(true);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(2)).getChildAt(2).setEnabled(true);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(0)).getChildAt(1).setEnabled(true);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(1)).getChildAt(1).setEnabled(true);
//                    ((ViewGroup) ((ViewGroup) view.getChildAt(0)).getChildAt(2)).getChildAt(1).setEnabled(true);
//                    super.updateDate(year, month, day);
//                    super.onDateChanged(view, year, month, day);
//                }
//            }
//        }
//    }

}
