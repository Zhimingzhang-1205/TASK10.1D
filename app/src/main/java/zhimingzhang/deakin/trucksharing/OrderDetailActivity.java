package zhimingzhang.deakin.trucksharing;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import zhimingzhang.deakin.trucksharing.databinding.ActivityOrderDetialBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class OrderDetailActivity extends BaseBindingActivity<ActivityOrderDetialBinding> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss a", Locale.ENGLISH);

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {

        Order detail = (Order) getIntent().getSerializableExtra("order_detail");
        viewBinder.tvSender.setText("Form sender " + App.user.username);
        viewBinder.tvStartTime.setText("Pick up time: " + dateFormat.format(Long.parseLong(detail.timestamp)));
        viewBinder.tvReceiverName.setText("To receiver: " + detail.receiverName);
        viewBinder.tvEndTime.setText("Drop off time: " + dateFormat.format(Long.parseLong(detail.timestamp) + 60 * 1000 * 60 * 2));
        viewBinder.tvWeight.setText("Weight\n" + detail.weight + "kg");
        viewBinder.tvWidth.setText("Width\n" + detail.width + "m");
        viewBinder.tvHeight.setText("Height\n" + detail.height + "m");
        viewBinder.tvLength.setText("Length\n" + detail.length + "m");
        viewBinder.tvQuantity.setText("Quantity\n26");
        viewBinder.tvType.setText("Type\n" + detail.type);
        viewBinder.face.setImageResource(detail.face);
        TextView estimateButton = findViewById(R.id.tv_create_order);
        estimateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),MapsActivity.class);

                intent1.putExtra("pickupLocation","userhome");
                intent1.putExtra("pickupLatitude",29.37650541368938);
                intent1.putExtra("pickupLongitude",106.53742738357543);
                intent1.putExtra("dropoffLocation","ruoquanmingwu");
                intent1.putExtra("dropoffLatitude",29.412858225117553);
                intent1.putExtra("dropoffLongitude",106.4970601379013);

                startActivity(intent1);
            }
        });

    }
}