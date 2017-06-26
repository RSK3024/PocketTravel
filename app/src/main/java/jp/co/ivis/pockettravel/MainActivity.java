package jp.co.ivis.pockettravel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private Button cityBtn;
    private Button scheduleBtn;
    private Button createBtn;
    private Button mapBtn;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cityBtn = (Button) findViewById(R.id.city_btn);
        createBtn = (Button) findViewById(R.id.create_plan_btn);
        scheduleBtn = (Button) findViewById(R.id.schedule_btn);
        mapBtn = (Button) findViewById(R.id.map_btn);

        cityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this,CityActivity.class);
                startActivity(intent);
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,CreatePlanActivity.class);
                startActivity(intent);
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this,MyScheduleActivity.class);
                startActivity(intent);
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this,MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pageFrom",2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

}

