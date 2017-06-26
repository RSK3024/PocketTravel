//--------------------------------------------------
//
//  DBUnit.java
//
//  分類：DB-DB操作
//  名称：DB操作プログラム
//  説明：DBの操作
//　
//　作成：2017/06/19 劉　梓康
//
//  Copyright(c) 2009 IVIS All rights reserved.
//--------------------------------------------------
package jp.co.ivis.pockettravel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 *   データベースの操作を定義するクラス
 *
 *   @author 劉　梓康
 *   @version  1.0
 *
 */

public class DBUnit {

    //都市情報表名
    private final String TABLE_CITY = "city";

    //名所情報表名
    private final String TABLE_PLACE = "place";

    //項目情報表名
    private final String TABLE_ITEM = "item";

    //気に入る場所表名
    private final String TABLE_FOND = "fond";

    //スケジュール表名
    private final String TABLE_SCHEDULE = "schedule";

    //サブスケジュール表名
    private final String TABLE_SUB_SCHEDULE= "sub_schedule";

    //勧め路線表名
    private final String TABLE_ROUTE = "route";

    //サブ勧め路線表名
    private final String TABLE_SUB_ROUTE = "sub_route";

    //読めるデータベース対象
    private SQLiteDatabase readDatabase;

    //書けるデータベース対象
    private SQLiteDatabase writeDatabase;

    private DBHelper dbHelper;

    private Cursor cursor;

    /**
     * コンストラクタ
     * データベース操作対象の取得
     *
     * @param context　データを管理する用変数
     */
    public DBUnit(Context context) {

        //データベースのコピー
        dbHelper = new DBHelper(context);

        //読めるデータベース対象の取得
        readDatabase = dbHelper.getReadableDatabase();

        //書けるデータベース対象の取得
        writeDatabase = dbHelper.getWritableDatabase();
    }

    /**
     * 都市情報取得
     *
     * @return cursor 都市情報
     */
    public Cursor getCity() {

        cursor = readDatabase.query(TABLE_CITY,null,null,null,null,null,null);

        return cursor;
    }

    /**
     * 都市IDで、都市情報取得
     *
     * @param cityId　都市ID
     * @return cursor 都市情報
     */
    public Cursor getCityById(int cityId) {

        cursor = readDatabase.query(TABLE_CITY,null,"city_id = ?",new String[] {cityId + ""},null,null,null);

        return cursor;
    }

    /**
     * 都市名で、都市情報取得
     *
     * @param cityName　都市名
     * @return cursor　都市情報
     */
    public Cursor getCityIdByName(String cityName) {

        cursor = readDatabase.query(TABLE_CITY,null,"city_name=?",new String[] {cityName},null,null,null);

        return cursor;
    }

    /**
     * 都市IDで、都市情報取得
     *
     * @param cityId 都市ID
     * @return cursor 都市情報
     */
    public Cursor getPlaceByCityId(int cityId) {

        cursor = readDatabase.query(TABLE_PLACE,null,"city_id=?",new String[] {cityId + ""},null,null,null);

        return cursor;
    }

    /**
     * 名所IDで、名所情報取得
     *
     * @param placeId　名所ID
     * @return cursor 名所情報
     */
    public Cursor getPlaceById(int placeId) {

        cursor = readDatabase.query(TABLE_PLACE,null,"place_id = ?",new String[] {placeId + ""},null,null,null);

        return cursor;
    }

    /**
     * 名所IDで、名所名取得
     *
     * @param placeId　名所ID
     * @return cursor 名所名
     */
    public Cursor getPlaceNameByPlaceId(int placeId) {

        cursor = readDatabase.query(TABLE_PLACE,new String[] {"place_name"},"place_id = ?",new String[] {placeId + ""},null,null,null);

        return cursor;
    }


    /**
     * 名所名で、名所情報取得
     *
     * @param placeName　名所名
     * @return cursor　名所情報
     */
    public Cursor getPlaceByName(String placeName) {

        cursor = readDatabase.query(TABLE_PLACE,null,"place_name=?",new String[] {placeName},null,null,null);

        return cursor;
    }

    /**
     * 名所IDと区分で、項目情報取得
     *
     * @param placeId　名所ID
     * @param flag　区分
     * @return cursor　項目情報表
     */
    public Cursor getItem(int placeId,int flag) {

        cursor = readDatabase.query(TABLE_ITEM,null,"place_id=? and flag=?",new String[] {placeId+"",flag+""},null,null,null);

        return cursor;
    }

    /**
     * 項目名で、項目情報の取得
     *
     * @param itemName　項目名
     * @return cursor 項目情報
     */
    public Cursor getItemByName(String itemName) {

        cursor = readDatabase.query(TABLE_ITEM,null,"item_name = ?",new String[] {itemName},null,null,null);

        return cursor;
    }

    /**
     * 気に入る場所情報取得
     *
     * @return cursor　気に入る場所情報
     */
    public Cursor getFond() {

        cursor = readDatabase.query(TABLE_FOND,null,null,null,null,null,null);

        return cursor;
    }

    /**
     * 都市IDで、気に入る場所情報取得
     *
     * @param cityId　都市ID
     * @return cursor　気に入る場所情報
     */
    public Cursor getFondByCityID(int cityId) {

        //都市IDで、名所ID取得
        Cursor cursor = readDatabase.query(TABLE_PLACE,new String[] {"place_id"},"city_id=?",new String[] {cityId + ""},null,null,null);

        String[] selectionArgs = new String[cursor.getCount()];
        String selection = new String("place_id in (");

        int i = 0;

        //検索条件設定
        while (cursor.moveToNext()) {
            selectionArgs[i] = String.valueOf(cursor.getInt(cursor.getColumnIndex("place_id")));
            i++;
        }

        for (int j = 0; j < cursor.getCount() - 1; j++) {
            selection = selection + "?,";
        }
        selection = selection + "?)";

        //気に入る場所情報の取得
        cursor = readDatabase.query(TABLE_FOND,null,selection,selectionArgs,null,null,null);

        return cursor;
    }

