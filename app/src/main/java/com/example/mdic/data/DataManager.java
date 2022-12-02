package com.example.mdic.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by janisharali on 25/12/16.
 */

@Singleton
public class DataManager {

    public static Long folder_parentId = null;
    public static Long current_card_id = null;

    private Context mContext;
    private DbHelper mDbHelper;
    private SharedPrefsHelper mSharedPrefsHelper;

    @Inject
    public DataManager(@ApplicationContext Context context,
                       DbHelper dbHelper,
                       SharedPrefsHelper sharedPrefsHelper) {
        mContext = context;
        mDbHelper = dbHelper;
        mSharedPrefsHelper = sharedPrefsHelper;
    }

    public void saveAccessToken(String accessToken) {
        mSharedPrefsHelper.put(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, accessToken);
    }

    public String getAccessToken() {
        return mSharedPrefsHelper.get(SharedPrefsHelper.PREF_KEY_ACCESS_TOKEN, null);
    }

/*    public Long createUser(User user) throws Exception {
        return mDbHelper.insertUser(user);
    }

    public User getUser(Long userId) throws Resources.NotFoundException, NullPointerException {
        return mDbHelper.getUser(userId);
    }*/


    /****************************************************FOLDER WRAPPER*************************************************************/

    public List<Folder> getAllFolder(Long parentId) {
        folder_parentId = parentId;
        return mDbHelper.getAllFolder(parentId);
    }

    public List<Folder> getAllFolder() {
        return mDbHelper.getAllFolder();
    }


    public void deleteFolder(Long id) {
        mDbHelper.deleteFolder(id);
    }

    public Long saveFolder(Long id, ContentValues contentValues) {
        return mDbHelper.saveFolder(id, folder_parentId, contentValues);
    }

    public List<Folder> getFolders(String selection, String[] selArgs) {
        return mDbHelper.getFolders(selection, selArgs);
    }

    public void getFolders(List<Folder> folderList, String selection, String[] selArgs) {
        mDbHelper.getFolders(folderList, selection, selArgs);
    }

    public Folder loadByEntityIdFolder(Long id) {
        return mDbHelper.loadByEntityIdFolder(id);
    }


    /****************************************************CARD WRAPPER*************************************************************/

    public void deleteCard(Long id) {
        mDbHelper.deleteCard(id);
    }

    public Long saveCard(Long id, ContentValues contentValues) {
        return mDbHelper.saveCard(id, contentValues);
    }

    public List<Card> getAllCards(Long folderId) {
        return mDbHelper.getAllCards(folderId);
    }

    public List<Card> getAllCardsForReview(Long folderId) {
        return mDbHelper.getAllCardsForReview(folderId);
    }

    public List<Card> getAllChildCards(Long folderId) {
        return mDbHelper.getAllChildCards(folderId);
    }


    public Card loadByEntityIdCard(Long id) {
        return mDbHelper.loadByEntityIdCard(id);
    }

    public void setLike(Long id, boolean like) {
        mDbHelper.setLike(id, like);
    }

    public void switch_card_show(long id) {
        mDbHelper.switch_card_show(id);
    }


}