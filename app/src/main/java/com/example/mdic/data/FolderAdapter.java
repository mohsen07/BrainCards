package com.example.mdic.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mdic.CardActivity;
import com.example.mdic.FolderActivity;
import com.example.mdic.R;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;

import java.util.List;

public class FolderAdapter extends ArrayAdapter {
    private Activity activity;
    private List<Folder> folders;

    private DataManager dataManager;

    //private FolderRepository dataManager;
    LinearLayout wrapper_add_btns;
    LinearLayout wrapper_folder_list;

    public void setFolders(List<Folder> folders) {
        this.folders.clear();
        this.folders.addAll(folders);
    }

    public FolderAdapter(@NonNull Activity input_activity, @NonNull List<Folder> folders, LinearLayout lnbtn , LinearLayout lnfldr,DataManager dManager) {
        super(input_activity, R.layout.folder_adapter, folders);
        dataManager = dManager;

        this.folders = folders;
        this.activity = input_activity;
        //this.dataManager = new FolderRepository(input_activity);
        wrapper_add_btns = lnbtn;
        wrapper_folder_list = lnfldr;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Folder folder = folders.get(position);
        ViewHolder viewHolder;
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.folder_adapter, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = convertView.findViewById(R.id.tv_title);
            viewHolder.icon_more = convertView.findViewById(R.id.icon_more);
            viewHolder.folder_row = convertView.findViewById(R.id.folder_row);
            //viewHolder.folder_row
           /* viewHolder.tv_phone = convertView.findViewById(R.id.mycontact_phone);
            viewHolder.tv_name = convertView.findViewById(R.id.mycontact_name);
            viewHolder.icsms = convertView.findViewById(R.id.mycontact_icsms);
            viewHolder.iccall = convertView.findViewById(R.id.mycontact_iccall);*/
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.fill(folder);

        return convertView;
    }


    public class ViewHolder implements View.OnClickListener {
        public ImageView icon_more;
        public TextView title;
        public RelativeLayout folder_row;
        /*public TextView tv_phone;
        public ImageView icsms, iccall;*/



        public void fill(final Folder folder){
            /*img_profile.setImageResource(contact.getId());*/
            title.setText(folder.getTitle());
            /*tv_phone.setText(contact.getPhoneNumber());
            iccall.setTag(contact.getPhoneNumber());
            icsms.setTag(contact.getPhoneNumber());
            icsms.setOnClickListener(this);
            iccall.setOnClickListener(this);*/


            final PopupMenu popupMenu = new PopupMenu(icon_more.getContext(), icon_more);
            popupMenu.inflate(R.menu.folder_popup_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    if(menuItem.getItemId() == R.id.popup_option_delete){
                        dataManager.deleteFolder(folder.getId());
                        folders.remove(getPosition(folder));
                        /** notifyDataSetChanged() is for array adapter */
                        notifyDataSetChanged();
                    }else if(menuItem.getItemId() == R.id.popup_option_edit){
                        showUpdateDialog(folder);
                    }
                    return false;
                }
            });



            icon_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupMenu.show();
                    //Toast.makeText(getContext(), "productId"+folder.getId(), Toast.LENGTH_SHORT).show();
                }
            });


            folder_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //folders.clear();
                    //folders = mdicDbHelper.getAll(folder.getId());


                    DataManager.folder_parentId = folder.getId();
                    //folders.clear();
                    //folders.addAll(dataManager.getFolders(" parentId = "+folder.getId(), null));


                    wrapper_add_btns.setVisibility(View.VISIBLE);
                    wrapper_folder_list.setVisibility(View.GONE);

                    /*
                    if(folders != null && folders.size() > 0){
                        notifyDataSetChanged();
                    }else {
                        List<Card> cardList =dataManager.getAllCards(DataManager.folder_parentId);
                        if(cardList != null && cardList.size() >0 ){
                            wrapper_add_btns.setVisibility(View.VISIBLE);
                            wrapper_folder_list.setVisibility(View.GONE);
                            //activity.startActivity(new Intent(activity, CardActivity.class));
                        }

                    }

                    */

                    //Toast.makeText(getContext(), "folder_row_clicked", Toast.LENGTH_SHORT).show();
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

    public void showUpdateDialog(final Folder folder) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.folder_update);
        changeDialogSize(dialog);


        final EditText inputTitle = dialog.findViewById(R.id.et_title);
        final EditText inputDescription = dialog.findViewById(R.id.et_description);
        //fill inputs
        inputTitle.setText(folder.getTitle());
        inputDescription.setText(folder.getDescription());

        Button btnSubmit = dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(folder.getId() > 0){
                    folder.setTitle(inputTitle.getText().toString().trim());
                    folder.setDescription(inputDescription.getText().toString().trim());
                    dataManager.saveFolder(folder.getId(), folder.getContentValues());
                    notifyDataSetChanged();
                }else {
                    folder.setTitle(inputTitle.getText().toString().trim());
                    folder.setDescription(inputDescription.getText().toString().trim());
                    folder.setIsFinal(0);
                    //folder.setParentId(null);
                    Long i = dataManager.saveFolder(-1L, folder.getContentValues());
                    folder.setId(i);
                    folders.add(folder);
                    // folders = mdicDbHelper.getAll();
                    notifyDataSetChanged();
                }
                wrapper_add_btns.setVisibility(View.GONE);
                wrapper_folder_list.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });



        dialog.show();
    }

    private Point getScreenSize(Activity activity){
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        return point;
    }

    private void changeDialogSize(Dialog dialog){
        Point point = getScreenSize(activity);
        if(dialog.getWindow() != null){
            dialog.getWindow().setLayout((int)(0.9*point.x), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

}
