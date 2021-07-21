package com.example.oop_project_2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder> {

    private final LinkedList<String> categoryList;
    private LayoutInflater cInflater;

    DocumentReference documentReference;
    String CategoryType;

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        public final EditText categoryTitleView;
        public final CardView categoryCard;
        public final ImageButton deleteCategoryButton;
        final CategoryListAdapter cAdapter;

        public CategoryViewHolder(View itemView, CategoryListAdapter adapter) {
            super(itemView);

            Context mContext = itemView.getContext();

            categoryTitleView = itemView.findViewById(R.id.category_edittext);
            categoryCard = itemView.findViewById(R.id.category_card);
            deleteCategoryButton = itemView.findViewById(R.id.delete_category_button);
            this.cAdapter = adapter;


            //Onclick behaviour for the Edit Text field
            categoryTitleView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int mPosition = getLayoutPosition();
                    String oldCategory = categoryList.get(mPosition);

                    categoryList.set(mPosition,categoryTitleView.getText().toString()) ;
                    CategoryType = categoryTitleView.getText().toString();

                    //If the name of a category is changed, change the category field in all the items of that category
                    for (ItemViewModel item : TrackerActivity.ItemList) {
                        if (item.getCategory().toLowerCase().equals(oldCategory.toLowerCase())) {
                            item.setCategory(categoryList.get(mPosition)); //set it to the new category name
                        }
                    }

                }
            });

            //onclick behaviour for the card: goto tracker activity
            categoryCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition = getLayoutPosition();
                    Intent intent = new Intent(mContext, TrackerActivity.class);
                    intent.putExtra("itemCategory", categoryList.get(mPosition));
                    mContext.startActivity(intent);

                }
            });

            //Creating the alert dialog box to delete a category from the list
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setMessage("Confirm delete of category and all its contents?");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            int mPosition = getLayoutPosition();
                            String category = categoryList.get(mPosition);
                            LinkedList<ItemViewModel> toRemove = new LinkedList<ItemViewModel>();
                            for (ItemViewModel item : TrackerActivity.ItemList) {
                                if (item.getCategory().toLowerCase().equals(category.toLowerCase())) {
                                    toRemove.add(item);
                                }
                            }
                            TrackerActivity.ItemList.removeAll(toRemove);
                            categoryList.remove(mPosition);
                            cAdapter.notifyDataSetChanged();

                        }

                    });
            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                }
            });
            //onclick behaviour for deleting the category and its contents
            deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        }
    }

    public CategoryListAdapter(Context context,
                               LinkedList<String> catList) {
        cInflater = LayoutInflater.from(context);
        this.categoryList = catList;
    }


    @NonNull
    @Override
    public CategoryListAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cItemView = cInflater.inflate(R.layout.category_card_design,
                parent, false);
        return new CategoryViewHolder(cItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListAdapter.CategoryViewHolder holder, int position) {
        String cCurrent = categoryList.get(position);
        holder.categoryTitleView.setText(cCurrent);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
