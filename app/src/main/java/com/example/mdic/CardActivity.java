package com.example.mdic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mdic.data.CardAdapter;
import com.example.mdic.data.DataManager;
import com.example.mdic.data.FolderAdapter;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.component.ActivityComponent;
import com.example.mdic.di.component.DaggerActivityComponent;
import com.example.mdic.di.module.ActivityModule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

public class CardActivity extends AppCompatActivity {

    @Inject
    DataManager dataManager;
    private ActivityComponent activityComponent;

    ListView listView;
    List<Card> cardList = new ArrayList<>();

    EditText editTextKey;
    EditText editTextValue;
    EditText editTextPronunciation;
    EditText editTextDescription;
    Button btnSaveCard;


    LinearLayout wrapper_card_Insert;
    LinearLayout wrapper_card_list;

    CardAdapter cardAdapter;

    Long folderParentId =-1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        getActivityComponent().inject(this);


        folderParentId = getIntent().getLongExtra("parent_folder_id", -1L);


        wrapper_card_Insert = findViewById(R.id.wrapper_card_Insert);
        wrapper_card_list = findViewById(R.id.wrapper_card_list);
        editTextKey = findViewById(R.id.txt_key);
        editTextValue = findViewById(R.id.txt_value);
        editTextPronunciation = findViewById(R.id.txt_pronunciation);
        editTextDescription = findViewById(R.id.txt_description);
        Button btnCancelCard = findViewById(R.id.btn_cancel_card);
        btnCancelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wrapper_card_Insert.setVisibility(View.GONE);
                wrapper_card_list.setVisibility(View.VISIBLE);
                clearCardForm();
            }
        });
        btnSaveCard = findViewById(R.id.btn_save_card);
        btnSaveCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(DataManager.current_card_id == null){
                    Card card = new Card();
                    card.setKey(editTextKey.getText().toString().trim());
                    card.setValue(editTextValue.getText().toString().trim());
                    card.setPronunciation(editTextPronunciation.getText().toString().trim());
                    card.setDescription(editTextDescription.getText().toString().trim());
                    card.setFolderId(folderParentId);
                    Toast.makeText(CardActivity.this, folderParentId.toString(), Toast.LENGTH_SHORT).show();
                    card.setLike(0);
                    card.setDisLike(0);
                    card.setShow(0);

                    Long savedId = dataManager.saveCard(DataManager.current_card_id, card.getContentValues());
                    card.setId(savedId);

                    cardAdapter.cards.add(card);
                    cardAdapter.notifyDataSetChanged();
                }else {
                    Card current_card = new Card();
                    for(Card card:cardAdapter.cards){
                        if(card.getId() == DataManager.current_card_id){
                            current_card = card;
                        }
                    }

                    current_card.setKey(editTextKey.getText().toString().trim());
                    current_card.setValue(editTextValue.getText().toString().trim());
                    current_card.setPronunciation(editTextPronunciation.getText().toString().trim());
                    current_card.setDescription(editTextDescription.getText().toString().trim());

                    Long savedId = dataManager.saveCard(DataManager.current_card_id, current_card.getContentValues());

                    cardList = dataManager.getAllCards(folderParentId);
                    refreshDisplay();
                }

                wrapper_card_Insert.setVisibility(View.GONE);
                wrapper_card_list.setVisibility(View.VISIBLE);
                clearCardForm();
            }
        });


        listView = findViewById(R.id.cardListView);
        cardList = dataManager.getAllCards(folderParentId);

        if(cardList != null && cardList.size() > 0 ){
            wrapper_card_Insert.setVisibility(View.GONE);
            wrapper_card_list.setVisibility(View.VISIBLE);
        }else {
            wrapper_card_Insert.setVisibility(View.VISIBLE);
            wrapper_card_list.setVisibility(View.GONE);
        }

        refreshDisplay();
    }

    private void clearCardForm() {
        editTextKey.setText("");
        editTextValue.setText("");
        editTextPronunciation.setText("");
        editTextDescription.setText("");
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

        cardAdapter = new CardAdapter(this, cardList, dataManager );
        listView.setAdapter(cardAdapter);

        //listView.setAdapter(new ArrayAdapter<Card>(this, android.R.layout.simple_list_item_1, cardList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("new Card").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                wrapper_card_Insert.setVisibility(View.VISIBLE);
                wrapper_card_list.setVisibility(View.GONE);
                DataManager.current_card_id = null;
                return false;
            }
        }).setShowAsAction(1);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public void onBackPressed() {
        Folder folder = dataManager.loadByEntityIdFolder(DataManager.folder_parentId);
        DataManager.folder_parentId = folder.getParentId();
        startActivity(new Intent(CardActivity.this, FolderActivity2.class));
        //super.onBackPressed();
    }
}




