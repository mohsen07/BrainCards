package com.example.mdic.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.ApplicationContext;
import com.example.mdic.di.DatabaseInfo;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by janisharali on 25/12/16.
 */

@Singleton
public class DbHelper extends SQLiteOpenHelper {


    private static final String FOLDER_TABLE_NAME = "tb_folder";
    private static final String CARD_TABLE_NAME = "tb_card";

    private static final String[] All_COLUMNS =
            new String[]{"id","folderId","like", "disLike","show","key","value","description","pronunciation"};

    @Inject
    public DbHelper(@ApplicationContext Context context,
                    @DatabaseInfo String dbName,
                    @DatabaseInfo Integer version) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        tableCreateStatements(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+FOLDER_TABLE_NAME);
        onCreate(db);
    }

    private void tableCreateStatements(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS '"+ FOLDER_TABLE_NAME +"'" +
                    "( 'id' integer primary key autoincrement not null, "+
                    "  'parentId' integer , "+
                    "  'title' text not null , "+
                    "  'description' text , "+
                    "  'isFinal' integer not null "+
                    ")"
            );


            db.execSQL( "CREATE TABLE IF NOT EXISTS '"+ CARD_TABLE_NAME +"'" +
                    "( 'id' integer primary key autoincrement not null, "+
                    "  'folderId' integer not null , "+
                    "  'like' integer not null , "+
                    "  'disLike' integer not null , "+
                    "  'show' integer not null , "+
                    "  'key' text not null , "+
                    "  'value' text not null , "+
                    "  'description' text , "+
                    "  'pronunciation' text "+
                    ")"
            );

            /*+ USER_COLUMN_USER_CREATED_AT + " VARCHAR(10) DEFAULT " + getCurrentTimeStamp() + ", "
                    + USER_COLUMN_USER_UPDATED_AT + " VARCHAR(10) DEFAULT " + getCurrentTimeStamp() + "
                    */


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }



   /*****************************************************folder wrapper*********************************************************/

   public List<Folder> getAllFolder(){

       SQLiteDatabase db = getReadableDatabase();
       List<Folder> folderList = new ArrayList<>();
       Cursor cursor = db.rawQuery("SELECT * FROM '"+FOLDER_TABLE_NAME+"'  ", null);
       if(cursor.moveToFirst()){
           do{
               //process each row
               Folder folder = new Folder();
               folder.setId(cursor.getInt(cursor.getColumnIndex("id")));
               folder.setParentId(cursor.getLong(cursor.getColumnIndex("parentId")));
               folder.setTitle(cursor.getString(cursor.getColumnIndex("title")));
               folder.setDescription(cursor.getString(cursor.getColumnIndex("description")));
               folder.setIsFinal(cursor.getInt(cursor.getColumnIndex("isFinal")));
               folderList.add(folder);
           }while (cursor.moveToNext());
       }

       cursor.close();
       if(db.isOpen()){
           db.close();
       }
       return folderList;

   }

   public List<Folder> getAllFolder(Long parentId){

       SQLiteDatabase db = getReadableDatabase();
       List<Folder> folderList = new ArrayList<>();
       Cursor cursor = parentId != null ? db.query(FOLDER_TABLE_NAME, new String[]{"id","parentId","title", "description", "isFinal"} ," parentId = "+parentId, null, null , null, " ID ASC " )
               : db.rawQuery("SELECT * FROM '"+FOLDER_TABLE_NAME+"' where parentId is null ORDER BY ID ASC ", null);
       if(cursor.moveToFirst()){
           do{
               //process each row
               Folder folder = new Folder();
               folder.setId(cursor.getInt(cursor.getColumnIndex("id")));
               folder.setParentId(cursor.getLong(cursor.getColumnIndex("parentId")));
               folder.setTitle(cursor.getString(cursor.getColumnIndex("title")));
               folder.setDescription(cursor.getString(cursor.getColumnIndex("description")));
               folder.setIsFinal(cursor.getInt(cursor.getColumnIndex("isFinal")));
               folderList.add(folder);
           }while (cursor.moveToNext());
       }

       cursor.close();
       if(db.isOpen()){
           db.close();
       }
       return folderList;

   }


    public void deleteFolder(Long folderId){
        SQLiteDatabase db = getWritableDatabase();
        int count = db.delete(FOLDER_TABLE_NAME, "id = ?",new String[]{String.valueOf(folderId)});
        Log.i("Mohsen:", count +"rows deleted.");
        if(db.isOpen()){
            db.close();
        }
    }

    public Long saveFolder(Long id , Long folderParentId, ContentValues contentValues){
        SQLiteDatabase db = getWritableDatabase();
        if(id != null && id > 0){
            int count = db.update(FOLDER_TABLE_NAME, contentValues, " id = "+id, null );
            Log.i("Mohsen:", count +"rows updated.");
            return Long.valueOf(count);
        }else {
            contentValues.put("parentId", folderParentId);
            Long returnedId = db.insert(FOLDER_TABLE_NAME, null , contentValues);
            return returnedId;
        }
    }

    public List<Folder> getFolders(String selection , String[] selArgs){
        SQLiteDatabase db = getWritableDatabase();
        List<Folder> newFolderList = new ArrayList<>();

        Cursor cursor = db.query(FOLDER_TABLE_NAME, new String[]{"id","parentId","title", "description", "isFinal"} ,selection, selArgs, null , null, null );
        if(cursor.moveToFirst()){
            do{
                //process each row
                Folder folder = new Folder();
                folder.setId(cursor.getInt(cursor.getColumnIndex("id")));
                folder.setParentId(cursor.getLong(cursor.getColumnIndex("parentId")));
                folder.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                folder.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                folder.setIsFinal(cursor.getInt(cursor.getColumnIndex("isFinal")));
                newFolderList.add(folder);
            }while (cursor.moveToNext());
        }

        cursor.close();
        if(db.isOpen()){
            db.close();
        }
        return newFolderList;
    }

    public void getFolders(List<Folder> folderList, String selection , String[] selArgs){
        SQLiteDatabase db = getWritableDatabase();
        folderList.clear();
        Cursor cursor = db.query(FOLDER_TABLE_NAME, new String[]{"id","parentId","title", "description", "isFinal"} ,selection, selArgs, null , null, null );
        if(cursor.moveToFirst()){
            do{
                //process each row
                Folder folder = new Folder();
                folder.setId(cursor.getLong(cursor.getColumnIndex("id")));
                folder.setParentId(cursor.getLong(cursor.getColumnIndex("parentId")));
                folder.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                folder.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                folder.setIsFinal(cursor.getInt(cursor.getColumnIndex("isFinal")));
                folderList.add(folder);
            }while (cursor.moveToNext());
        }

        cursor.close();
        if(db.isOpen()){
            db.close();
        }
    }


    public Folder loadByEntityIdFolder(Long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(FOLDER_TABLE_NAME, new String[]{"id","parentId","title", "description", "isFinal"} ," id = "+id, null, null , null, null );
        if(cursor.moveToFirst()){

            //process each row
            Folder folder = new Folder();
            folder.setId(cursor.getInt(cursor.getColumnIndex("id")));
            folder.setParentId(cursor.getLong(cursor.getColumnIndex("parentId"))== 0 ? null :cursor.getLong(cursor.getColumnIndex("parentId")) );
            folder.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            folder.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            folder.setIsFinal(cursor.getInt(cursor.getColumnIndex("isFinal")));

            cursor.close();
            if(db.isOpen()){
                db.close();
            }
            return folder;
        }
        return null;
    }


    void addAllFolderChildren(Folder parentFolder, List<Folder> childFolder) {
        if(getAllFolder(parentFolder.getId())!= null && getAllFolder(parentFolder.getId()).size()>0 ) {
            for(Folder child : getAllFolder(parentFolder.getId())) {
                childFolder.add(child);
                addAllFolderChildren(child, childFolder);
            }
        }
    }

    /*******************************************Card wrapper***********************************************/


    public void deleteCard(Long id){
        SQLiteDatabase db = getWritableDatabase();
        int count = db.delete(CARD_TABLE_NAME, "id = ?",new String[]{String.valueOf(id)});
        Log.i("Mohsen:"+CARD_TABLE_NAME, count +"rows deleted.");
        if(db.isOpen()){
            db.close();
        }
    }

    public Long saveCard(Long id , ContentValues contentValues){
        SQLiteDatabase db = getWritableDatabase();
        if(id != null && id > 0){
            int count = db.update(CARD_TABLE_NAME, contentValues, " id = "+id, null );
            Log.i("Mohsen:", count +"rows updated.");
        }else {
            db.insert(CARD_TABLE_NAME, null , contentValues);
        }

        /*if(db.isOpen()){
            db.close();
        }*/
        //SQLiteDatabase dbRead = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM '"+CARD_TABLE_NAME+"' ", null);
        cursor.moveToLast();
        Integer returnedId = cursor.getInt(cursor.getColumnIndex("id"));
        if(db.isOpen()){
            db.close();
        }
        return Long.valueOf(returnedId);


    }




    public List<Card> getAllCardsForReview(Long folderId){
        SQLiteDatabase db = getReadableDatabase();
        List<Card> newList = new ArrayList<>();
        Cursor cursor = db.query(CARD_TABLE_NAME, All_COLUMNS ," folderId = "+folderId + " and like < 8 and show = 1 ", null, null , null, null );
        if(cursor.moveToFirst()){
            do{
                //process each row
                Card card = new Card();
                card.setId(cursor.getLong(cursor.getColumnIndex("id")));
                card.setFolderId(cursor.getLong(cursor.getColumnIndex("folderId")));
                card.setLike(cursor.getInt(cursor.getColumnIndex("like")));
                card.setDisLike(cursor.getInt(cursor.getColumnIndex("disLike")));
                card.setShow(cursor.getInt(cursor.getColumnIndex("show")));
                card.setKey(cursor.getString(cursor.getColumnIndex("key")));
                card.setValue(cursor.getString(cursor.getColumnIndex("value")));
                card.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                card.setPronunciation(cursor.getString(cursor.getColumnIndex("pronunciation")));

                newList.add(card);
            }while (cursor.moveToNext());
        }

        cursor.close();
        if(db.isOpen()){
            db.close();
        }
        return newList;

    }



    public List<Card> getAllCards(Long folderId){
        SQLiteDatabase db = getReadableDatabase();
        List<Card> newList = new ArrayList<>();
        Cursor cursor = folderId == null ? db.rawQuery("SELECT * FROM '"+ CARD_TABLE_NAME +"' ", null)
                :db.query(CARD_TABLE_NAME, All_COLUMNS ," folderId = "+folderId, null, null , null, null );
        //  : db.rawQuery("SELECT * FROM '"+ CARD_TABLE_NAME +"' where parentId is null ", null);
        if(cursor.moveToFirst()){
            do{
                //process each row
                Card card = new Card();
                card.setId(cursor.getLong(cursor.getColumnIndex("id")));
                card.setFolderId(cursor.getLong(cursor.getColumnIndex("folderId")));
                card.setLike(cursor.getInt(cursor.getColumnIndex("like")));
                card.setDisLike(cursor.getInt(cursor.getColumnIndex("disLike")));
                card.setShow(cursor.getInt(cursor.getColumnIndex("show")));
                card.setKey(cursor.getString(cursor.getColumnIndex("key")));
                card.setValue(cursor.getString(cursor.getColumnIndex("value")));
                card.setDescription(cursor.getString(cursor.getColumnIndex("description")));
                card.setPronunciation(cursor.getString(cursor.getColumnIndex("pronunciation")));

                newList.add(card);
            }while (cursor.moveToNext());
        }

        cursor.close();
        if(db.isOpen()){
            db.close();
        }
        return newList;

    }



    public List<Card> getAllChildCards(Long parentFolderId){
        List<Folder> allFolders  = new ArrayList<>();
        //call recursive method (addAllFolderChildren)
        addAllFolderChildren(loadByEntityIdFolder(parentFolderId),allFolders);
        List<Card> allCards = new ArrayList<>();
        for(Folder folder : allFolders){
            List<Card> cards = getAllCardsForReview(folder.getId());
            if(cards != null && cards.size()>0){
                allCards.addAll(cards);
            }
        }
        return allCards;
    }




    public Card loadByEntityIdCard(Long id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(CARD_TABLE_NAME, All_COLUMNS ," id = "+id, null, null , null, null );
        if(cursor.moveToFirst()){
            //process each row
            Card card = new Card();
            card.setId(cursor.getLong(cursor.getColumnIndex("id")));
            card.setFolderId(cursor.getLong(cursor.getColumnIndex("folderId")));
            card.setLike(cursor.getInt(cursor.getColumnIndex("like")));
            card.setDisLike(cursor.getInt(cursor.getColumnIndex("disLike")));
            card.setShow(cursor.getInt(cursor.getColumnIndex("show")));
            card.setKey(cursor.getString(cursor.getColumnIndex("key")));
            card.setValue(cursor.getString(cursor.getColumnIndex("value")));
            card.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            card.setPronunciation(cursor.getString(cursor.getColumnIndex("pronunciation")));

            cursor.close();
            if(db.isOpen()){
                db.close();
            }
            return card;
        }
        return null;
    }


    public void setLike(Long id, boolean like) {
        Card card = loadByEntityIdCard(id);
        if(like){
            card.setLike(card.getLike()+1);
        }else {
            card.setLike(0);
            card.setDisLike(card.getDisLike()+1);
        }
        saveCard(id, card.getContentValues());

    }

    public void switch_card_show(long id) {
        Card card = loadByEntityIdCard(id);
        card.setShow(card.getShow() == 1 ? 0:1);
        saveCard(id, card.getContentValues());
    }

}
