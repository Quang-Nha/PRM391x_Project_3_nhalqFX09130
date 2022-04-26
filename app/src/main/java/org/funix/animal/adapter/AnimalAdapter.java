package org.funix.animal.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.funix.animal.R;
import org.funix.animal.interfaces.RvItemClicked;
import org.funix.animal.model.Animal;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * adapter của {@link RecyclerView}
 */
public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.AnimalViewHolder> {

    List<Animal> animals;
    RvItemClicked rvItemClicked;

    public AnimalAdapter(List<Animal> listAnimals, RvItemClicked rvItemClicked) {
        this.animals = listAnimals;
        this.rvItemClicked = rvItemClicked;
    }

    /**
     * ViewHolder extends RecyclerView.ViewHolder chứa layout tự tạo
     */
    protected static class AnimalViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAnimal, ivFavorite;
        TextView tvName;

        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAnimal = itemView.findViewById(R.id.iv_ic_animal);
            ivFavorite = itemView.findViewById(R.id.iv_favorite);
            tvName = itemView.findViewById(R.id.tv_name);

        }
    }


    /**
     * khởi tạo AnimalViewHolder, gán cho layout item_animal
     *
     * @return AnimalViewHolder
     */
    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_animal, parent, false);

        return new AnimalViewHolder(v);
    }

    /**
     * liên kết các view của AnimalViewHolder với Animal trong list ở position tương ứng
     *
     * @param holder   AnimalViewHolder
     * @param position vị trí Animal trong list
     */
    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Animal animal = animals.get(position);

        holder.ivAnimal.setImageBitmap(animal.getPhoto());
        holder.tvName.setText(animal.getName());

        // ẩn, hiện icon favorite theo thuộc tính isFav của animal
        if (animal.isFav()) {
            holder.ivFavorite.setVisibility(View.VISIBLE);
        } else {
            holder.ivFavorite.setVisibility(View.GONE);
        }

        /* khi click vào holder gọi onRvItemClicked của interface rvItemClicked đã được truyền bằng
        lớp ẩn danh và truyền vào position đang xét */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set animation cho view được click
                Animation blink = AnimationUtils.loadAnimation(view.getContext(), R.anim.blink);
                view.startAnimation(blink);

                // tạo luồng trễ chuyển sang màn hình fragment detail
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        rvItemClicked.onRvItemClicked(position);
                    }
                };

                Timer timer = new Timer();

                /* chạy timerTask sau 0.2s để xem rõ animation của view được click
                 rồi mới chạy luồng chuyển màn hình */
                timer.schedule(timerTask, 200L);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (animals != null) {
            return animals.size();
        }
        return 0;
    }
}
