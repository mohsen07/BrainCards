package com.example.mdic;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mdic.data.DataManager;
import com.example.mdic.data.FolderAdapter;
import com.example.mdic.data.FolderAdapter2;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;
import com.example.mdic.di.component.ActivityComponent;
import com.example.mdic.di.component.DaggerActivityComponent;
import com.example.mdic.di.module.ActivityModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;


public class ExamActivity extends AppCompatActivity {

    @Inject
    DataManager dataManager;
    private ActivityComponent activityComponent;

    ListView listView;
    List<Folder> folderList = new ArrayList<>();
    FolderAdapter folderAdapter;

    LinearLayout wrapper_show_cards;
    LinearLayout wrapper_root_folders;

    LinearLayout btn_start;
    LinearLayout btn_back;

    LinearLayout wrapper_like;
    LinearLayout wrapper_dislike;
    LinearLayout show_value;

    List<Card> cardListLooper = new ArrayList<>();
    Card currentCard = null;
    Integer currentCardIndex =0;

    TextView all_card_number;
    TextView current_card_number;
    TextView exam_key;
    TextView exam_value;

    boolean review = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        if(getIntent().getBooleanExtra("review", false)){
            review = true;
        }

        getActivityComponent().inject(this);

        all_card_number = findViewById(R.id.all_card_number);
        current_card_number = findViewById(R.id.current_card_number);
        exam_key = findViewById(R.id.exam_key);
        exam_value = findViewById(R.id.exam_value);
        show_value = findViewById(R.id.show_value);
        show_value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCard != null)
                    exam_value.setVisibility(exam_value.getVisibility() == View.VISIBLE ? View.INVISIBLE: View.VISIBLE);
            }
        });

        //mainRepository = new MainRepository(this);
        listView = findViewById(R.id.folderListView);
        wrapper_show_cards = findViewById(R.id.wrapper_show_cards);
        wrapper_root_folders = findViewById(R.id.wrapper_root_folders);

        wrapper_like = findViewById(R.id.wrapper_like);
        wrapper_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCard != null){
                    if(!review){
                        dataManager.setLike(currentCard.getId(), true);
                    }
                    setNextCard(true);
                }
            }
        });
        wrapper_dislike = findViewById(R.id.wrapper_dislike);
        wrapper_dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCard != null){
                    if(!review){
                        dataManager.setLike(currentCard.getId(), false);
                        setNextCard(true);
                    }else {
                        setNextCard(false);
                    }
                }
            }
        });

        if(review){
            wrapper_like.setBackground(ContextCompat.getDrawable(this, R.drawable.next_card));
            wrapper_dislike.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_card));
            //exam_value.setVisibility(View.VISIBLE);
            //show_value.setVisibility(View.INVISIBLE);
        }

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCard();
            }
        });

        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentCard == null){
                    //set order in review mod
                    cardListLooper =dataManager.getAllChildCards(DataManager.folder_parentId);
                    if(review){
                        Collections.sort(cardListLooper, new Comparator<Card>() {
                            @Override
                            public int compare(Card obj1, Card obj2) {
                                /**Note : for desending sort reverse obj1 and obj2*/
                                //return  obj1.getValue().compareToIgnoreCase(obj2.getValue());//for string
                                return Integer.compare(obj2.getDisLike(), obj1.getDisLike());//for integer
                            }
                        });
                    }

                    btn_start.setVisibility(View.INVISIBLE);
                    if(cardListLooper != null && cardListLooper.size() >0 ){
                        all_card_number.setText(String.valueOf(cardListLooper.size()));
                        setNextCard(true);
                    }else {
                        clearCard();
                        //Toast.makeText(ExamActivity.this, "no card", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        refreshDisplay();

    }



    public void clearCard(){
        wrapper_show_cards.setVisibility(View.GONE);
        wrapper_root_folders.setVisibility(View.VISIBLE);
        currentCard = null;
        currentCardIndex = 0;
        current_card_number.setText(String.valueOf(0));
        all_card_number.setText(String.valueOf(0));
        btn_start.setVisibility(View.VISIBLE);
        exam_key.setText("");
        exam_value.setText("");
    }

    private boolean setNextCard(boolean next) {
        //Toast.makeText(this, "index: "+currentCardIndex, Toast.LENGTH_SHORT).show();

            if(next){
                currentCardIndex++;
                if(currentCardIndex>cardListLooper.size()){
                    if(review){
                        currentCardIndex = 1;
                    }else{
                        clearCard();
                        return true;
                    }
                }

            }else {//never run this else when (review == false)
                currentCardIndex--;
                if(currentCardIndex==0){
                    currentCardIndex = cardListLooper.size();
                }
            }

            current_card_number.setText(String.valueOf(currentCardIndex));
            currentCard = cardListLooper.get(currentCardIndex-1);
            exam_key.setText(currentCard.getKey());
            exam_value.setText(currentCard.getValue());
            exam_value.setVisibility(View.INVISIBLE);
            return true;

     /*   }else{
            exam_value.setVisibility(View.INVISIBLE);
            if(currentCardIndex+1 <= cardListLooper.size()){
                current_card_number.setText(String.valueOf(currentCardIndex+1));
                currentCard = cardListLooper.get(currentCardIndex);
                exam_key.setText(currentCard.getKey());
                exam_value.setText(currentCard.getValue());
                Toast.makeText(this, "like: "+currentCard.getLike() +" | dislike: "+currentCard.getDisLike(), Toast.LENGTH_SHORT).show();
                currentCardIndex++;
            }else {

                Toast.makeText(this, "no card", Toast.LENGTH_SHORT).show();
            }
        }*/

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
        folderList = dataManager.getAllFolder(null);
        folderAdapter = new FolderAdapter(this, folderList, wrapper_show_cards,wrapper_root_folders,dataManager );
        listView.setAdapter(folderAdapter);

        wrapper_show_cards.setVisibility(View.GONE);
        wrapper_root_folders.setVisibility(View.VISIBLE);

    }


}
