package zhimingzhang.deakin.trucksharing;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import zhimingzhang.deakin.trucksharing.databinding.ActivityMainBinding;
import zhimingzhang.deakin.trucksharing.databinding.ItemCarBinding;
import zhimingzhang.deakin.trucksharing.databinding.ItemOrderBinding;
import zhimingzhang.deakin.trucksharing.databinding.PopupwindowMenuBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseBindingActivity<ActivityMainBinding> {
    static List<Car> cars = new ArrayList<>();
    static {
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
        cars.add(new Car(App.randomGenerateReceiver(), App.randomInteger(), App.randomCarFace()));
    }
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
    private BindAdapter<ItemOrderBinding, Order> adapter = new BindAdapter<ItemOrderBinding, Order>() {
        @Override
        public ItemOrderBinding createHolder(ViewGroup parent) {
            return ItemOrderBinding.inflate(getLayoutInflater());
        }

        @Override
        public void bind(ItemOrderBinding itemOrderBinding, Order order, int position) {
            itemOrderBinding.tvReceiverName.setText("Receiver:" + order.receiverName);
            String format = dateFormat.format(Long.parseLong(order.timestamp));
            Log.d("ssss", "bind: " + format);
            itemOrderBinding.tvTimeStart.setText("Time:" + format);
            itemOrderBinding.tvStartLoaction.setText("Location:" + order.location);
            itemOrderBinding.face.setImageResource(order.face);
            itemOrderBinding.getRoot().setOnClickListener(view -> {
                startActivity(OrderDetailActivity.class, intent -> intent.putExtra("order_detail", order));
            });
            itemOrderBinding.ivShare.setOnClickListener(view -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,order.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });
        }
    };

    private BindAdapter<ItemCarBinding, Car> carAdapter = new BindAdapter<ItemCarBinding, Car>() {
        {
            getData().addAll(cars);
        }

        @Override
        public ItemCarBinding createHolder(ViewGroup parent) {
            return ItemCarBinding.inflate(getLayoutInflater());
        }

        @Override
        public void bind(ItemCarBinding itemOrderBinding, Car car, int position) {
            itemOrderBinding.tvName.setText("Car owner:" + car.name);
            itemOrderBinding.tvPrice.setText("Price:" + car.price + "$/h");
            itemOrderBinding.face.setImageResource(car.face);
            itemOrderBinding.ivShare.setOnClickListener(view -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,car.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            });
        }
    };

    @Override
    protected void initListener() {

    }




    @Override
    protected void initData() {
        viewBinder.ivAddNew.setOnClickListener(view -> {
            startActivityForResult(AddOrderStep1Activity.class, null, 100);
        });

        viewBinder.ivMenu.setOnClickListener(view -> {
            PopupwindowMenuBinding binding = PopupwindowMenuBinding.inflate(getLayoutInflater());

            MenuPopupWindow popupWindow = new MenuPopupWindow(binding.getRoot());
            popupWindow.showAsDropDown(viewBinder.ivMenu);
            binding.getRoot().setOnClickListener(view1 -> {
                popupWindow.dismiss();
            });
            binding.tvMyOrder.setOnClickListener(view1 -> {
                viewBinder.recycler.setAdapter(adapter);
                popupWindow.dismiss();
            });
            binding.home.setOnClickListener(view1 -> {
                viewBinder.recycler.setAdapter(carAdapter);
                popupWindow.dismiss();
            });

        });
        adapter.getData().addAll(DBHelper.getHelper().queryMyOrder());
        viewBinder.recycler.setAdapter(carAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            adapter.getData().clear();
            adapter.getData().addAll(DBHelper.getHelper().queryMyOrder());
            adapter.notifyDataSetChanged();
        }
    }
}