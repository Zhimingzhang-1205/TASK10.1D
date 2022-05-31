package zhimingzhang.deakin.trucksharing;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import zhimingzhang.deakin.trucksharing.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.googlecertificates;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    DirectionsResult result;
    double pickupLatitude,pickupLongitude,dropoffLatitude,dropoffLongitude,fare;
    String pickupLocation,dropoffLocation,distance,travelTime;
    TextView fareTextView,travelTimeTextView;
    private static final int REQUEST_PHONE_CALL = 1;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId("AVxkbEbFPQD7u8Sh-PwmfCSz9rSIkGcTWvqxpkn08IYmYOl-4rAyig18rk2ztjlNV49u78nOqexJrao1");
    Button bookButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView pickUpLocationTextView =findViewById(R.id.pickUpLocationTextView);
        TextView dropOffLocationTextView =  findViewById(R.id.dropOffLocation);
        fareTextView = findViewById(R.id.fareTextView);
        travelTimeTextView = findViewById(R.id.travelTimeTextView);
        bookButton = findViewById(R.id.bookButton);
        Button callButton = findViewById(R.id.callButton);

        Intent intent = getIntent();
        pickupLocation = intent.getStringExtra("pickupLocation");
        pickupLatitude = intent.getDoubleExtra("pickupLatitude",106.53742738357);
        pickupLongitude = intent.getDoubleExtra("pickupLongitude",29.37650541368);
        dropoffLocation = intent.getStringExtra("dropoffLocation");
        dropoffLatitude = intent.getDoubleExtra("dropoffLatitude",106.4970601379);
        dropoffLongitude = intent.getDoubleExtra("dropoffLongitude",29.412858225117);
        pickUpLocationTextView.setText("Pickup Location: "+pickupLocation);
        dropOffLocationTextView.setText("Drop-off Location: "+dropoffLocation);

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayPalPayment payment = new PayPalPayment(new BigDecimal(fare), "AUD", "Course Fees",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent3 = new Intent(getApplicationContext(), PaymentActivity.class);
                intent3.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent3.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivity(intent3);
            }
        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(Intent.ACTION_CALL);
                intent1.setData(Uri.parse("tel:8618090445379"));

                if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(intent1);
                }
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng pickUpLng = new LatLng(pickupLatitude, pickupLongitude);
        LatLng dropOffLng = new LatLng(dropoffLatitude, dropoffLongitude);



        GeoApiContext geoApiContext = new GeoApiContext.Builder()
                .apiKey("AIzaSyACGGzwLjgbMviNDvd6huH_z6-DvhcZ_7E")
                .build();

        try {
            result = DirectionsApi.newRequest(geoApiContext).mode(TravelMode.DRIVING).origin(new com.google.maps.model.LatLng(pickUpLng.latitude, pickUpLng.longitude))
                    .destination(new com.google.maps.model.LatLng(dropOffLng.latitude, dropOffLng.longitude))
                    .transitMode()
                    .await();
            List<LatLng> decodedPath = PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());
            mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        distance =  result.routes[0].legs[0].distance.toString();
        travelTime =  result.routes[0].legs[0].duration.toString();
        String distanceText = distance.substring(0,distance.length()-3).trim();
        double distanceNum = Double.parseDouble(distanceText);
        fare = distanceNum*5;
        String fareText = String.valueOf(fare);
        fareTextView.setText("Approx.Fare: $" +fareText);
        travelTimeTextView.setText("Approx.Travel time: " +travelTime);


        mMap.addMarker(new MarkerOptions().position(pickUpLng));
        mMap.addMarker(new MarkerOptions().position(dropOffLng));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pickUpLng,13));
    }


}