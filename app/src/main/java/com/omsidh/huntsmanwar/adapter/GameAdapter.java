package com.omsidh.huntsmanwar.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.MatchActivity;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.model.GamePojo;
import com.omsidh.huntsmanwar.utils.ExtraOperations;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private Context context;
    private List<GamePojo> gamePojoList;

    public GameAdapter(List<GamePojo> gamePojoList, Context context){
        super();
        this.gamePojoList = gamePojoList;
        this.context = context;
    }

    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_games, parent, false);
        GameAdapter.ViewHolder viewHolder = new GameAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final GameAdapter.ViewHolder holder, int position) {
        final GamePojo gamePojo =  gamePojoList.get(position);

        holder.gameTitle.setText(gamePojo.getTitle());

        if (!gamePojo.getBanner().isEmpty()) {
            holder.gameImage.setVisibility(View.VISIBLE);
            Picasso.get().load(Config.FILE_PATH_URL+gamePojo.getBanner().replace(" ", "%20")).placeholder(R.drawable.pubg_banner).into(holder.gameImage);
        }
        else {
            holder.gameImage.setVisibility(View.GONE);
        }

        holder.mainCard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (gamePojo.getType().equals("0")){
                    if (new ExtraOperations().haveNetworkConnection(context)) {
                        Intent intent = new Intent(context, MatchActivity.class);
                        intent.putExtra("ID_KEY", gamePojo.getId());
                        intent.putExtra("TITLE_KEY", gamePojo.getTitle());
                        intent.putExtra("URL_KEY", gamePojo.getUrl());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (new ExtraOperations().haveNetworkConnection(context)) {
                        try {
                            if (!gamePojo.getUrl().startsWith("http://") && !gamePojo.getUrl().startsWith("https://")){
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://"+gamePojo.getUrl())));
                            }
                            else {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(gamePojo.getUrl())));
                            }
                        }catch (ActivityNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return gamePojoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView mainCard;
        TextView gameTitle;
        ImageView gameImage;

        public ViewHolder(View itemView) {
            super(itemView);

            this.gameImage = (ImageView) itemView.findViewById(R.id.gameImage);
            this.gameTitle = (TextView) itemView.findViewById(R.id.gameTitle);
            this.mainCard = (CardView) itemView.findViewById(R.id.mainCard);
        }

    }

}

