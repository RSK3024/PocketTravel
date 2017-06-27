package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static jp.co.ivis.pockettravel.MessageList.TB_M_001;
import static jp.co.ivis.pockettravel.MessageList.TB_M_004;

/**
 * Created by 杜 月 on 2017/06/21.
 */

public class ItemActivity extends Activity implements View.OnClickListener {

    private View view1;
    private View view2;
    private View view3;
    private View view4;
    private ViewPager upViewPager;
    private ViewPager downViewPager;
    private ImageButton[] upitemsBtn=new ImageButton[4];
    private ImageButton[] downitemsBtn=new ImageButton[4];
    private ImageView dotFirst;
    private ImageView dotFSecond;
    private ImageView dotFirst1;
    private ImageView dotFSecond1;
    private TextView[] upitemText = new TextView[4];
    private TextView[] downitemText = new TextView[4];
    private TextView titleText;
    private Button item_rtnBtn;
    private Button likeBtn;
    private DBUnit dbUnit;
    private List<View> upViews = new ArrayList<View>();
    private List<View> downViews = new ArrayList<View>();
    private List<ImageView> mDots;
    private List<ImageView> mDots1;
    private MyPagerAdapter myPagerAdapter;
    private MyPagerAdapter myPagerAdapter1;
    private int oldPosition;     //前のポジションをマーク
    private Toast toast;
    private Cursor cursor;
    private Intent intent;
    private Cursor cursor1;
    private Cursor cursor2;
    private Bundle bundle;
    private int placeId = 0;
    private String place;
    private String msg = null;
    private String msg_1 = null;


