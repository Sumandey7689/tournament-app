package com.omsidh.huntsmanwar.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.model.Notice2Class;
import com.squareup.picasso.Picasso;

import java.util.List;

public class Notice2Adapter extends PagerAdapter {
    private Context context;
    private List<Notice2Class> imageUrls;

    public Notice2Adapter(Context context, List<Notice2Class> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;


    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_notice2, null);


        ImageView imageView = view.findViewById(R.id.imageview2);

        Notice2Class notice2Class = imageUrls.get(position);

        Picasso.get()
                .load(notice2Class.getImage())
                .fit()
                .centerCrop()
                .into(imageView);

        imageView.setOnClickListener(v -> {

            if (notice2Class.getUrl().length() > 4) {
                if (Patterns.WEB_URL.matcher(notice2Class.getUrl().toLowerCase()).matches()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(notice2Class.getUrl()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }


//
//                Toast.makeText(context, notice2Class.getUrl(), Toast.LENGTH_LONG).show();

        });

//        Toast.makeText(context, notice2Class.getImage(), Toast.LENGTH_LONG).show();

//        imageView.setBackgroundResource(icon.get(position));

        ViewPager viewPager = (ViewPager) parent;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup parent, int position, Object object) {
        ViewPager viewPager = (ViewPager) parent;
        View view = (View) object;
        viewPager.removeView(view);
    }
}