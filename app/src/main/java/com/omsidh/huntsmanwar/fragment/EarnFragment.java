package com.omsidh.huntsmanwar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.LotteryActivity;
import com.omsidh.huntsmanwar.activity.MainActivity;
import com.omsidh.huntsmanwar.activity.ProductActivity;
import com.omsidh.huntsmanwar.activity.RefereEarnActivity;

public class EarnFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isNavigationHide = false;

    private LinearLayout referandearncard;
    private LinearLayout shopcard;
    private LinearLayout lotterycard;

    public EarnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_earn, container, false);

        referandearncard = view.findViewById(R.id.referandearncard);
        shopcard = view.findViewById(R.id.shopcard);
        lotterycard = view.findViewById(R.id.lotterycard);

        referandearncard.setOnClickListener(this);
        shopcard.setOnClickListener(this);
        lotterycard.setOnClickListener(this);

        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                }
            }
        });

        return view;
    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.referandearncard){
            this.startActivity(new Intent(this.getActivity(), RefereEarnActivity.class));
        }
        else if (v.getId()==R.id.shopcard){
            this.startActivity(new Intent(this.getActivity(), ProductActivity.class));
        }
        else if (v.getId()==R.id.lotterycard){
            this.startActivity(new Intent(this.getActivity(), LotteryActivity.class));
        }
    }

}
