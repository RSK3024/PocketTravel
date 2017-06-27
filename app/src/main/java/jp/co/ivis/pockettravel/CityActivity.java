package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CityActivity extends Activity implements View.OnClickListener{

    private ViewPager mViewpagerFirst;
    private List<View> mViews = new ArrayList<View>();
    private LayoutInflater mInflater;
    private List<ImageView> mDots;
    private MyPagerAdapter myPagerAdapter;
    private Button returnBtn;
    private Button likelistBtn;
    private ImageButton[] citiesBtn = new ImageButton[8];
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private int oldPosition;


    public CityActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        dbUnit = new DBUnit(CityActivity.this);
        initViewpager();
        init();

    }

    private void init() {

        returnBtn = (Button) findViewById(R.id.city_rtn_btn);
        likelistBtn = (Button) findViewById(R.id.like_list_btn);
        returnBtn.setOnClickListener(this);
        likelistBtn.setOnClickListener(this);
        for (int i = 0; i<8; i++ ) {
            citiesBtn[i].setOnClickListener(this);
        }
    }

    private void initViewpager() {

        mViewpagerFirst = (ViewPager) findViewById(R.id.viewpager_show);
        mInflater = LayoutInflater.from(CityActivity.this);
        View view1 = mInflater.inflate(R.layout.city1, null);
        View view2 = mInflater.inflate(R.layout.city2, null);
        citiesBtn[0] = (ImageButton) view1.findViewById(R.id.btn_1);
        citiesBtn[1] = (ImageButton) view1.findViewById(R.id.btn_2);
        citiesBtn[2] = (ImageButton) view1.findViewById(R.id.btn_3);
        citiesBtn[3] = (ImageButton) view1.findViewById(R.id.btn_4);
        citiesBtn[4] = (ImageButton) view2.findViewById(R.id.btn_5);
        citiesBtn[5] = (ImageButton) view2.findViewById(R.id.btn_6);
        citiesBtn[6] = (ImageButton) view2.findViewById(R.id.btn_7);
        citiesBtn[7] = (ImageButton) view2.findViewById(R.id.btn_8);
        cursor = dbUnit.getCity();

        int i = 0;
        while (cursor.moveToNext()) {
            String photoName = cursor.getString(cursor.getColumnIndex("city_photo"));
            int photo = CityActivity.this.getResources().getIdentifier(photoName,"drawable","jp.co.ivis.pockettravel");
            citiesBtn[i].setImageResource(photo);
            i++;
        }

        mViews.add(view1);
        mViews.add(view2);
        mDots = new ArrayList<ImageView>();
        ImageView dotFirst = (ImageView) findViewById(R.id.dot_first);
        ImageView dotFSecond = (ImageView) findViewById(R.id.dot_second);
        mDots.add(dotFirst);
        mDots.add(dotFSecond);
        oldPosition = 0;
        mDots.get(oldPosition).setImageResource(R.drawable.dot_normal);
        myPagerAdapter = new MyPagerAdapter(mViews);
        mViewpagerFirst.setAdapter(myPagerAdapter);
        mViewpagerFirst.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                mDots.get(oldPosition).setImageResource(R.drawable.dot_focused);
                mDots.get(position).setImageResource(R.drawable.dot_normal);
                oldPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.city_rtn_btn:
                 intent = new Intent();
                 intent.setClass(CityActivity.this,MainActivity.class);
                 startActivity(intent);
                 break;
             case R.id.like_list_btn:
                 intent = new Intent();
                 intent.setClass(CityActivity.this,LikeListActivity.class);
                 startActivity(intent);
                 break;
             default:
                 intent = new Intent();

                 for (int i = 0;i < citiesBtn.length; i++) {
                     if (v.getId() == citiesBtn[i].getId()) {
                         cursor.moveToPosition(i);
                         String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
                         bundle = new Bundle();
                         intent.setClass(CityActivity.this,PlaceActivity.class);
                         bundle.putString("city",cityName);
                         intent.putExtras(bundle);
                     }
                 }
                 startActivity(intent);
                 break;
             }
    }

}
