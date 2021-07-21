package com.example.oop_project_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder> {

    class ItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView itemImageView;
        public EditText itemTitleView;
        public EditText itemNotesView;
        public ImageButton decreaseItemButton;
        public ImageButton increaseItemButton;
        public TextView itemQuantityView;
        public ImageButton deleteItemButton;
        public ImageButton shareItemButton;

        final ItemListAdapter mAdapter;



        public ItemViewHolder(@NonNull View itemView, ItemListAdapter adapter) {
            super(itemView);
            Context mContext = itemView.getContext();
            itemImageView = itemView.findViewById(R.id.item_image);
            itemTitleView = itemView.findViewById(R.id.item_title);
            itemNotesView = itemView.findViewById(R.id.item_notes);
            decreaseItemButton = itemView.findViewById(R.id.decrease_item_quantity);
            increaseItemButton = itemView.findViewById(R.id.increase_item_quantity);
            itemQuantityView = itemView.findViewById(R.id.item_quantity);
            deleteItemButton = itemView.findViewById(R.id.delete_item_button);
            shareItemButton = itemView.findViewById(R.id.share_item_button);


            //Creating the alert dialog box to delete an item from the list
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setMessage("Confirm delete?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            int mPosition = getLayoutPosition();
                            mItemList.remove(mPosition);
                            TrackerActivity.ItemList.remove(mPosition);
                            //update the adapter
                            mAdapter.notifyDataSetChanged();


                        }

                    });
            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                }
            });
            //Delete item onClick Listener
            deleteItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }
            });

            //Increase Quantity onClick Listener
            increaseItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition = getLayoutPosition();
                    mItemList.get(mPosition).increaseQuantity();
                    //update the adapter
                    mAdapter.notifyDataSetChanged();
                   // TrackerActivity.UploadToFirebase();
                }
            });
            shareItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int mPosition = getLayoutPosition();
                    ItemViewModel item = mItemList.get(mPosition);
                    String ItemName = item.getTitle();
                    String ItemQuantity = item.getQuantity() + "";
                    String ItemNotes = item.getNotes();
                    String ItemCategory = item.getCategory();
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, "Category: " + ItemCategory + "\nItem name: " + ItemName + "\nItem Quantity: " + ItemQuantity
                    + "\nAdditional Notes: " + ItemNotes);

                    mContext.startActivity(intent.createChooser(intent, "Choose your App"));
                }
            });

            //Decrease Quantity onClick listener
            decreaseItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition = getLayoutPosition();
                    mItemList.get(mPosition).decreaseQuantity();
                    //update the adapter
                    mAdapter.notifyDataSetChanged();
                   // TrackerActivity.UploadToFirebase();
                }
            });


            //Save the title
            itemTitleView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int mPosition = getLayoutPosition();
                    mItemList.get(mPosition).setTitle(itemTitleView.getText().toString());
                    //TrackerActivity.UploadToFirebase();

                }
            });

            //Save the notes
            itemNotesView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int mPosition = getLayoutPosition();
                    mItemList.get(mPosition).setNotes(itemNotesView.getText().toString());
                   // TrackerActivity.UploadToFirebase();
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            //onclick for imageView : change activity to pick image from gallery
            itemImageView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
                @Override
                public void onClick(View v) {
                    int mPosition = getLayoutPosition();
                    Intent intent = new Intent(mContext, GalleryActivity.class);
                    intent.putExtra("position", mPosition);
                    mContext.startActivity(intent);
                    mAdapter.notifyDataSetChanged();

                }

            });

            this.mAdapter = adapter;
        }

    }


    private LinkedList<ItemViewModel> mItemList;
    private LayoutInflater mInflater;

    public ItemListAdapter(Context context,
                           LinkedList<ItemViewModel> itemList) {
        mInflater = LayoutInflater.from(context);
        this.mItemList = itemList;
    }

    @NonNull
    @Override
    public ItemListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.item_card_design, parent, false);


        return new ItemViewHolder(mItemView, this);
    }


    @Override
    public void onBindViewHolder(@NonNull ItemListAdapter.ItemViewHolder holder, int position) {
        ItemViewModel mCurrent = mItemList.get(position);
        holder.itemTitleView.setText(mCurrent.getTitle());
        holder.itemQuantityView.setText(String.valueOf(mCurrent.getQuantity()));
        holder.itemNotesView.setText(mCurrent.getNotes());
        if(mCurrent.img==null) {
            holder.itemImageView.setImageResource(R.drawable.add_image);
        } else {
            holder.itemImageView.setImageBitmap(mCurrent.getImg());
        }

    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void filteredList(LinkedList<ItemViewModel> filteredList){
        mItemList = filteredList;
        notifyDataSetChanged();


    }
}

