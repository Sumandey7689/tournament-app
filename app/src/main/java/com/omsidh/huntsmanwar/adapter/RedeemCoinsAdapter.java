package com.omsidh.huntsmanwar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.MyWalletActivity;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.model.PayoutPojo;
import com.omsidh.huntsmanwar.views.TextView_Lato;
import com.squareup.picasso.Picasso;

public class RedeemCoinsAdapter extends RecyclerView.Adapter<RedeemCoinsAdapter.ViewHolder> {

    private Context context;
    private List<PayoutPojo> payoutList;

    public RedeemCoinsAdapter(List<PayoutPojo> payoutList, Context context){
        super();
        this.payoutList = payoutList;
        this.context = context;
    }

    @Override
    public RedeemCoinsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_redeem, parent, false);
        RedeemCoinsAdapter.ViewHolder viewHolder = new RedeemCoinsAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RedeemCoinsAdapter.ViewHolder holder, int position) {
        final PayoutPojo payoutPojo =  payoutList.get(position);

        holder.tnName.setText(payoutPojo.getTitle());
        holder.tncat.setText(payoutPojo.getSubtitle());
        holder.amount.setText(payoutPojo.getAmount()+" "+payoutPojo.getCurrency());

        if (!payoutPojo.getImage().equals("null")) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+payoutPojo.getImage()).resize(120,120).noFade().into(holder.image);
        }
        else {
            holder.image.setVisibility(View.GONE);
        }

        holder.SingleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ((MyWalletActivity)context).Redeem(payoutPojo.getTitle(), payoutPojo.getSubtitle(), payoutPojo.getMessage(), payoutPojo.getAmount(), payoutPojo.getCoins(), payoutPojo.getId(), payoutPojo.getStatus(), payoutPojo.getImage(),payoutPojo.getType(),payoutPojo.getCurrency());
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return payoutList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView_Lato date,tnName,tncat,amount;
        CircleImageView image;
        LinearLayout SingleItem;

        public ViewHolder(View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            tnName = itemView.findViewById(R.id.tnName);
            tncat = itemView.findViewById(R.id.tnType);
            amount = itemView.findViewById(R.id.amount);
            image = itemView.findViewById(R.id.image);
            SingleItem = itemView.findViewById(R.id.SingleItem);
        }

    }
}
