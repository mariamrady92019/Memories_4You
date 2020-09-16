package com.example.memories.Main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.memories.AllMemories.Allmemories;
import com.example.memories.ThisMemory;
import com.example.memories.dataBase.Memory;
import com.example.memories.locationAndPermessions.MyLocationGetter;
import com.example.memories.NewMemory.newMemory;
import com.example.memories.R;
import com.example.memories.dataBase.AppDataBase;
import com.example.memories.locationAndPermessions.Permissions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Permissions implements OnMapReadyCallback, View.OnClickListener , LocationListener {

    protected MapView myMap;
    protected Button addMemoryButton;
    protected Button getAallButton;
    private GoogleMap map;
    private MyLocationGetter locationGetter ;
    private Location myLocation ;
    private Marker userMarker ;
    private static final  int PERMESSION_REQUESTLOCATION_CODE =1;
    public static AppDataBase myDatabase ;
    public static final String DB_NAME ="myDataBase";
     List<Memory> allMemoriesLocations = new ArrayList<>();
    public static boolean haveIntentToRecieve = false ;
    public static boolean changemap = false ;
    Memory targetedMemoryfromMarker = new Memory();
    public static boolean haveNearMemory = false;

    DialogInterface.OnClickListener  goToMemoryOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            //go to this memory

            Intent intent = new Intent(MainActivity.this , ThisMemory.class);
            intent.putExtra("targeted memory", targetedMemoryfromMarker);
            startActivity(intent);
            dialogInterface.dismiss();
            haveNearMemory = true ;
            finish();


            }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        initView();
        initialaizeDataBase();
        myMap.onCreate(savedInstanceState);
        myMap.getMapAsync(this);
        access();


        {
     // checkForRecieveIntent();
        }


    }



    public AppDataBase initialaizeDataBase() {
        if (myDatabase== null) {
           myDatabase = Room.databaseBuilder(MainActivity.this,   AppDataBase.class, MainActivity.DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries().build();
        }

        return myDatabase;

    }

    private void initView() {
        myMap = (MapView) findViewById(R.id.myMap);
        addMemoryButton = (Button) findViewById(R.id.addMemory_button);
        addMemoryButton.setOnClickListener(MainActivity.this);
        getAallButton = (Button) findViewById(R.id.getAall_button);
        getAallButton.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
      markUserLocation(myLocation,R.drawable.ic_locaion ,userMarker,"im here" , this.map);
      checkForRecieveIntent(this.map);
      retrieveMemoriesLocations(this.map);
      findMemoryNear(myLocation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myMap.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myMap.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        myMap.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myMap.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myMap.onDestroy();
    }

 public  void checkForRecieveIntent(GoogleMap map){
        if(haveIntentToRecieve==true){
            haveIntentToRecieve=false ;
        Toast.makeText(this,"دخل ف checkfor recieve",Toast.LENGTH_SHORT).show();

            double lat= getIntent().getDoubleExtra("latittude",0) ;
            double lon= getIntent().getDoubleExtra("longtude",0) ;

            Location memoryLocation = new Location("");
            memoryLocation.setLatitude(lat);
            memoryLocation.setLongitude(lon);
             Marker newMarker = null;
             markUserLocation(memoryLocation,R.drawable.ic_memory , newMarker , "memory here" ,map);


        }

 }



       public  void retrieveMemoriesLocations(GoogleMap map){
        allMemoriesLocations= myDatabase.memoryDao().getAllNotes();
        for (Memory memory: allMemoriesLocations){
            double lat = memory.getLattitude();
            double lon = memory.getLongtude();
            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lon);
            Marker newMarker= null ;
            if(location!=null){
                String title = memory.getTitlee();
                String desc = memory.getDescription();
                String date = memory.getDate();
                String time = memory.getTime();
            markUserLocation(location,R.drawable.ic_memory,newMarker,"your title:" +title+"||"+"your description :"+desc+"||"+date+time+""
                    ,map);
             }
        }
        }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addMemory_button) {
            Intent intent = new Intent(MainActivity.this, newMemory.class);
            startActivity(intent);

          } else if (view.getId() == R.id.getAall_button) {

            Intent intent = new Intent(MainActivity.this ,Allmemories.class);
            startActivity(intent);

        }
    }



          public  void access(){

         if(isPermissionGranted(this , Manifest.permission.ACCESS_FINE_LOCATION)==true)
         {
             Toast.makeText(this,"can access your location",Toast.LENGTH_SHORT).show();
             getUserLocation();
         }
         else{
             needRequestPermession(Manifest.permission.ACCESS_FINE_LOCATION ,
                     "need permission to access your location !",
                     MainActivity.this,PERMESSION_REQUESTLOCATION_CODE);
         }




           /*if(isPermissionGranted()==true ){
               Toast.makeText(this,"can access your location",Toast.LENGTH_SHORT).show();
               getUserLocation();
           } else{
               needRequestPermession();
           }*/
            }


  /** public boolean isPermissionGranted(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public void needRequestPermession(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            showMessage("this app need your location to detect where your memory been", "ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                            requestPermission();
                        }
                    },true);

        } else {
            // No explanation needed; request the permission


            requestPermission();

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    public void requestPermission(){

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMESSION_REQUESTLOCATION_CODE);

    }
**/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMESSION_REQUESTLOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getUserLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    custmToast(this,"can't access your location ! ");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private void getUserLocation()
    {
        if(locationGetter==null){
          locationGetter = new MyLocationGetter(this);

        }
       myLocation = locationGetter.getUserLocation(this);
    }



    private void markUserLocation(Location location , int drawable , Marker marker , String message , GoogleMap map ) {
        if( map == null||location == null )
        {
           Toast.makeText(this,"null location",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(marker== null){
            Toast.makeText(this,"enter if marker",Toast.LENGTH_SHORT).show();

            Log.e("user_location",location.getLatitude()+ "  "+location.getLongitude());
            marker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(),location.getLongitude()))
                    .title(message)
                    .icon(BitmapDescriptorFactory.fromResource(drawable)));
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
        }else{

           marker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
            map.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));

        }

    }


    @Override
    public void onLocationChanged(Location location) {
        this.myLocation = location;
        markUserLocation(myLocation , R.drawable.ic_locaion , userMarker , "im here" , this.map);
        findMemoryNear(myLocation);
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public  void findMemoryNear(Location currentLocation){
         List<Memory> allLocations = myDatabase.memoryDao().getAllNotes();

        double lat1;
        double lon1;
        if(currentLocation==null){
            return;
        }
        double currentlat = currentLocation.getLatitude();
        double currentlon = currentLocation.getLongitude();
        for (Memory memory: allMemoriesLocations) {
            if(!(memory.getLattitude()>0)){
                break ;
            }else{
            lat1 = memory.getLattitude();
            lon1 = memory.getLongtude();
            float[] distance = new float[1];
            Location.distanceBetween(lat1, lon1, currentlat, currentlon, distance);
            // distance[0] is now the distance between these lat/lons in meters
            if (distance[0] < 20.0) {
                // your code...
                targetedMemoryfromMarker= memory;
                String message = "you have amemory here around you now , wish it was happy";
                showMessage(message,"see it ?" ,goToMemoryOnClickListener,true);

            }}
        }



    }

}
















