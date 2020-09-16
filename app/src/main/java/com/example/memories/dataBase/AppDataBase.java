package com.example.memories.dataBase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.memories.Main.MainActivity;
import com.example.memories.NewMemory.newMemory;

@Database( entities = {Memory.class} , version = 8,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    private  static AppDataBase mydataBase ;
   public abstract memoryDao memoryDao();


   public static AppDataBase getInstance(Context context){
       if(mydataBase==null){
           mydataBase = Room.databaseBuilder(context, AppDataBase.class, MainActivity.DB_NAME)
                   .fallbackToDestructiveMigration()
                   .allowMainThreadQueries().build();
       }
       return mydataBase;

   }




}
