package com.example.memories.NewMemory;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.memories.Main.MainActivity;
import com.example.memories.R;
import com.example.memories.dataBase.AppDataBase;
import com.example.memories.dataBase.Memory;
import com.example.memories.locationAndPermessions.MyLocationGetter;
import com.example.memories.locationAndPermessions.Permissions;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class newMemory extends Permissions implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    protected EditText title;
    protected EditText description;
    protected TextView currentTime;
    protected ImageView saveMemory;
    protected TextView dateSelected;
    protected ImageView selectedPhoto;
    protected ImageView pickPhotoFromGallery;
    protected CheckBox checkBox;

    AppDataBase myDatabase;
    String PhotosPermession = Manifest.permission.READ_EXTERNAL_STORAGE;
    String locationPermession = Manifest.permission.ACCESS_FINE_LOCATION;

    String message = "if you want to get photo from gallery you should allow permission";
    public final int PERMISSION_FOR_ACCESS_PHOTOS = 2;
    public final int Gallery_Request = 3;
    public final int LOCATION_REQUEST_CODE = 4;
   MyLocationGetter locationGetter;
   Location userLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_new_memory);
        initView();
        initialaizeDataBase();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.save_memory) {
            checkBoxSelected();
            saveMyMemory();

        } else if (view.getId() == R.id.pickPhoto) {
              accessPhotosOnPhone();

        } else if (view.getId() == R.id.checkBox) {

            Toast.makeText(this,"box selected",Toast.LENGTH_SHORT).show();

        }
    }

    public void checkBoxSelected(){
        CheckBox box = findViewById(R.id.checkBox);
        boolean checked = box.isChecked();

        if(checked==true){
           if(isPermissionGranted(this,locationPermession)){
               Toast.makeText(this,"permession granted can now detect location",Toast.LENGTH_SHORT).show();
               detectLocation();

           }else{
              String message = "if you want to add this memory with your location you nedd to allow permission";
               needRequestPermession(locationPermession,message,newMemory.this,LOCATION_REQUEST_CODE );

           }

        }


    }


    private void detectLocation() {

        if( locationGetter == null ){
            locationGetter = new MyLocationGetter(this);

        }

        userLocation = locationGetter.getUserLocation(null);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(userLocation!= null) {
            MainActivity.haveIntentToRecieve=true;
            Intent intent = new Intent(this, MainActivity.class);
            double lat = userLocation.getLatitude();
            double lon = userLocation.getLongitude();
             intent.putExtra("latittude", lat);
            intent.putExtra("longtude", lon);
            startActivity(intent);
           /* Intent intent = new Intent(this, MainActivity.class);
            Location n = userLocation ;
            intent.putExtra("LOCATION", userLocation);
            startActivity(intent);*/
        }
    }

    public void saveMyMemory(){

          if (checkValidate() != true) {

              Toast.makeText(this, " some fields empty",Toast.LENGTH_SHORT).show();
          } else {
              getCurrentTime();
              getCurrentDate();
              Bitmap bitmap = null ;
              Memory memory = null;
            if( selectedPhoto.getDrawable()==null)
              {
                  memory = new Memory(
                          title.getText().toString(),
                          description.getText().toString(),
                          dateSelected.getText().toString(),
                          currentTime.getText().toString(), userLocation);

              myDatabase.memoryDao().addNewMemory(memory);
              Toast.makeText(newMemory.this, "added successfully", Toast.LENGTH_SHORT).show();
              onBackPressed();

              }
              if(selectedPhoto.getDrawable()!=null){
               bitmap = ((BitmapDrawable) selectedPhoto.getDrawable()).getBitmap();
               Location l = userLocation;
              memory = new Memory(
                      title.getText().toString(),
                      description.getText().toString(),
                      dateSelected.getText().toString(),
                      currentTime.getText().toString(), bitmap , userLocation);}

              myDatabase.memoryDao().addNewMemory(memory);
              Toast.makeText(newMemory.this, "added successfully", Toast.LENGTH_SHORT).show();
              onBackPressed();
          }


      }

      public void accessPhotosOnPhone() {

        if (isPermissionGranted(this,PhotosPermession) == true) {
            Toast.makeText(this, "permission taken !", Toast.LENGTH_SHORT).show();
            pickPhotoFromPhone();

        } else {
            needRequestPermession(PhotosPermession, message, newMemory.this, PERMISSION_FOR_ACCESS_PHOTOS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FOR_ACCESS_PHOTOS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickPhotoFromPhone();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    custmToast(this, "can't access your gallery ! ");
                }
                return;
            }
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                   detectLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
             Toast.makeText(this,"can't access your location! ",Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }

    }

    private void pickPhotoFromPhone() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Gallery_Request);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedPhoto.setImageBitmap(selectedImage);
                //uri  " content://com.android.providers.media.documents/document/image%3A1202"
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(newMemory.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(newMemory.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }


    boolean checkValidate() {
        boolean validate = true;
        if (title.getText().toString().trim().isEmpty() ||
                description.getText().toString().trim().isEmpty()) {
            validate = false;
        }

        return validate;

    }


    public AppDataBase initialaizeDataBase() {
        /*if (myDatabase == null) {
            myDatabase = Room.databaseBuilder(newMemory.this, AppDataBase.class, MainActivity.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }*/

        myDatabase = AppDataBase.getInstance(this);
        return myDatabase;
    }

    private void initView() {
        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        currentTime = (TextView) findViewById(R.id.current_time);
        saveMemory = (ImageView) findViewById(R.id.save_memory);
        saveMemory.setOnClickListener(newMemory.this);
        dateSelected = (TextView) findViewById(R.id.date_selected);
        selectedPhoto = (ImageView) findViewById(R.id.photo);
        pickPhotoFromGallery = (ImageView) findViewById(R.id.pickPhoto);
        pickPhotoFromGallery.setOnClickListener(newMemory.this);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
    }

    public void getCurrentTime() {
        SimpleDateFormat SdateFormat = new SimpleDateFormat("h:mm a");
        Calendar cal = Calendar.getInstance();
        String time = SdateFormat.format(cal.getTime());
        currentTime.setText(SdateFormat.format(cal.getTime()));
    }

    public void getCurrentDate() {
        SimpleDateFormat SdateFormat = new SimpleDateFormat("EEE, MMM d, ''yyyy");
        Calendar cal = Calendar.getInstance();
        String time = SdateFormat.format(cal.getTime());
        dateSelected.setText(SdateFormat.format(cal.getTime()));
    }

    public void showDatePickerDialogs() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + month + "/" + year;
        dateSelected.setText(date);

    }


}

