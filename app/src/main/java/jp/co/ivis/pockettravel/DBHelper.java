//--------------------------------------------------
//
//  DBHelper.java
//
//  分類：DB-DB作成
//  名称：DB作成プログラム
//  説明：DBの作成あるいはコピー
//　
//　作成：2017/06/19 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------

package jp.co.ivis.pockettravel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *   データベースを作るまたはコピーするプログラム
 *
 *   @author 劉　梓康
 * 　@version 1.0
 *
 */

class DBHelper extends SQLiteOpenHelper {

    //データベースファイルのパス
    private static String DB_PATH = "data/data/jp.co.ivis.pockettravel/databases/";

    //データベース名
    private static String DB_NAME = "PockTravel";

    /**
     * コンストラクタ
     * システムに新しいデータベースを作って、自分のデータベースでオーバーライドする。
     *
     * @param context　データを管理する用の変数
     */
     DBHelper(Context context) {

        //親クラスのコンストラクタを実行する
        super(context, DB_NAME, null, 1);

        //システムにデータベースの存在の判断結果の取得
        boolean dbExists = checkDatabase();

        if (!dbExists) {  //システムにデータベースがすでに存在する場合

            this.getReadableDatabase();

            try {
                copyDatabase(context);
            } catch (IOException e) {
                throw new Error("データベースのコピーが失敗しました");
            }
        }
    }

    /**
     * データベースの存在の判断
     *
     * @return boolean result
     */
    private boolean checkDatabase() {

        //データベースの声明
        SQLiteDatabase checkDatabase = null;

        //判断結果
        boolean result = false;

        try {
            String myPath = DB_PATH + DB_NAME;
            //データベースの取得
            checkDatabase = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {

        }

        if (checkDatabase != null) {
            //データベースのクロス
            checkDatabase.close();
            result = true;
        }

        return result;
    }

    /**
     * I/O　StreamでDBファイルをコピーする。
     *
     * @param context　データを管理する用の変数
     * @throws IOException　IO異常
     */
    private void copyDatabase(Context context) throws IOException {

        InputStream inputStream = context.getAssets().open(DB_NAME);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream outputStream = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer,0,length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
