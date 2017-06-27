//--------------------------------------------------
//
//  CreatePlanActivity.java
//
//  分類：画面-路線プラン画面
//  名称：路線プラン画面プログラム
//  説明：路線プラン画面を表示するクラス
//　
//　作成：2017/06/20 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 路線プラン画面を表示するクラス
 *
 * @author 劉　梓康
 * @version 1.0
 */
public class CreateModeActivity extends Activity {

    private Button recoModeBtn;
    private Button diyModeBtn;
    private Button rtnBtn;
    private Intent intent;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_mode);

        hideBottomUIMenu();

        recoModeBtn = (Button) findViewById(R.id.recommend_btn);
        diyModeBtn = (Button) findViewById(R.id.diy_btn);
        rtnBtn = (Button) findViewById(R.id.create_mode_rtn_btn);

        intent = getIntent();
        bundle= intent.getExtras();

        recoModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreateModeActivity.this,RecommendRouteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        diyModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreateModeActivity.this,DiyRouteActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        rtnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(CreateModeActivity.this,CreatePlanActivity.class);
                startActivity(intent);
            }
        });

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
