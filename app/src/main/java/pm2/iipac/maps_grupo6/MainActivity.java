package pm2.iipac.maps_grupo6;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapsSdkInitializedCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback
        ,LocationSource, OnMapsSdkInitializedCallback {

    private static final CATALOGO_PEMISOS catalogo = CATALOGO_PEMISOS.INSTANCE;
    private GoogleMap mMap;
    private TextView latitud, longitud;
    private OnLocationChangedListener mListener;
    private boolean mPaused;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Renderizado del Mapa
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this);

        latitud = findViewById(R.id.tvLatitud);
        longitud = findViewById(R.id.tvLongitud);
        Button btnFlush = findViewById(R.id.btnFlush);
        MapView mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.onStart();
        btnFlush.setOnClickListener(v -> flush());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getCameraPosition();
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mMap.setOnMyLocationClickListener(this);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        latitud.setText("Latitud -> " +mMap.getMyLocation().getLatitude());
        longitud.setText("Longitud -> "+mMap.getMyLocation().getLongitude());
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        latitud.setText("Latitud -> " +location.getLatitude());
        longitud.setText("Longitud -> "+location.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))  {
            mMap.setMyLocationEnabled(true);
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if ( (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))  {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, catalogo.LOCATION_PERMISSION_REQUEST_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, catalogo.LOCATION_PERMISSION_REQUEST_CODE_COARSE);
        }
    }

    private void flush(){
        mMap.clear();
        latitud.setText(R.string.label_latitud);
        longitud.setText(R.string.label_longitud);
    }

    @Override
    public void activate(@NonNull OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    public void onNewLocationReceived(Location location) {
        if (mListener != null && !mPaused) {
            mListener.onLocationChanged(location);
        }
    }

    @Override
    public void onMapsSdkInitialized(@NonNull MapsInitializer.Renderer renderer) {

    }
}