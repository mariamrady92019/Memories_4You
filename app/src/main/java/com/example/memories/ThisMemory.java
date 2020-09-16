package com.example.memories;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memories.Base.BaseActivity;
import com.example.memories.Main.MainActivity;
import com.example.memories.dataBase.Memory;

public class ThisMemory extends BaseActivity implements View.OnClickListener {


    protected ImageView back;
    protected TextView title;
    protected TextView description;
    protected TextView memoryDate;
    protected TextView memoryTime;
    protected ImageView photo;
    String titleS;
    String descriptionS;
    String dateS;
    String timeS;
   int position;
   public static boolean recieveFromItemClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_this_memory);


        initView();

           recieveFromItemClick();



           recieveForNearMemory();




    }



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
              onBackPressed();
        }
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(ThisMemory.this);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        memoryDate = (TextView) findViewById(R.id.memory_date);
        memoryTime = (TextView) findViewById(R.id.memory_time);
        photo = (ImageView) findViewById(R.id.photo);
    }


    public void recieveFromItemClick() {

          //recieve intent
        if(recieveFromItemClick==false){return;}
        else{
        Memory memory = (Memory) getIntent().getSerializableExtra("this memory");
        position = getIntent().getIntExtra("position", 0);
        titleS = memory.getTitlee();
        descriptionS = memory.getDescription();
        timeS = memory.getTime();
        dateS = memory.getDate();
        //set data
        title.setText(titleS);
        description.setText(descriptionS);
        memoryDate.setText(dateS);
        memoryTime.setText(timeS);
        if(memory.getStringOfImage()==null){
            recieveFromItemClick=false;
            return;
        }
        String forImage = memory.getStringOfImage();
       Bitmap bit = StringToBitMap(forImage);
        photo.setImageBitmap(bit);
        recieveFromItemClick=false;
        }

    }
    public void recieveForNearMemory(){
        if(MainActivity.haveNearMemory==true) {
            Memory nearMemory = (Memory) getIntent().getSerializableExtra("targeted memory");
            titleS = nearMemory.getTitlee();
            descriptionS = nearMemory.getDescription();
            timeS = nearMemory.getTime();
            dateS = nearMemory.getDate();
            //set data
            title.setText(titleS);
            description.setText(descriptionS);
            memoryDate.setText(dateS);
            memoryTime.setText(timeS);
            if(nearMemory.getStringOfImage()==null)
            {
                 MainActivity.haveNearMemory=false;
                    return;
            }
            String forImage = nearMemory.getStringOfImage();
            Bitmap bit = StringToBitMap(forImage);
            photo.setImageBitmap(bit);
            MainActivity.haveNearMemory=false;
        }else
            return;
    }

}
