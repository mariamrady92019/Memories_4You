package com.example.memories.AllMemories;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.memories.R;
import com.example.memories.dataBase.Memory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class adapter extends RecyclerView.Adapter<adapter.ViewHolder>{

List<Memory> allMemories = new ArrayList<>();
View view ;
//RequestManager glide;
//private  Context context = null;
private onItemClick onItemClick ;

 public adapter(List<Memory> memories  ) {
//RequestManager glide
    this.allMemories= memories;
    //this.glide= glide;
     //this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.onememory,parent,false);
        return new ViewHolder(view);
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

       final Memory memory = allMemories.get(position);
       String s = memory.getStringOfImage();
           if (s==null){
              holder.photo.setImageResource(R.drawable.ic_defualtphoto);
           }else{
               Bitmap converted = StringToBitMap(s);
               holder.photo.setImageBitmap(converted);
           }

       holder.title.setText(memory.getTitlee());
       holder.date.setText(memory.getDate());
       holder.time.setText(memory.getTime());
       holder.description.setText(memory.getDescription());
       if (onItemClick!=null){
           holder.itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   onItemClick.Click(position , memory);


               }
           });

       }


       //Glide.with(context).load(uri).into(holder.photo);
       //glide.load(uri).into(holder.photo);
       // Glide.with(view.getContext()).load(uri).placeholder(R.drawable.back).dontAnimate().into(holder.photo);
         /*byte[] decodedString = Base64.decode(picUri, Base64.NO_WRAP);
        InputStream input=new ByteArrayInputStream(decodedString);
        Bitmap ext_pic = BitmapFactory.decodeStream(input);
        holder.photo.setImageBitmap(ext_pic);*/
    }

    @Override
    public int getItemCount() {

    return allMemories.size();
    }

 public class ViewHolder extends  RecyclerView.ViewHolder{
         TextView title ;
         TextView date ;
         TextView time ;
          TextView description ;
          ImageView photo ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.oneimage);
            title  = itemView.findViewById(R.id.onetitle);
           date =itemView.findViewById(R.id.onedate);
            time =itemView.findViewById(R.id.onetime);
            description = itemView.findViewById(R.id.oneDescription);
           }
    }

    public void setNewList (List<Memory> newMemories){
        this.allMemories= newMemories;
        notifyDataSetChanged();
    }
    public  void  restoreItem(Memory memory, int position){

        allMemories.add(position,memory);
        notifyItemInserted(position);
    }


    public interface onItemClick{

        void  Click(int position , Memory memory);

    }

    public void setOnItemClick(adapter.onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }
}
