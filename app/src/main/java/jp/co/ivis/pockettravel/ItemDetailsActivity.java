package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by du.yue on 2017/06/22.
 */

public class ItemDetailsActivity extends Activity {
    private Button rtnBtn;
    private Cursor cursor;
    private DBUnit dbUnit;
    private Intent intent;
    private Bundle bundle;
    private String itemName = null;
    private ImageView imageView;
    private TextView testViewtop;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private Button item_details_rtnBtn;
    private String itemAddress;


    public ItemDetailsActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_details);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        testViewtop = (TextView) findViewById(R.id.item_details_textTop);
        imageView = (ImageView) findViewById(R.id.item_details_image);
        textView1 = (TextView) findViewById(R.id.item_details_text1);
        textView2 = (TextView) findViewById(R.id.item_details_text2);
        textView3 = (TextView) findViewById(R.id.item_details_text3);
        item_details_rtnBtn = (Button)findViewById(R.id.item_details_rtn_btn);
        item_details_rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent();
                intent.setClass(ItemDetailsActivity.this, ItemActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        dbUnit = new DBUnit(this);
        intent = getIntent();
        bundle = intent.getExtras();
        itemName = bundle.getString("itemName");
        cursor = dbUnit.getItemByName(itemName);
        cursor.moveToFirst();
        String itemPhoto = cursor.getString(cursor.getColumnIndex("item_photo"));
        int photo;
        if (itemPhoto == null || itemPhoto.equals("")) {
            photo = ItemDetailsActivity.this.getResources().getIdentifier("noimage","drawable","jp.co.ivis.pockettravel");
        }
        else {
            photo = ItemDetailsActivity.this.getResources().getIdentifier(itemPhoto,"drawable","jp.co.ivis.pockettravel");
        }
        itemAddress = cursor.getString(cursor.getColumnIndex("item_address"));
        String itemOpenTime = cursor.getString(cursor.getColumnIndex("item_open_time"));
        String itemTel = cursor.getString(cursor.getColumnIndex("item_tele"));
        testViewtop.setText(itemName);
        imageView.setImageResource(photo);
        textView1.setText(textView1.getText().toString() + itemAddress);
        textView1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ItemDetailsActivity.this,MapActivity.class);
                bundle.putInt("pageFrom",1);
                bundle.putString("destination",itemAddress);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        textView2.setText(textView2.getText().toString() + itemOpenTime);
        textView3.setText(textView3.getText().toString() + itemTel);
    }

}