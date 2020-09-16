package com.example.memories.dataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;


@Dao
public interface memoryDao {

  @Insert
  public void addNewMemory(Memory memory);

@Delete
public void deleteMemory(Memory memory);


@Query(" select * from Memory")
List<Memory> getAllNotes();


}
