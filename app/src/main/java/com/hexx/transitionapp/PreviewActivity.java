package com.hexx.transitionapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import view.DragViewPager;
import view.PreviewFragment;

/**
 * Created by Hexx on 2019/3/6 15:10
 * Desc：
 */
public class PreviewActivity extends AppCompatActivity {

    private int mPosition;
    private DragViewPager mPager;
    private TextView mTvCount;
    private String[] imgUrl = {};
    private FragmentAdapter mFragmentAdapter;

    public static void start(Context context, String[] imgs, int position, Bundle bundle) {
        Intent starter = new Intent(context, PreviewActivity.class);
        starter.putExtra("imgs", imgs);
        starter.putExtra("position", position);
        context.startActivity(starter, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mPager = findViewById(R.id.view_pager);
        mTvCount = findViewById(R.id.tv_page_count);

        imgUrl = getIntent().getStringArrayExtra("imgs");
        mPosition = getIntent().getIntExtra("position", 0);

        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), imgUrl);
        mPager.setAdapter(mFragmentAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mTvCount.setText(String.format(Locale.CHINA, "%d/%d", i + 1, imgUrl.length));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mPager.setCurrentItem(mPosition);
        mPager.setImageCloseCallback(new Runnable() {
            @Override
            public void run() {
                finishWithTransition();
            }
        });
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                View view = mFragmentAdapter.getItem(mPager.getCurrentItem()).getView().findViewById(R.id.iv_img);
                sharedElements.clear();
                sharedElements.put("share_img", view);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishWithTransition();
    }

    private void finishWithTransition() {
        Intent intent = new Intent();
        intent.putExtra("img_position", mPager.getCurrentItem());
        setResult(RESULT_OK, intent);
        ActivityCompat.finishAfterTransition(this);
    }


    public class FragmentAdapter extends FragmentStatePagerAdapter {

        List<Fragment> mFragments;
        String[] mUrls;

        public FragmentAdapter(FragmentManager fm, String[] urls) {
            super(fm);
            this.mUrls = urls;
            loadFragment();
        }

        private void loadFragment() {
            mFragments = new ArrayList<>();
            for (int i = 0; i < mUrls.length; i++) {
                PreviewFragment previewFragment = PreviewFragment.newInstance(mUrls[i]);
                previewFragment.setLoadListener(new PreviewFragment.LoadListener() {
                    @Override
                    public void onViewCreated(View bg, View img) {
                        img.setTransitionName("share_img");
                        mPager.setCurrentView(img);
                        mPager.setBackgroundView(bg);
                    }
                });
                mFragments.add(previewFragment);
            }
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
