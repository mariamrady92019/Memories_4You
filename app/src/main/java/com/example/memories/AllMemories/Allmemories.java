package com.example.memories.AllMemories;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.RequestManager;
import com.example.memories.Helper.RecyclerItemTouchHelper;
import com.example.memories.Helper.RecyclerItemTouchHelperListener;
import com.example.memories.R;
import com.example.memories.ThisMemory;
import com.example.memories.dataBase.AppDataBase;
import com.example.memories.dataBase.Memory;
import com.example.memories.Main.MainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.Serializable;
import java.util.List;

public class Allmemories extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    protected RecyclerView allMemoriesRecycler;
   adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    AppDataBase myDatabase;
    RequestManager glide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_allmemories);
        initialaizeDataBase();
        buildRecyclerView();
        onMemoryClick();
        makeTouchHelper();


    }

    public void buildRecyclerView(){
        List<Memory> allMemories = myDatabase.memoryDao().getAllNotes();
        allMemoriesRecycler = (RecyclerView) findViewById(R.id.allMemories_recycler);
        adapter = new adapter(allMemories );
        layoutManager = new LinearLayoutManager(Allmemories.this);
        allMemoriesRecycler.setAdapter(adapter);
        allMemoriesRecycler.setLayoutManager(layoutManager);
   }
    void makeTouchHelper() {

        ItemTouchHelper.SimpleCallback item = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(item).attachToRecyclerView(allMemoriesRecycler);
    }
    public AppDataBase initialaizeDataBase() {
        if(myDatabase==null)
        { myDatabase = Room.databaseBuilder(Allmemories.this,AppDataBase.class,MainActivity.DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries().build();
        }

        return myDatabase ;
    }


    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        List<Memory> allmemories = myDatabase.memoryDao().getAllNotes();
        final Memory deletedMemory = allmemories.get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();
        final int id = deletedMemory.getId();
        //adapter.removeItem(deletedIndex);
        myDatabase.memoryDao().deleteMemory(deletedMemory);
        allmemories.remove(deletedIndex);
        adapter.setNewList(myDatabase.memoryDao().getAllNotes());
        adapter.notifyDataSetChanged();

        Snackbar snack=Snackbar.make(viewHolder.itemView," your note deleted",Snackbar.LENGTH_LONG).setAction("Undo ?", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // deletedNote.setId(id);
               adapter.restoreItem(deletedMemory,deletedIndex);
                // adapter.notifyDataSetChanged();
                myDatabase.memoryDao().addNewMemory(deletedMemory);

                 /*   List<Note> newNote = myDatabase.noteDao().getAllNotes();
                    adapter.setNewList(newNote);
                 */
                   /* List<Note> newNote = myDatabase.noteDao().getAllNotes();
                    adapter.setNewList(newNote);
                    adapter.notifyDataSetChanged();
*/
            }
        });
        snack.setActionTextColor(getResources().getColor(R.color.white));
        View snackview = snack.getView();
        snackview.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        snack.show();
    }

    void onMemoryClick() {

        adapter.setOnItemClick(new adapter.onItemClick() {
            @Override
            public void Click(int position, Memory memory) {
                //هيروح لل الميمري

               Intent intent = new Intent(Allmemories.this,ThisMemory.class);
                intent.putExtra("this memory",  memory);
                intent.putExtra("position", position);
                startActivity(intent);
                ThisMemory.recieveFromItemClick= true;
                finish();
            }
        });


    }



}

