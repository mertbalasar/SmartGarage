package com.ayhanunal.akilligaraj;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class YetkiliRecycleAdapter extends RecyclerView.Adapter<YetkiliRecycleAdapter.PostHolder>{

    private ArrayList<String> kulEmailList;
    private ArrayList<String> kulAdList;
    private ArrayList<String> kulYerList;
    private ArrayList<String> kulResimList;


    public YetkiliRecycleAdapter(ArrayList<String> kulEmailList, ArrayList<String> kulAdList, ArrayList<String> kulYerList, ArrayList<String> kulResimList) {
        this.kulEmailList = kulEmailList;
        this.kulAdList = kulAdList;
        this.kulYerList = kulYerList;
        this.kulResimList = kulResimList;

    }


    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_satir, parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {

        holder.emailText.setText("Email Adresi : " + kulEmailList.get(position));
        holder.parkYerText.setText("Park Yeri : " + kulYerList.get(position));
        holder.adText.setText("Kullanıcı Adı : " + kulAdList.get(position));
        Picasso.get().load(kulResimList.get(position)).into(holder.imageView);



    }

    @Override
    public int getItemCount() {
        // recyler view da kaç row olacak onu belirliyoruz. olusturdugumuz array sayısı kadar.
        return kulEmailList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView adText;
        TextView emailText;
        TextView parkYerText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.kulResmi);
            adText = itemView.findViewById(R.id.kulAdiText);
            emailText = itemView.findViewById(R.id.emailText);
            parkYerText = itemView.findViewById(R.id.parkYeriText);


        }
    }

}
