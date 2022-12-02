package com.example.mdic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.mdic.data.FolderAdapter2;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.component.ActivityComponent;
import com.example.mdic.di.component.DaggerActivityComponent;
import com.example.mdic.di.module.ActivityModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FolderActivity2 extends AppCompatActivity {

    @Inject
    DataManager dataManager;
    private ActivityComponent activityComponent;

    RecyclerView recyclerView;
    SearchView searchView;
    List<Folder> folderList = new ArrayList<>();
    FolderAdapter2 folderAdapter2;

    LinearLayout wrapper_add_btns;
    LinearLayout wrapper_folder_list;

    LinearLayout btn_add_folder;
    LinearLayout btn_add_cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder2);


        //Toast.makeText(this, "on create folderActivity2", Toast.LENGTH_SHORT).show();
        
        getActivityComponent().inject(this);


        //mainRepository = new MainRepository(this);
        recyclerView = findViewById(R.id.folderRecyclerView);
        /*searchView = findViewById(R.id.search_view);*/
        wrapper_add_btns = findViewById(R.id.wrapper_add_btns);
        wrapper_folder_list = findViewById(R.id.wrapper_folder_list);
        btn_add_folder = findViewById(R.id.btn_add_folder);
        btn_add_cards = findViewById(R.id.btn_add_cards);



        folderList = dataManager.getAllFolder(DataManager.folder_parentId);
        refreshDisplay();


        btn_add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                folderAdapter2.showUpdateDialog(new Folder());
            }
        });

        btn_add_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FolderActivity2.this, CardActivity.class);
                intent.putExtra("parent_folder_id", DataManager.folder_parentId);
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
        folderAdapter2 = new FolderAdapter2(this, folderList, wrapper_add_btns,wrapper_folder_list,dataManager );
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration();
        recyclerView.setAdapter(folderAdapter2);

        if(folderList != null && folderList.size() > 0){
            wrapper_add_btns.setVisibility(View.GONE);
            wrapper_folder_list.setVisibility(View.VISIBLE);
        }

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

                backPressedCustom();

                return false;
            }
        }).setShowAsAction(1);
        menu.add("+").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //dbHelper.createFolder();
                //folderList = dbHelper.getAll();
                //refreshDisplay();
                //Toast.makeText(getBaseContext(), "  DataManager.folder_parentId:"+DataManager.folder_parentId, Toast.LENGTH_LONG).show();
                folderAdapter2.showUpdateDialog(new Folder());

                return false;
            }
        }).setShowAsAction(1);

        return super.onCreateOptionsMenu(menu);
    }



    private void backPressedCustom() {

        if(DataManager.folder_parentId == null){
            startActivity(new Intent(FolderActivity2.this, MainActivity.class));
        }else {
            Folder folder = dataManager.loadByEntityIdFolder(DataManager.folder_parentId);
            DataManager.folder_parentId = folder.getParentId();
            folderList = dataManager.getAllFolder(folder.getParentId());
            folderAdapter2.setFolders(folderList);
            folderAdapter2.notifyDataSetChanged();
            wrapper_add_btns.setVisibility(View.GONE);
            wrapper_folder_list.setVisibility(View.VISIBLE);
        }


    }


    @Override
    public void onBackPressed() {
        backPressedCustom();
        //super.onBackPressed();
    }



/*


    @Override
    protected void onResume() {
        Toast.makeText(this, "FolderActivity2: onResume()", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        Toast.makeText(this, "FolderActivity2: onDestroy()", Toast.LENGTH_SHORT).show();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


    @Override
    protected void onRestart() {
        Toast.makeText(this, "FolderActivity2: onRestart()", Toast.LENGTH_SHORT).show();
        System.out.println("====================================================================");
        System.out.println("**********************FolderActivity2: onStop()********************");
        System.out.println("====================================================================");
        super.onRestart();
    }


    @Override
    protected void onStop() {
        System.out.println("====================================================================");
        System.out.println("**********************FolderActivity2: onStop()********************");
        System.out.println("====================================================================");
        super.onStop();
    }

    @Override
    protected void onStart() {
        System.out.println("====================================================================");
        System.out.println("**********************FolderActivity2: onStart()********************");
        System.out.println("**********************DataManager.folder_parentId : "+ DataManager.folder_parentId  +"**************************");
        System.out.println("====================================================================");
        //refreshDisplay();
        //folderAdapter2.notifyDataSetChanged();
*/
/*

        if(folderList != null && folderList.size() > 0){
            wrapper_add_btns.setVisibility(View.GONE);
            wrapper_folder_list.setVisibility(View.VISIBLE);
        }*//*


     */
/*   folderList.clear();
        folderList.addAll(dataManager.getFolders(" parentId = "+DataManager.folder_parentId , null));
        refreshDisplay();*//*



        super.onStart();
    }
*/










}