    public ItemActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item);

        hideBottomUIMenu();

        dbUnit = new DBUnit(ItemActivity.this);
        intent = getIntent();
        bundle = intent.getExtras();
        place = bundle.getString("place");

        titleText = (TextView) findViewById(R.id.place_name);
        titleText.setText(place);

        cursor = dbUnit.getPlaceByName(place);
        cursor.moveToFirst();
        placeId = cursor.getInt(cursor.getColumnIndex("place_id"));
        cursor1=dbUnit.getItem(placeId,0);
        cursor2=dbUnit.getItem(placeId,1);
        System.out.println(placeId + "------------------");
        if (cursor1.getCount() == 0 && cursor2.getCount() == 0) {
            toast = toast.makeText(ItemActivity.this,"項目がない",Toast.LENGTH_LONG);
            toast.show();
            intent = new Intent(ItemActivity.this,PlaceActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else {
            initViewpager();
            init();
        }
    }

    private void init() {

        item_rtnBtn = (Button) findViewById(R.id.item_rtn_btn);
        item_rtnBtn.setOnClickListener(this);
        likeBtn = (Button) findViewById(R.id.like_btn);
        likeBtn.setOnClickListener(this);
        for(int i=0;i<cursor1.getCount(); i++){
            final int ii = i;
            upitemsBtn[i].setVisibility(View.VISIBLE);
            upitemsBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor1.moveToPosition(ii);
                    String itemName = cursor1.getString(cursor1.getColumnIndex("item_name"));
                    intent = new Intent(ItemActivity.this, ItemDetailsActivity.class);
                    System.out.println(itemName);
                    bundle.putString("itemName",itemName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }
        for(int j=0;j<cursor2.getCount();j++){
            final int jj = j;
            downitemsBtn[j].setVisibility(View.VISIBLE);
            downitemsBtn[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cursor2.moveToPosition(jj);
                    intent = new Intent(ItemActivity.this, ItemDetailsActivity.class);
                    String itemName = cursor2.getString(cursor2.getColumnIndex("item_name"));
                    System.out.println(itemName);
                    bundle.putString("itemName",itemName);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }


     private void initViewpager() {

        view1= LayoutInflater.from(this).inflate(R.layout.item1,null);
        view2 = LayoutInflater.from(this).inflate(R.layout.item2,null);

        upitemsBtn[0] = (ImageButton) view1.findViewById(R.id.item_food_btn1);
        upitemsBtn[1] = (ImageButton) view1.findViewById(R.id.item_food_btn2);
        upitemsBtn[2] = (ImageButton) view2.findViewById(R.id.item_food_btn3);
        upitemsBtn[3] = (ImageButton) view2.findViewById(R.id.item_food_btn4);
        upitemText[0] = (TextView) view1.findViewById(R.id.item_text1);
         upitemText[1] = (TextView) view1.findViewById(R.id.item_text2);
         upitemText[2] = (TextView) view2.findViewById(R.id.item_text3);
         upitemText[3] = (TextView) view2.findViewById(R.id.item_text4);


         int i= 0;
        while (cursor1.moveToNext()) {
            String photoName = cursor1.getString(cursor1.getColumnIndex("item_photo"));
            int photo;
            if (photoName == null || photoName.equals("")) {
                photo = ItemActivity.this.getResources().getIdentifier("noimage","drawable","jp.co.ivis.pockettravel");
            }
            else {
                photo = ItemActivity.this.getResources().getIdentifier(photoName,"drawable","jp.co.ivis.pockettravel");
            }
            upitemsBtn[i].setImageResource(photo);
            String itemName = cursor1.getString(cursor1.getColumnIndex("item_name"));
            upitemText[i].setText(itemName);
            i++;
        }

        upViewPager = (ViewPager) findViewById(R.id.viewpager_show3);
         mDots = new ArrayList<ImageView>();
         dotFirst = (ImageView) findViewById(R.id.dot_first);
         dotFSecond = (ImageView) findViewById(R.id.dot_second);

         upViews.add(view1);
         mDots.add(dotFirst);
         if (cursor1.getCount() > 2) {
             upViews.add(view2);
             mDots.add(dotFSecond);
             dotFirst.setVisibility(View.VISIBLE);
             dotFSecond.setVisibility(View.VISIBLE);
         }

        oldPosition = 0;
        mDots.get(oldPosition).setImageResource(R.drawable.dot_normal);
        myPagerAdapter = new MyPagerAdapter(upViews);
        upViewPager.setAdapter(myPagerAdapter);
        upViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

        //down viepager
        view3= LayoutInflater.from(this).inflate(R.layout.item3,null);
        view4 = LayoutInflater.from(this).inflate(R.layout.item4,null);
        downitemsBtn[0] = (ImageButton) view3.findViewById(R.id.item_shop_btn1);
        downitemsBtn[1] = (ImageButton) view3.findViewById(R.id.item_shop_btn2);
        downitemsBtn[2] = (ImageButton) view4.findViewById(R.id.item_shop_btn3);
        downitemsBtn[3] = (ImageButton) view4.findViewById(R.id.item_shop_btn4);
         downitemText[0] = (TextView) view3.findViewById(R.id.item_text5);
         downitemText[1] = (TextView) view3.findViewById(R.id.item_text6);
         downitemText[2] = (TextView) view4.findViewById(R.id.item_text7);
         downitemText[3] = (TextView) view4.findViewById(R.id.item_text8);

        int ii = 0;
        while (cursor2.moveToNext()) {
            String photoName = cursor2.getString(cursor2.getColumnIndex("item_photo"));
            int photo;
            if (photoName == null || photoName.equals("")) {
                photo = ItemActivity.this.getResources().getIdentifier("noimage","drawable","jp.co.ivis.pockettravel");
            }
            else {
                photo = ItemActivity.this.getResources().getIdentifier(photoName,"drawable","jp.co.ivis.pockettravel");
            }
            downitemsBtn[ii].setImageResource(photo);
            String itemName = cursor2.getString(cursor2.getColumnIndex("item_name"));
            downitemText[ii].setText(itemName);
            ii++;
        }
        downViewPager = (ViewPager) findViewById(R.id.viewpager_show4);
         mDots1 = new ArrayList<ImageView>();
         dotFirst1 = (ImageView) findViewById(R.id.dot_first1);
         dotFSecond1 = (ImageView) findViewById(R.id.dot_second1);

        downViews.add(view3);
         mDots1.add(dotFirst1);

         if (cursor2.getCount() > 2) {
             downViews.add(view4);
             mDots1.add(dotFSecond1);
             dotFirst1.setVisibility(View.VISIBLE);
             dotFSecond1.setVisibility(View.VISIBLE);
         }

        oldPosition = 0;
        mDots1.get(oldPosition).setImageResource(R.drawable.dot_normal);
        myPagerAdapter1 = new MyPagerAdapter(downViews);
        downViewPager.setAdapter(myPagerAdapter1);
        downViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                mDots1.get(oldPosition).setImageResource(R.drawable.dot_focused);
                mDots1.get(position).setImageResource(R.drawable.dot_normal);
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
            case R.id.item_rtn_btn:
                intent = new Intent();
                intent.setClass(ItemActivity.this, PlaceActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.like_btn:
                msg = place + TB_M_004;
                showAlertDiaglog(ItemActivity.this, msg, "WARN");
                break;
            default:
                break;
        }
    }
    private void showAlertDiaglog(final Context context, String message, String title) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbUnit.addFond(placeId);
                msg_1 = place +TB_M_001;
                toast = toast.makeText(ItemActivity.this, msg_1, Toast.LENGTH_LONG);
                toast.show();
            }
        });
        alertDialogbuilder.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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
