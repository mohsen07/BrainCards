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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mdic.CardActivity;
import com.example.mdic.ExamActivity;
import com.example.mdic.R;
import com.example.mdic.data.model.Card;
import com.example.mdic.data.model.Folder;

import java.util.List;

import javax.inject.Inject;

public class FolderAdapter2 extends RecyclerView.Adapter<FolderAdapter2.ViewHolder> {
    private Activity activity;
    private List<Folder> folders;
   // private List<Card> cardLoop;

    private DataManager dataManager;

    LinearLayout wrapper_add_btns;
    LinearLayout wrapper_folder_list;

    public void setFolders(List<Folder> folders) {
        this.folders.clear();
        this.folders.addAll(folders);
    }

    public FolderAdapter2(@NonNull Activity input_activity, @NonNull List<Folder> folders, LinearLayout lnbtn , LinearLayout lnfldr, DataManager dManager) {
        //super(input_activity, R.layout.folder_adapter, folders);
        dataManager = dManager;
        this.folders = folders;
        this.activity = input_activity;
        wrapper_add_btns = lnbtn;
        wrapper_folder_list = lnfldr;

    }

/*
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

            convertView.setTag(viewHolder);

        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.fill(folder);

        return convertView;
    }

*/


    /***********************************************************************************************/


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_adapter, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderAdapter2.ViewHolder holder, int position) {
        holder.bind(folders.get(position));
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon_more;
        public TextView title;
        public RelativeLayout folder_row;

        ViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.tv_title);
            icon_more = itemView.findViewById(R.id.icon_more);
            folder_row = itemView.findViewById(R.id.folder_row);

        }

        void bind(Folder folder){
            /*img_profile.setImageResource(contact.getId());*/
            title.setText(folder.getTitle()+"  ("+folder.getId()+")");
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
                        folders.remove(folder);
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

                    folders.clear();
                    folders.addAll(dataManager.getFolders(" parentId = "+folder.getId(), null));
                    DataManager.folder_parentId = folder.getId();
                    if(folders != null && folders.size() > 0){
                        notifyDataSetChanged();
                    }else {
                        List<Card> cardList =dataManager.getAllCards(folder.getId());
                        if(cardList != null && cardList.size() >0 ){

                            Intent intent = new Intent(activity, CardActivity.class);
                            intent.putExtra("parent_folder_id",folder.getId());
                            activity.startActivity(intent);
                            /**Note: to resolve fatal error when back to folderActivity i recreate this activity and then go to other activity*/
                            activity.recreate();
                        }else {
                            wrapper_add_btns.setVisibility(View.VISIBLE);
                            wrapper_folder_list.setVisibility(View.GONE);
                        }

                    }
                    //Toast.makeText(folder_row.getContext(), "folder.getId():"+folder.getId()+"  DataManager.folder_parentId:"+DataManager.folder_parentId, Toast.LENGTH_LONG).show();
                }
            });
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
                    //edit mode
                    folder.setTitle(inputTitle.getText().toString().trim());
                    folder.setDescription(inputDescription.getText().toString().trim());
                    Long newId = dataManager.saveFolder(folder.getId(), folder.getContentValues());
                    //folders.add(folder);
                    notifyDataSetChanged();
                }else {
                    //save mode
                    folder.setTitle(inputTitle.getText().toString().trim());
                    folder.setDescription(inputDescription.getText().toString().trim());
                    folder.setIsFinal(0);
                    //folder.setParentId(null);
                    Long i = dataManager.saveFolder(-1L, folder.getContentValues());
                    folder.setId(i);

                    //folders.addAll(dataManager.getFolders(" parentId = "+folder.getParentId(), null));
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
