package com.example.mdic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.mdic.data.DataManager;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.data.model.FolderCardViewModel;
import com.example.mdic.di.component.ActivityComponent;
import com.example.mdic.di.component.DaggerActivityComponent;
import com.example.mdic.di.module.ActivityModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    DataManager mDataManager;

    private ActivityComponent activityComponent;

    private List<Folder> externalFolderList;
    private List<Card> externalCardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityComponent().inject(this);

        LinearLayout linearLayout_btn_folders = findViewById(R.id.btn_folders);
        linearLayout_btn_folders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataManager.folder_parentId = null ;
                startActivity(new Intent(MainActivity.this, FolderActivity2.class));
            }
        });

        LinearLayout btn_backup_cards = findViewById(R.id.btn_backup_cards);
        btn_backup_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                FolderCardViewModel folderCardViewModel = new FolderCardViewModel();
                folderCardViewModel.setFolderList(mDataManager.getAllFolder());
                folderCardViewModel.setCardList(mDataManager.getAllCards(null));

                Date currentTime = Calendar.getInstance().getTime();
                try {
                    File myFile = new File(Environment.getExternalStorageDirectory(), "BrainCards");
                    if(!myFile.exists()){
                        myFile.mkdir();
                    }
                    crateExternalFile(myFile,
                            currentTime.toString()+".bc",gson.toJson(folderCardViewModel));
                    Toast.makeText(getBaseContext(), "Backup Generated: (~/BrainCards/"+currentTime.toString()+".bc)", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        LinearLayout btn_load_cards = findViewById(R.id.btn_load_cards);
        btn_load_cards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                FolderCardViewModel folderCardViewModel = null;
                try( Reader reader = new FileReader(Environment.getExternalStorageDirectory()+"/BrainCards/load.bc"); ) {
                    if(reader == null){
                        Toast.makeText(getBaseContext(), "file not found in (~/BrainCards/load.bc)",Toast.LENGTH_LONG).show();
                        return;
                    }
                    folderCardViewModel = gson.fromJson(reader, FolderCardViewModel.class);
                    Toast.makeText(getBaseContext(), "Load success: "+folderCardViewModel.getCardList().size(),Toast.LENGTH_LONG).show();

                    externalFolderList = folderCardViewModel.getFolderList();
                    externalCardList = folderCardViewModel.getCardList();




                } catch (IOException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


                for (Folder folder: getFoldersByParentId(0L)) {
                    reSaveFoldersByParentId(folder, 0L);
                }

            }
        });







        LinearLayout btn_exam = findViewById(R.id.btn_exam);
        btn_exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ExamActivity.class));
            }
        });


        LinearLayout btn_review = findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ExamActivity.class);
                intent.putExtra("review", true);
                startActivity(intent);
            }
        });


        //intent.putExtra("folderId", DataManager.folder_parentId);




    }

    private void reSaveFoldersByParentId(Folder folder, Long parentId) {
        Long oldId = folder.getId();
        //folder.setParentId(parentId);
        /** line below for set parentId before save folder object is needed*/
        mDataManager.folder_parentId = parentId== 0 ? null : parentId;
        Long newId = mDataManager.saveFolder(-1L, folder.getContentValues());
        folder.setId(newId);
        List<Folder> folderList = getFoldersByParentId(oldId);
        if(folderList != null && folderList.size()>0){
            for (Folder innerFolder:folderList) {
                reSaveFoldersByParentId(innerFolder, newId);
            }
        }else {
            //save cards
            for(Card oldCard:externalCardList){
                if(oldCard.getFolderId() == oldId){
                    oldCard.setFolderId(newId);
                   Long newCardId =  mDataManager.saveCard(-1L,oldCard.getContentValues());

                    System.out.println("Folder="+newId+"  Card="+newCardId);

                }
            }
        }
    }


    private List<Folder> getFoldersByParentId(Long parentId){
        List<Folder> foldersReturn = new ArrayList<>();
        for(Folder folder :externalFolderList){
            if(folder.getParentId() == parentId){
                foldersReturn.add(folder);
            }
        }
        return foldersReturn;
    }

    private void crateExternalFile(File dir, String fileName, String content) throws IOException {
        if(!dir.exists()){
            dir.mkdirs();
        }
        fileName = fileName.replace(' ', '_');
        File f = new File(dir, fileName);

        FileOutputStream fos = new FileOutputStream(f);
        fos.write(content.getBytes());
        fos.close();
        //Toast.makeText(this, "backupFileOn: "+dir.getAbsolutePath()+"/"+fileName, Toast.LENGTH_LONG).show();

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



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        createUser();
        getUser();
        mDataManager.saveAccessToken("ASDR12443JFDJF43543J543H3K543");

        String token = mDataManager.getAccessToken();
        if(token != null){
          //  mTvAccessToken.setText(token);
        }
    }

    private void createUser(){
        try {
           // mDataManager.createUser(new User("Ali", "1367, Gurgaon, Haryana, India"));
        }catch (Exception e){e.printStackTrace();}
    }

    private void getUser(){
        try {
            //User user = mDataManager.getUser(1L);
            //mTvUserInfo.setText(user.toString());
        }catch (Exception e){e.printStackTrace();}
    }






}
