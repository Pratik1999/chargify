package com.example.pratik.chargify;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pratik.chargify.classes.FetchDirections;
import com.example.pratik.chargify.classes.LocationTracker;
import com.example.pratik.chargify.models.ChargingSpots;
import com.example.pratik.chargify.models.ParkingSpot;
import com.example.pratik.chargify.models.SpotLocation;
import com.example.pratik.chargify.views.CustomePopUpDialog;
import com.example.pratik.chargify.views.SearchFilterDialog;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {


    Context context;


    private GoogleMap mMap;
    Marker userMarker;
    LocationManager locationManager;
    LocationListener locationListener;




    //permissions variables
    /*  final int coarseLocationCode=1;
        final int fineLocationCode=2;
   */
    // lists for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    // integer for permissions results request
    private static final int ALL_PERMISSIONS_RESULT = 1011;



    //user Location
    LocationTracker locationTracker;
    LatLng userLocation;

    //firebase
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dataBaseReference;

    private DatabaseReference geoFireRef;
    private DatabaseReference dataFireRef;
    private ChildEventListener childEventListener;
    GeoFire geoFire;
    GeoQuery geoQuery;

    //parking spots
    Map<String, ParkingSpot> parkingSpotsMap;
    ArrayList<ParkingSpot> parkingSpots;

    //filter variables;
    boolean rapidFilter,fastFilter,slowFilter;
    double filterRadius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        LocationManager locManager=(LocationManager) context.getSystemService(LOCATION_SERVICE);
        if(!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Toast.makeText(context,"Please Enable Gps",Toast.LENGTH_LONG).show();
            finish();
        }else {


            //permissions
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(!checkFineLocationPermissions())
            {
                askFineLocationPermissions();
            }
            if(!checkCoarseLocationPermissions())
            {
                askCoarseLocationPermissions();
            }

        }
        */
            permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);

            permissionsToRequest = permissionsToRequest(permissions);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(
                            new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
                }
            }


            parkingSpots = new ArrayList<ParkingSpot>();
            parkingSpotsMap = new HashMap<String, ParkingSpot>();


            //firebase initialisations
            firebaseDatabase = FirebaseDatabase.getInstance();
            dataBaseReference = firebaseDatabase.getReference();
            geoFireRef=firebaseDatabase.getReference("GeoFire");
            dataFireRef=firebaseDatabase.getReference("data");
            geoFire=new GeoFire(geoFireRef);




            /*ParkingSpot ps=new ParkingSpot(24.18720467677,78.9325403577,5.0,"2222222222","rapid","482011",9,2);

            String itemId=dataFireRef.push().getKey();

            geoFire.setLocation(itemId, new GeoLocation(ps.getLatitude(), ps.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                }
            });

            dataFireRef.child(itemId).setValue(ps.getCs());
            */

            geoQuery();
            //attachDatabaseReadListener();

            //filter Initialisation
            rapidFilter = fastFilter = slowFilter = true;
            filterRadius=500.0;


            // navigation drawer
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showSearchFilterDialog();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);


            //maps codes
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.parkingMap);
            mapFragment.getMapAsync(this);


            //user loaction code
            if(locationTracker==null)
                initializeLocationTracker();

            if (locationTracker.canGetLocation()) {
                userLocation = new LatLng(locationTracker.getLatitude(), locationTracker.getLongitude());
            } else {
                Toast.makeText(getApplicationContext(), "Enable GPS FIRST", Toast.LENGTH_LONG);
                //locationTracker.showSettingsAlert();
            }


        }

    }
    public void geoQuery()
    {
        geoQuery = geoFire.queryAtLocation(new GeoLocation(23.18720467677,79.9325403577), filterRadius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(final String key, final GeoLocation location) {
                Log.i("Location", "Added"+key);
                dataFireRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ChargingSpots newCSpot=dataSnapshot.getValue(ChargingSpots.class);
                        ParkingSpot newPSpot= new ParkingSpot(newCSpot,new SpotLocation(location.latitude,location.longitude));
                        if(parkingSpotsMap.containsKey(key))
                            parkingSpotsMap.remove(key);
                        parkingSpotsMap.put(key,newPSpot);
                        Log.i("ChamgedData",newCSpot.getPhone());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Log.i("Location", "Exited"+key);
                parkingSpotsMap.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                Log.i("Location", "All initial data has been loaded and events have been fired!");
                updateMap();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);

                Log.i("Location", "There was an error with this query: " + error);

            }
        });
    }


    //notification drawer codes
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_find_spot) {
            // Handle the camera action
        } else if (id == R.id.nav_add_spot) {
            Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_manage) {
            Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();


        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();


        } else if (id == R.id.nav_send) {
            Toast.makeText(getApplicationContext(),"Under Construction",Toast.LENGTH_LONG).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //notification drawer codes ends


    //maps codes
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markUserLocationMarker();
       /* userMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title("You Are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_face_black_18dp)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));
*/

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng destination=marker.getPosition();
                String title=marker.getTitle();//title is phone no

                Log.i("markerValue",title);
                if(!title.equals("You Are here")) {
                    ParkingSpot spot = parkingSpotsMap.get(title);


                    CustomePopUpDialog dialog = new CustomePopUpDialog(MainActivity.this, spot) {
                        @Override
                        public void getDirectionButtonClicked(LatLng destination) {
                            displayDirections(userLocation, destination);
                        }

                    };
                    dialog.show();

                }
                else {
                    Toast.makeText(context,"Your Location",Toast.LENGTH_LONG).show();
                }



                Log.i("HashMap",parkingSpotsMap.keySet().toString());
                return true;
            }
        });
    }

    public void displayDirections(LatLng userLocation,LatLng destination)
    {
        FetchDirections fetchDirections =new FetchDirections()
        {
            @Override
            public void displayDirections(PolylineOptions lineOptions)
            {
                updateMap();
                mMap.addPolyline(lineOptions);
            }
        };
        String url=fetchDirections.getDirectionsUrl(userLocation,destination);
        fetchDirections.runDownloadTask(url);
    }

    public void updateMap()
    {
        mMap.clear();
        markUserLocationMarker();

        /*LatLng sydney = new LatLng(23.21720467677, 80.0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        LatLng sydney2 = new LatLng(23.18721467678, 79.9325413578);
        mMap.addMarker(new MarkerOptions().position(sydney2).title("Marker in Sydney"));
*/
        Iterator hmIterator = parkingSpotsMap.entrySet().iterator();
        Log.i("updateMap","called");
        int i=0;
        while (hmIterator.hasNext()) {
            i++;
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            ParkingSpot spot= (ParkingSpot) mapElement.getValue();
            Log.i("updateMap","list"+String.valueOf(i)+spot.getPhone());
            //mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongitude())).title(spot.getPhone()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("SLOW"));

            if((spot.getType().equals("slow")&&slowFilter)||(spot.getType().equals("fast")&&fastFilter)||(spot.getType().equals("rapid")&&rapidFilter))
            {
                if(spot.getType().equals("slow"))
                        mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongitude())).title(mapElement.getKey().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).snippet("SLOW"));
                else if(spot.getType().equals("fast"))
                        mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongitude())).title(mapElement.getKey().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                else
                        mMap.addMarker(new MarkerOptions().position(new LatLng(spot.getLatitude(), spot.getLongitude())).title(mapElement.getKey().toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

            }
        }


    }





      private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }


    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MainActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    //permission granted Already

                    //user loaction code

                    if(locationTracker==null)
                        initializeLocationTracker();
                    if(locationTracker.canGetLocation())
                    {
                        userLocation=new LatLng(locationTracker.getLatitude(),locationTracker.getLongitude());
                    }
                    else
                    {
                        locationTracker.showSettingsAlert();
                    }

                    Toast.makeText(getApplicationContext(),"Getting Accurate GPS location Wait..",Toast.LENGTH_LONG).show();
                    markUserLocationMarker();
                }

                break;
        }
    }



    //firebase functions

    private void attachDatabaseReadListener() {

        if(childEventListener==null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("Child", "Added");
                    ParkingSpot parkingSpot=dataSnapshot.getValue(ParkingSpot.class);
                    parkingSpots.add(parkingSpot);
                    parkingSpotsMap.put(parkingSpot.getPhone(),parkingSpot);
                    updateMap();

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.i("Child", "changed"+dataSnapshot.toString());
                    ParkingSpot parkingSpot=dataSnapshot.getValue(ParkingSpot.class);
                    parkingSpotsMap.remove(parkingSpot.getPhone());
                    parkingSpotsMap.put(parkingSpot.getPhone(),parkingSpot);
                    updateMap();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.i("Child", "removed"+dataSnapshot.toString());
                    ParkingSpot parkingSpot=dataSnapshot.getValue(ParkingSpot.class);
                    parkingSpotsMap.remove(parkingSpot.getPhone());
                    updateMap();

                }


                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            dataBaseReference.orderByChild("pinCode").equalTo("482011").addChildEventListener(childEventListener);
        }

    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            dataBaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    public void markUserLocationMarker()
    {
        if(userMarker!=null)
            userMarker.remove();
        userMarker=mMap.addMarker(new MarkerOptions().position(userLocation).title("You Are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_circle_black_18dp)));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12.0f));

    }


    //searchFilter
    void showSearchFilterDialog()
    {
        SearchFilterDialog dialog=new SearchFilterDialog(MainActivity.this,slowFilter,fastFilter,rapidFilter,filterRadius)
        {
            @Override
            public void applyFilter(boolean slow,boolean fast, boolean rapid,double radius)
            {
                slowFilter=slow;
                fastFilter=fast;
                rapidFilter=rapid;
                filterRadius=radius;
                geoQuery.setRadius(filterRadius);
                updateMap();
            }

        };
        dialog.show();
    }

    public void initializeLocationTracker()
    {
        locationTracker = new LocationTracker(getApplicationContext()) {
            @Override
            public void updateLocation(Location location) {
                Log.i("LocationUpdated", String.valueOf(location.getLatitude()));
                userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                markUserLocationMarker();
            }
        };
    }

}
