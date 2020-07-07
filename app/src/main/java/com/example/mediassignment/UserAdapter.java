package com.example.mediassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mediassignment.room.UserEntity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {

    private Context mCtx;
    private List<UserEntity> userList;

    public UserAdapter(Context mCtx, List<UserEntity> userList) {
        this.mCtx = mCtx;
        this.userList = userList;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.user_data_card, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        UserEntity t = userList.get(position);
        holder.textViewFirstName.setText(t.getFirstName());
        holder.textViewLastName.setText(t.getLastName());
        holder.textViewGender.setText(t.getGender());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewFirstName, textViewLastName, textViewGender;

        public UsersViewHolder(View itemView) {
            super(itemView);

            textViewFirstName = itemView.findViewById(R.id.tv_first_name);
            textViewLastName = itemView.findViewById(R.id.tv_last_name);
            textViewGender = itemView.findViewById(R.id.tv_gender);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}