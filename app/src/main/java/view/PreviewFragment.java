package view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hexx.transitionapp.R;

/**
 * Created by Hexx on 2019/3/7 17:38
 * Desc：
 */
public class PreviewFragment extends Fragment {

    private String mUrl;
    private FrameLayout mFlBg;
    private ImageView mIvImg;
    private Context mContext;
    private LoadListener mLoadListener;
    private boolean mIsVisibleToUser;
    private boolean mViewCreated = false;
    private View mView;

    public static PreviewFragment newInstance(String url) {
        Bundle args = new Bundle();
        PreviewFragment fragment = new PreviewFragment();
        args.putCharSequence("url", url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        if (mIsVisibleToUser) {
            initData();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewCreated = true;
        initData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_image_preview, null, false);
            mFlBg = mView.findViewById(R.id.fl_bg);
            mIvImg = mView.findViewById(R.id.iv_img);
        }
        return mView;
    }

    public void setLoadListener(LoadListener loadListener) {
        mLoadListener = loadListener;
    }

    private void initData() {
        if (mViewCreated && mIsVisibleToUser) {
            if (mLoadListener != null) {
                mLoadListener.onViewCreated(mFlBg, mIvImg);
            }
            mUrl = getArguments().getString("url", "");
            Glide.with(mContext).load(mUrl).into(mIvImg);
        }
    }

    public interface LoadListener {
        void onViewCreated(View bg, View img);
    }
}