    /**
     * 名所IDで、気に入る場所情報追加
     *
     * @param placeId　名所ID
     */
    public void addFond(int placeId) {

        ContentValues cv = new ContentValues();

        cv.put("place_id",placeId);

        writeDatabase.insert(TABLE_FOND,null,cv);

    }

    /**
     * 名所IDで、気に入る場所削除
     *
     * @param placeId　名所ID
     */
    public void deleteFond(int placeId) {

        writeDatabase.delete(TABLE_FOND,"place_id = ?",new String[] {placeId + ""});

    }

    /**
     * スケジュール情報取得
     *
     * @return cursor　スケジュール情報
     */
    public Cursor getSchedule() {

        cursor = readDatabase.query(TABLE_SCHEDULE,null,null,null,null,null,null);

        return cursor;
    }

    /**
     * 開始日で、スケジュールID取得
     *
     * @param startDate　開始日
     * @return cursor　スケジュールID
     */
    public Cursor getScheduleIdByStartDate(String startDate) {

        cursor = readDatabase.query(TABLE_SCHEDULE,new String[] {"schedule_id"},"start_date = ?",new String[] {startDate},null,null,null,null);

        return cursor;
    }

    /**
     * スケジュール作成
     *
     * @param startDate　開始日
     * @param peroid　旅行日数
     * @param endDate　終了日
     * @param cityName　都市名
     */
    public void addSchedule(String startDate, int peroid, String endDate, String cityName) {

        ContentValues cv = new ContentValues();

        cv.put("start_date",startDate);
        cv.put("peroid",peroid);
        cv.put("end_date",endDate);
        cv.put("destination",cityName);

        writeDatabase.insert(TABLE_SCHEDULE,null,cv);
    }

    /**
     * スケジュール削除
     *
     * @param scheduleId　スケジュールID
     */
    public void deleteSchedule(int scheduleId) {

        //当該スケジュールのすべてのサブスケジュールの削除
        deleteSubScheduleByScheduleId(scheduleId);

        //当該スケジュールの削除
        writeDatabase.delete(TABLE_SCHEDULE,"schedule_id = ?",new String[] {scheduleId + ""});
    }

    /**
     * スケジュールIDで、サブスケジュール情報取得
     *
     * @param scheduleId　スケジュールID
     * @return cursor サブスケジュール情報
     */
    public Cursor getSubSchedule(int scheduleId) {

        cursor = readDatabase.query(TABLE_SUB_SCHEDULE,null,"schedule_id = ?",new String[] {scheduleId + ""},null,null,null);

        return cursor;
    }

    /**
     * 予定の日付で、サブスケジュール情報取得
     *
     * @param subScheduleDate　予定の日付
     * @return cursor　サブスケジュール情報
     */
    public Cursor getSubScheduleBySubScheduleDate(String subScheduleDate) {

        cursor = readDatabase.query(TABLE_SUB_SCHEDULE,null,"sub_schedule_date = ?",new String[] {subScheduleDate},null,null,null);

        return cursor;
    }

    public void updateSubSchedule(String placeName,String subScheduleDate) {

        ContentValues cv = new ContentValues();

        cv.put("place_name",placeName);

        writeDatabase.update(TABLE_SUB_SCHEDULE,cv,"sub_schedule_date = ?",new String[] {subScheduleDate});
    }

    /**
     * サブスケジュール作成
     *
     * @param subScheduleDate　予定日付
     * @param placeName　名所名
     * @param scheduleId　スケジュールID
     */
    public void addSubSchedule(String  subScheduleDate,String placeName,int scheduleId) {

        ContentValues cv = new ContentValues();

        cv.put("sub_schedule_date",subScheduleDate);
        cv.put("place_name",placeName);
        cv.put("schedule_id",scheduleId);

        writeDatabase.insert(TABLE_SUB_SCHEDULE,null,cv);
    }

    /**
     * スケジュールIDで、サブスケジュール削除
     *
     * @param scheduleId　スケジュールID
     */
    public void deleteSubScheduleByScheduleId(int scheduleId) {

        writeDatabase.delete(TABLE_SUB_SCHEDULE,"schedule_id = ?",new String[] {scheduleId + ""});

    }

    /**
     * サブスケジュールIDで、サブスケジュール削除
     *
     * @param subScheduleId　サブスケジュールID
     */
    public void deleteSubScheduleBySubScheduleId(int subScheduleId) {

        writeDatabase.delete(TABLE_SUB_SCHEDULE,"sub_schedule_id = ?",new String[] {subScheduleId + ""});

    }

    /**
     * 都市IDで、勧め路線情報取得
     *
     * @param cityId　都市ID
     * @return cursor 勧め路線情報
     */
    public Cursor getRoute(int cityId) {

        cursor = readDatabase.query(TABLE_ROUTE,null,"city_id = ?",new String[] {cityId + ""},null,null,null);

        return cursor;
    }

    /**
     * 進め路線IDで、サブ勧め路線情報取得
     *
     * @param routeId　勧め路線ID
     * @return cursor サブ勧め路線情報
     */
    public Cursor getSubRoute(int routeId) {

        cursor = readDatabase.query(TABLE_SUB_ROUTE,null,"route_id = ?",new String[] {routeId + ""},null,null,"sub_route_day");

        return  cursor;
    }

}
