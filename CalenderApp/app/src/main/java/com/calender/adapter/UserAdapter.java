package com.calender.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.calender.R;
import com.calender.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by intel on 22-07-2018.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> userList;
    Context context;
    private HashMap<Integer, Boolean> selectedItem;
    private boolean isCheckBoxEnable;

    private void clearAllSelectionItem() {
        for (int index: new int[userList.size()])
            selectedItem.put(index, false);
    }

    private void clearAllSelectionItem(int position) {
        for (int index: new int[userList.size()])
            if (position != index)
            selectedItem.put(index, false);
    }

    public UserAdapter(Context context, List<User> userList, boolean isCheckBoxEnable) {
        this.context = context;
        this.userList = userList;
        this.isCheckBoxEnable = isCheckBoxEnable;
        selectedItem = new HashMap<>();
        clearAllSelectionItem();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckedTextView userNameCheckedTextView;
        private TextView userNameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            userNameCheckedTextView = itemView.findViewById(R.id.userNameCheckedTextView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.share_screen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        User user = userList.get(position);
        if (isCheckBoxEnable) {
            holder.userNameCheckedTextView.setVisibility(View.VISIBLE);
            holder.userNameTextView.setVisibility(View.GONE);
            holder.userNameCheckedTextView.setText(user.getName());
            holder.userNameCheckedTextView.setChecked(selectedItem.get(position));
        } else {
            holder.userNameCheckedTextView.setVisibility(View.GONE);
            holder.userNameTextView.setVisibility(View.VISIBLE);
            holder.userNameTextView.setText(user.getName());
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setNotifyDataSetChanged() {
        clearAllSelectionItem();
        notifyDataSetChanged();
    }

    public void updateSelectedItem(int position) {
        clearAllSelectionItem(position);
        selectedItem.put(position, !selectedItem.get(position));
        notifyDataSetChanged();
    }

    public List<User> getSelectedUserList() {
        List<User> user = new ArrayList<>();
        for (int index: new int[selectedItem.size()]) {
            if (selectedItem.get(index)) {
                user.add(userList.get(index));
                break;
            }
        }
        return user;
    }
}
