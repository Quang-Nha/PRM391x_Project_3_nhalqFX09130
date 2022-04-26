package org.funix.animal.adapter;

import static org.funix.animal.activity.MainActivity.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.funix.animal.R;
import org.funix.animal.activity.MainActivity;
import org.funix.animal.model.Animal;

import java.util.List;

/**
 * apdapter của {@link androidx.viewpager.widget.ViewPager}
 */
public class DetailPagerAdapter extends PagerAdapter {
    private final List<Animal> animals;

    SharedPreferences sharedPreferences;

    public DetailPagerAdapter(List<Animal> animals) {
        this.animals = animals;
    }

    /**
     * ánh xạ 1 item view từ thư mục layout vào trong chương trình
     * Sau đó đổ dữ liệu ứng với data tương ứng vào
     *
     * @param container view chứa các item view
     * @param position  vị trí trang tương ứng với phần tử trong list
     * @return item view cần lấy
     */
    @SuppressLint("CommitPrefEdits")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        sharedPreferences = container.getContext().getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);

        //Ánh xạ item view vào trong môi trường code
        View v = LayoutInflater.from(container.getContext()).inflate(R.layout.item_detail, container, false);

        //Ánh xạ các view ra từ itemView dựa vào id
        ImageView ivBg = v.findViewById(R.id.iv_bg);
        ImageView ivDetailFavorite = v.findViewById(R.id.iv_detail_favorite);
        TextView tvDetailName = v.findViewById(R.id.tv_detail_name);
        TextView tvDetail = v.findViewById(R.id.tv_detail);

        TextView tvPhone = v.findViewById(R.id.tv_phone);
        ImageView ivCall = v.findViewById(R.id.iv_call);

        // lấy animal trong list với position tương ứng
        Animal animal = animals.get(position);

        // set các view của itemView dựa vào animal
        ivBg.setImageBitmap(animal.getPhotoBG());
        tvDetailName.setText(animal.getName());
        tvDetail.setText(animal.getContent());

        // set phone cho tvPhone lấy từ bộ nhớ với key là path của animal + "_phone"
        tvPhone.setText(sharedPreferences
                .getString(animal.getPath() + "_phone", null));

        // set icon tương ứng với trạng thái thích
        if (animal.isFav()) {
            ivDetailFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            ivDetailFavorite.setImageResource(R.drawable.ic_not_favorite);
        }

        // xử lý sự kiện click vào hình trạng thái like
        ivDetailFavorite.setOnClickListener(view -> {

            /* nếu đang thích thì chuyển sang không thích và ngược lại,
             set ảnh và thuộc tính like của animal lại tương ứng
             sửa lại dữ liệu trong sharedPreferences với key là path ảnh icon của animal */
            if (animal.isFav()) {
                ivDetailFavorite.setImageResource(R.drawable.ic_not_favorite);
                animal.setIsFav(false);
                sharedPreferences.edit().putBoolean(animal.getPath(), false).apply();

            } else {
                ivDetailFavorite.setImageResource(R.drawable.ic_favorite);
                animal.setIsFav(true);
                sharedPreferences.edit().putBoolean(animal.getPath(), true).apply();

            }
        });

        // bắt sự kiện nhấn nút điện thoại ivCall
        ivCall.setOnClickListener(view -> {

            showDialog(animal, view, tvPhone);

        });

        // thêm layout item vào container để hiển thị
        container.addView(v);

        return v;
    }

    /**
     * hiển thị dialog
     *
     * @param animal  động vật đang xem
     * @param view    view đã click
     * @param tvPhone cần set text từ kết quả nhập vào dialog
     */
    private void showDialog(Animal animal, View view, TextView tvPhone) {

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // cài đặt dialog, lấy layout liên kết, cho phép hủy khi click ra ngoài
        Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_input_phone);
        dialog.setCancelable(true);

        // lấy các view từ layout liên kết
        ImageView ivIcon = dialog.findViewById(R.id.iv_ic);
        EditText etPhone = dialog.findViewById(R.id.et_phone);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnDelete = dialog.findViewById(R.id.btn_delete);

        // set ảnh là icon của animal đang xem
        ivIcon.setImageBitmap(animal.getPhoto());

        // lấy text của tvPhone gán cho etPhone
        etPhone.setText(tvPhone.getText().toString());

        // khi ấn nút lưu set text cho tvPhone là sđt etPhone đã nhập đồng thời thoát dialog và ẩn bàn phím
        // cập nhật giá trị vào bộ nhớ
        btnSave.setOnClickListener(view1 -> {

            // lấy sdt đã nhập ở etPhone
            String phone = etPhone.getText().toString();

            // xem sdt đã tồn tại chưa bằng cách dùng nó làm key lấy dữ liệu trong bộ nhớ
            String path = sharedPreferences.getString(phone, null);

            // nếu nó đã tồn tại thì thông báo nhập lại và thoát
            if (path != null) {
                Toast.makeText(view.getContext(), R.string.number_lready_exists, Toast.LENGTH_LONG).show();
                return;
            }

            // set phone đã nhập cho tvPhone
            tvPhone.setText(phone);
            dialog.dismiss();
            hideSoftKeyboard(view.getContext());

            // put key là path của animal + "_phone", value là phone đã nhập
            // put key là phone đã nhập, value là path của animal
            editor.putString(animal.getPath() + "_phone", phone);
            editor.putString(phone, animal.getPath());
            editor.apply();
        });

        // khi ấn nút xóa clear tvPhone đồng thời thoát dialog và ẩn bàn phím
        // cập nhật giá trị vào bộ nhớ
        btnDelete.setOnClickListener(view1 -> {

            // lấy phone chuẩn bị xóa ghi ở tvPhone vào biến trung gian
            String phone = tvPhone.getText().toString();

            // xóa phone hiển thị trên tvPhone
            tvPhone.setText("");
            dialog.dismiss();
            hideSoftKeyboard(view.getContext());

            // put key là path của animal + "_phone", value là phone rỗng để cập nhật lại key này
            // xóa key là sđt vừa hủy khỏi bộ nhớ
            editor.putString(animal.getPath() + "_phone", "");
            editor.remove(phone);
            editor.apply();
        });

        dialog.show();
    }

    /**
     * ẩn bàn phím
     *
     * @param context để gọi các hàm
     */
    public void hideSoftKeyboard(Context context) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(
                    Activity.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(((MainActivity) context).getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * khai báo số lượng item view (page) sẽ được sinh ra
     */
    @Override
    public int getCount() {

        if (animals != null) {
            return animals.size();
        }
        return 0;
    }

    /**
     * khi vuốt sang trái-phải để hiển thị page mới
     * Nếu vuốt chưa được 1 nửa và nhả tay ra, Page cũ sẽ được giữ lại hiển thị trên màn hình
     */
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object oldView) {
        return view.equals(oldView);
    }

    /**
     * Khi 1 Page không còn được hiển thị trên màn hình, nó sẽ bị container destroy
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
