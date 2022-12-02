package com.example.mdic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mdic.data.DataManager;
import com.example.mdic.data.FolderAdapter;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.component.ActivityComponent;
import com.example.mdic.di.component.DaggerActivityComponent;
import com.example.mdic.di.module.ActivityModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FolderActivity extends AppCompatActivity {

    @Inject
    DataManager dataManager;
    private ActivityComponent activityComponent;

    ListView listView;
    SearchView searchView;
    List<Folder> folderList = new ArrayList<>();
    FolderAdapter folderAdapter;

    LinearLayout wrapper_add_btns;
    LinearLayout wrapper_folder_list;

    LinearLayout btn_add_folder;
    LinearLayout btn_add_cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);


        getActivityComponent().inject(this);


        //mainRepository = new MainRepository(this);
        listView = findViewById(R.id.folderListView);
        /*searchView = findViewById(R.id.search_view);*/
        wrapper_add_btns = findViewById(R.id.wrapper_add_btns);
        wrapper_folder_list = findViewById(R.id.wrapper_folder_list);
        btn_add_folder = findViewById(R.id.btn_add_folder);
        btn_add_cards = findViewById(R.id.btn_add_cards);
        folderList = dataManager.getAllFolder(null);
        refreshDisplay();
       /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                dataManager.getFolders(folderList, "title LIKE '%" + s + "%' ", null);
                folderAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                dataManager.getFolders(folderList, "title LIKE '%" + s + "%' ", null);
                folderAdapter.notifyDataSetChanged();
                return false;
            }
        });*/


        btn_add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folderAdapter.showUpdateDialog(new Folder());
            }
        });

        btn_add_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FolderActivity.this, CardActivity.class);
                //intent.putExtra("folderId", DataManager.folder_parentId);
                startActivity(intent);
            }
        });

    }


    public ActivityComponent getActivityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(DemoApplication.get(this).getComponent())
                    .build();
        }
        return activityComponent;
    }


    private void refreshDisplay() {
        folderAdapter = new FolderAdapter(this, folderList, wrapper_add_btns,wrapper_folder_list,dataManager );
        listView.setAdapter(folderAdapter);

        if(folderList != null && folderList.size() > 0){
            wrapper_add_btns.setVisibility(View.GONE);
            wrapper_folder_list.setVisibility(View.VISIBLE);
        }/*else {
            List<Card> cardList =dataManager.getAllCards(DataManager.folder_parentId);
            if(cardList != null && cardList.size() >0 ){
                startActivity(new Intent(FolderActivity.this, CardActivity.class));
            }else {
                wrapper_add_btns.setVisibility(View.VISIBLE);
                wrapper_folder_list.setVisibility(View.GONE);
            }
        }*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.add("getAll").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                folderList = mainRepository.getAll(null);
                refreshDisplay();
                return false;
            }
        });*/

        menu.add("<").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                //Toast.makeText(FolderActivity.this, "mainRepository.current_parentId = "+ mainRepository.current_parentId, Toast.LENGTH_SHORT).show();
                if(DataManager.folder_parentId == null){
                    return false;
                }
                Folder folder = dataManager.loadByEntityIdFolder(DataManager.folder_parentId);
                //Toast.makeText(FolderActivity.this, "FolderRepository.current_parentId = "+ DataManager.folder_parentId, Toast.LENGTH_SHORT).show();

                DataManager.folder_parentId = folder.getParentId();
                folderList = dataManager.getAllFolder(folder.getParentId());
                folderAdapter.setFolders(folderList);
                folderAdapter.notifyDataSetChanged();
                wrapper_add_btns.setVisibility(View.GONE);
                wrapper_folder_list.setVisibility(View.VISIBLE);
                //refreshDisplay();

                return false;
            }
        }).setShowAsAction(1);
        menu.add("+").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //dbHelper.createFolder();
                //folderList = dbHelper.getAll();
                //refreshDisplay();
                folderAdapter.showUpdateDialog(new Folder());

                return false;
            }
        }).setShowAsAction(1);

        return super.onCreateOptionsMenu(menu);
    }
}
