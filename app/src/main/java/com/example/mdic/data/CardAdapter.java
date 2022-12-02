package com.example.mdic.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.mdic.R;
import com.example.mdic.data.model.Card;
import java.util.List;

import static com.example.mdic.R.color.colorGreenLight;

public class CardAdapter extends ArrayAdapter {
    private Activity activity;
    public List<Card> cards;
    public boolean userClick = false;
    public View cardActivityView;

    LinearLayout wrapper_card_Insert;
    LinearLayout wrapper_card_list;

    private DataManager dataManager;


    EditText editTextKey;
    EditText editTextValue;
    EditText editTextPronunciation;
    EditText editTextDescription;

    public void setCards(List<Card> cards) {
        this.cards.clear();
        this.cards.addAll(cards);
    }

    public CardAdapter(@NonNull Activity input_activity, @NonNull List<Card> cards, DataManager dManager) {
        super(input_activity, R.layout.card_adapter, cards);
        dataManager = dManager;

        this.cards = cards;
        this.activity = input_activity;

        wrapper_card_Insert = input_activity.findViewById(R.id.wrapper_card_Insert);
        wrapper_card_list = input_activity.findViewById(R.id.wrapper_card_list);

        editTextKey = input_activity.findViewById(R.id.txt_key);
        editTextValue = input_activity.findViewById(R.id.txt_value);
        editTextPronunciation = input_activity.findViewById(R.id.txt_pronunciation);
        editTextDescription = input_activity.findViewById(R.id.txt_description);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {




        Card card = cards.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.card_adapter, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.key = convertView.findViewById(R.id.adapter_txt_key);
            viewHolder.value = convertView.findViewById(R.id.adapter_txt_value);
            viewHolder.pronunciation = convertView.findViewById(R.id.adapter_txt_pronunciation);
            viewHolder.description = convertView.findViewById(R.id.adapter_txt_description);
            viewHolder.like = convertView.findViewById(R.id.adapter_txt_like);
            viewHolder.dislike = convertView.findViewById(R.id.adapter_txt_dislike);
            viewHolder.adapterPosition = convertView.findViewById(R.id.adapter_txt_position);
            viewHolder.icon_more = convertView.findViewById(R.id.adapter_icon_more);
            viewHolder.card_row = convertView.findViewById(R.id.card_row);
            viewHolder.switch_show = convertView.findViewById(R.id.switch_show);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.fill(card);

        return convertView;
    }


    public class ViewHolder implements View.OnClickListener {
        public ImageView icon_more;
        public TextView key;
        public TextView value;
        public TextView description;
        public TextView pronunciation;
        public TextView like;
        public TextView dislike;
        public TextView adapterPosition;
        public LinearLayout card_row;
        public Switch switch_show;



        public void fill(final Card card){

            userClick = false;

            adapterPosition.setText(String.valueOf(getPosition(card)+1));
            key.setText(card.getKey());
            value.setText(card.getValue());
            pronunciation.setText(card.getPronunciation() == null ? "":card.getPronunciation());
            like.setText(String.valueOf(card.getLike()));
            dislike.setText(String.valueOf(card.getDisLike()));
            description.setText(card.getDescription() == null ? "":card.getDescription());
            switch_show.setChecked(card.getShow() == 1);

            switch_show.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    //Toast.makeText(activity, "start: id = "+card.getId(), Toast.LENGTH_SHORT).show();
                    if(userClick){
                        dataManager.switch_card_show(card.getId());
                        /**set current card in below line because dont load from db */
                        card.setShow(card.getShow()==1?0:1);
                    }

                    userClick = true;
                }
            });

            userClick = true;

            if(card.getLike() >= 8){
                card_row.setBackgroundColor(ContextCompat.getColor(activity , colorGreenLight));
            }else {
                card_row.setBackgroundColor(ContextCompat.getColor(activity , R.color.colorBlueLight));
            }


            final PopupMenu popupMenu = new PopupMenu(icon_more.getContext(), icon_more);
            popupMenu.inflate(R.menu.card_popup_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(menuItem.getItemId() == R.id.popup_option_delete){
                        dataManager.deleteCard(card.getId());
                        cards.remove(getPosition(card));
                        /** notifyDataSetChanged() is for array adapter */
                        notifyDataSetChanged();
                    }else if(menuItem.getItemId() == R.id.popup_option_edit){
                        DataManager.current_card_id = card.getId();
                        //activity.
                        //showUpdateDialog(card);
                        editTextKey.setText(card.getKey());
                        editTextValue.setText(card.getValue());
                        editTextPronunciation.setText(card.getPronunciation());
                        editTextDescription.setText(card.getDescription());

                        wrapper_card_Insert.setVisibility(View.VISIBLE);
                        wrapper_card_list.setVisibility(View.GONE);
                        //notifyDataSetChanged();
                    }
                    return false;
                }
            });



            icon_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                    //Toast.makeText(getContext(), "productId"+card.getId(), Toast.LENGTH_SHORT).show();
                }
            });


            card_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //cards.clear();
                    //cards = mdicDbHelper.getAll(card.getId());
                 /*   DataManager.card_parentId = card.getId();
                    cards.clear();
                    cards.addAll(dataManager.getCards(" parentId = "+card.getId(), null));
                    if(cards != null && cards.size() > 0){
                        notifyDataSetChanged();
                    }else {
                        List<Card> cardList =dataManager.getAllCards(DataManager.card_parentId);
                        if(cardList != null && cardList.size() >0 ){
                            activity.startActivity(new Intent(activity, CardActivity.class));
                        }else {
                            wrapper_add_btns.setVisibility(View.VISIBLE);
                            wrapper_card_list.setVisibility(View.GONE);
                        }

                    }*/
                    //Toast.makeText(getContext(), "card_row_clicked", Toast.LENGTH_SHORT).show();
                }
            });



        }

        @Override
        public void onClick(View view) {
           /* String phone = (String) view.getTag();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(view.equals(iccall)){
                intent.setData(Uri.parse("tel:"+ phone));
                getContext().startActivity(intent);
            }else if(view.equals(icsms)){
                intent.setData(Uri.parse("sms:"+ phone));
                getContext().startActivity(intent);
            }*/
        }
    }

}
