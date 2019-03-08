package com.hexx.transitionapp;

import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String[] imgs = {
            "https://img.picbling.cn/FpuPOZjvlrPSpJuGXzj7Bdpv17Hz-adminWorkPhotoMiddle",
            "https://img.picbling.cn/i43c5_171123_153408_829021758_4whd7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/nd8ow_171123_153416_854021758_8nhix-adminWorkPhotoMiddle",
            "https://img.picbling.cn/s5jw4_171123_153420_740021758_uu3x0-adminWorkPhotoMiddle",
            "https://img.picbling.cn/8dr53_171123_153424_555021758_35ba2-adminWorkPhotoMiddle",
            "https://img.picbling.cn/22a6e5a0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/2d1f8fa0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/bwvej_171123_153411_723021758_6svy1-adminWorkPhotoMiddle",
            "https://img.picbling.cn/e8c2fdf0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/c336b7c0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/34a119b0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
            "https://img.picbling.cn/319d0170-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/2eb17a40-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/2baac9f0-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/298ad020-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/246dea00-eb9f-11e7-ba78-77d93dd5cad7-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/b5uck_171123_153422_687021758_xnk3c-adminWorkPhotoMiddle",
//            "https://img.picbling.cn/7ff1z_171123_153421_639021758_osri4-adminWorkPhotoMiddle"
    };

    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;
    private Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rv_imgs);
        mAdapter = new ImageAdapter();
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mAdapter);
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (mExtras != null) {
                    int img_position = mExtras.getInt("img_position", 0);
                    sharedElements.clear();
                    sharedElements.put("share_img", mRecyclerView.getLayoutManager().findViewByPosition(img_position));
                    mExtras = null;
                }
            }
        });
    }

    @Override
    public void onActivityReenter(final int resultCode, Intent data) {
        mExtras = data.getExtras();
        int img_position = mExtras.getInt("img_position", 0);
        mRecyclerView.smoothScrollToPosition(img_position);
        ActivityCompat.postponeEnterTransition(this);
        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                ActivityCompat.startPostponedEnterTransition(MainActivity.this);
                return true;
            }

        });
        super.onActivityReenter(resultCode, data);
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View inflate = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_list_image, null, false);
            return new Holder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull final Holder holder, final int i) {
            Glide.with(MainActivity.this).asBitmap().load(imgs[i]).into(holder.iv);
            holder.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, holder.iv, "share_img").toBundle();
                    PreviewActivity.start(MainActivity.this, imgs, i, bundle);
                }
            });
        }

        @Override
        public int getItemCount() {
            return imgs.length;
        }

        public class Holder extends RecyclerView.ViewHolder {
            ImageView iv;

            public Holder(@NonNull View itemView) {
                super(itemView);
                iv = itemView.findViewById(R.id.iv_list_img);
            }
        }
    }

}
