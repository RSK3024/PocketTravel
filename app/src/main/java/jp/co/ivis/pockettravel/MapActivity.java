package jp.co.ivis.pockettravel;

/**
 * Created by tan.qinghua on 2017/06/21.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import static jp.co.ivis.pockettravel.MessageList.TB_M_012;
import static jp.co.ivis.pockettravel.MessageList.TB_M_015;
import static jp.co.ivis.pockettravel.MessageList.TB_M_016;
import static jp.co.ivis.pockettravel.MessageList.TB_M_017;
import static jp.co.ivis.pockettravel.MessageList.TB_M_018;


public class MapActivity extends Activity implements LocationListener {
    private static final String LOG_TAG = "MapActivity";
    private WebView routeGuideWebView;
    private Handler jsHandler = new Handler();
    private Location location;
    private int pageFrom;
    private String destination;
    private Intent intent;
    private Bundle bundle;
    private EditText editText;
    private Button endButton;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);

        routeGuideWebView = (WebView) findViewById(R.id.webView);
        endButton = (Button) findViewById(R.id.endNavi_btn);
        startButton = (Button) findViewById(R.id.start_btn);
        editText = (EditText) findViewById(R.id.editText);

        intent = getIntent();
        bundle = intent.getExtras();
        pageFrom = bundle.getInt("pageFrom");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            showAlertDialog(this, TB_M_015, "エラー");
        } else {
            // 現在の位置を取得する

            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location == null) {
                showAlertDialog(this, TB_M_016, "エラー");
            } else {
                // WebViewを設置するTB_M_018
                WebSettings webSettings = routeGuideWebView.getSettings();
                webSettings.setSaveFormData(false);
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setSupportZoom(false);
                routeGuideWebView.setWebChromeClient(new WebChromeClient() {
                    // JavaScriptのAlertメッセージを取得する
                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        Log.d(LOG_TAG, message);
                        result.confirm();
                        return true;
                    }
                });
                routeGuideWebView.addJavascriptInterface(new ControlJavaScriptInterface(), "routeGuide");

                String htmlUrl = null;
                intent = getIntent();
                htmlUrl = "file:///android_asset/SearchAndGuide.html";

                routeGuideWebView.loadUrl(htmlUrl);
            }
        }
        endButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDiaglogEnd(MapActivity.this,TB_M_012,"注意");
            }
        });

    }

    // エラーメッセージを表示する
    public void showAlertDialog(Context context, String message, final String title) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // アラートダイアログのタイトルを設定します
        alertDialogBuilder.setTitle(title);
        // アラートダイアログのメッセージを設定します
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        // アラートダイアログの肯定ボタンがクリックされた時に呼び出されるコールバックリスナーを登録します
        alertDialogBuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if("エラー".equals(title)){
                    finish();
                }

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // アラートダイアログを表示します
        alertDialog.show();
    }

    public void showAlertDiaglogEnd(final Context context, String message, String title) {
        AlertDialog.Builder alertDialogbuilder = new AlertDialog.Builder(context);

        alertDialogbuilder.setTitle(title);
        alertDialogbuilder.setMessage(message);
        alertDialogbuilder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pageFrom == 1) {
                    intent = new Intent(MapActivity.this,ItemDetailsActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else if(pageFrom == 2) {
                    intent = new Intent(MapActivity.this,MainActivity.class);
                    startActivity(intent);
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

    class ControlJavaScriptInterface {
        ControlJavaScriptInterface() {
        }

        // WebViewを表示するとき、現在地を初期化
        @JavascriptInterface
        public void routeGuideInit() {
            jsHandler.post(new Runnable() {
                public void run() {
                    routeGuideWebView.loadUrl(
                            "javascript:setOrigin('" + location.getLatitude() + "','" + location.getLongitude() + "')");
                }
            });
        }

        // pageFrom == 1の場合、目的地を初期化し、ルート案内を表示する
        @JavascriptInterface
        public void destinationInit() {
            jsHandler.post(new Runnable() {
                public void run() {
                    if(pageFrom == 1) {
                        destination = bundle.getString("destination");
                        routeGuideWebView.loadUrl("javascript:guide('" + destination + "')");
                    }

                }
            });
        }

        // 目的地を設定し、ルート案内を表示する
        @JavascriptInterface
        public void routeGuideAct() {
            jsHandler.post(new Runnable() {
                public void run() {
                    startButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            destination = editText.getText().toString();
                            if(destination==null || "".equals(destination)){
                        showAlertDialog(MapActivity.this, TB_M_017, "INFO");
                            }else{
                                routeGuideWebView.loadUrl("javascript:guide('" + destination + "')");
                            }

                        }
                    });
                }
            });
        }

        // 検索キーで目的地を取得できない場合、メッセージを出力する
        @JavascriptInterface
        public void notFound() {
            jsHandler.post(new Runnable() {
                public void run() {
                    showAlertDialog(MapActivity.this, TB_M_018, "INFO");
                }
            });
        }


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
