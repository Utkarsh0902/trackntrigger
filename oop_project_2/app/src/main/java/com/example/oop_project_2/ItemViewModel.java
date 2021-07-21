package com.example.oop_project_2;

import android.graphics.Bitmap;

public class ItemViewModel{
    public String category;
    public String title;
    public int quantity;
    public String notes;
    public Bitmap img;
    public ItemViewModel(){
        this.title = "";
        this.quantity = 0;
        this.img = null;
        this.category = "";

    }

    public String getTitle() {
        return title;
    }
    public String getCategory(){return category;}
    public int getQuantity() {
        return quantity;
    }
    public String getNotes() {
        return notes;
    }
    public Bitmap getImg(){ return img; }

    public void increaseQuantity() {
        this.quantity++;
    }
    public void decreaseQuantity() {
        if(this.quantity>0) {
            this.quantity--;
        }
    }

    public void setTitle(String S) {
        this.title = S;
    }
    public void setNotes(String S) {
        this.notes = S;
    }
    public void setImg(Bitmap bm){
        this.img = bm;
    }
    public void setCategory(String S){this.category = S;}
}
