package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by du.yue on 2017/06/21.
 */

public class PlaceActivity extends Activity implements View.OnClickListener {
    private ViewPager mViewpagerFirst;
    private List<View> mViews = new ArrayList<View>();
    private LayoutInflater mInflater;
    private List<ImageView> mDots;
    private MyPagerAdapter myPagerAdapter;
    private Button returnBtn;
    private ImageButton[] placesBtn = new ImageButton[8];
    private TextView[] placeText = new TextView[8];
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private int oldPosition;      //前のポジションをマーク

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place);
        dbUnit = new DBUnit(PlaceActivity.this);
        intent = getIntent();
        bundle = intent.getExtras();
        initViewpager();
        init();
    }

    private void init() {

        returnBtn = (Button) findViewById(R.id.place_rtn_btn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceActivity.this, CityActivity.class);
                startActivity(intent);
            }
        });
        for(int i=0;i<placesBtn.length;i++){
            placesBtn[i].setOnClickListener(this);
        }
    }

    private void initViewpager() {

        mViewpagerFirst = (ViewPager) findViewById(R.id.viewpager_show_place);
        mInflater = LayoutInflater.from(PlaceActivity.this);
        View view1 = mInflater.inflate(R.layout.place1, null);               // 一番目のページの都市表示するレイアウトをロード
        View view2 = mInflater.inflate(R.layout.place2, null);               // 二番目のページの都市表示するレイアウトをロード

        placesBtn[0] = (ImageButton) view1.findViewById(R.id.btn_21);             // レイアウトのコントロールを初期化
        placesBtn[1] = (ImageButton) view1.findViewById(R.id.btn_22);
        placesBtn[2] = (ImageButton) view1.findViewById(R.id.btn_23);
        placesBtn[3] = (ImageButton) view1.findViewById(R.id.btn_24);
        placesBtn[4] = (ImageButton) view2.findViewById(R.id.btn_25);
        placesBtn[5] = (ImageButton) view2.findViewById(R.id.btn_26);
        placesBtn[6] = (ImageButton) view2.findViewById(R.id.btn_27);
        placesBtn[7] = (ImageButton) view2.findViewById(R.id.btn_28);
        placeText[0] = (TextView) view1.findViewById(R.id.place_text1);
        placeText[1] = (TextView) view1.findViewById(R.id.place_text2);
        placeText[2] = (TextView) view1.findViewById(R.id.place_text3);
        placeText[3] = (TextView) view1.findViewById(R.id.place_text4);
        placeText[4] = (TextView) view2.findViewById(R.id.place_text5);
        placeText[5] = (TextView) view2.findViewById(R.id.place_text6);
        placeText[6] = (TextView) view2.findViewById(R.id.place_text7);
        placeText[7] = (TextView) view2.findViewById(R.id.place_text8);

        String City = bundle.getString("city");
        cursor  = dbUnit.getCityIdByName(City);
        cursor.moveToFirst();
        int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
        cursor = dbUnit.getPlaceByCityId(cityId);
        int i = 0;
        while (cursor.moveToNext()) {
            String photoName = cursor.getString(cursor.getColumnIndex("place_photo"));
            int photo = PlaceActivity.this.getResources().getIdentifier(photoName,"drawable","jp.co.ivis.pockettravel");
            placesBtn[i].setImageResource(photo);
            String placeName = cursor.getString(cursor.getColumnIndex("place_name"));
            placeText[i].setText(placeName);
            i++;
        }

        mViews.add(view1);
        mViews.add(view2);

        mDots = new ArrayList<ImageView>();      //スワイプマークする画像
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
            case R.id.place_rtn_btn:
                intent = new Intent();
                intent.setClass(PlaceActivity.this,CityActivity.class);
                startActivity(intent);
                break;
           case R.id.like_list_btn:
                intent = new Intent();
                intent.setClass(PlaceActivity.this,LikeListActivity.class);
                startActivity(intent);
               break;
            default:
                intent = new Intent();
                for (int i = 0;i < placesBtn.length; i++) {
                    if (v.getId() == placesBtn[i].getId()) {
                        cursor.moveToPosition(i);
                        String placeName = cursor.getString(cursor.getColumnIndex("place_name"));
                        intent.setClass(PlaceActivity.this,ItemActivity.class);
                        bundle.putString("place",placeName);
                        intent.putExtras(bundle);
                    }
                }
                startActivity(intent);
                break;
        }


    }
}
