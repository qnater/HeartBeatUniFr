package com.example.checksensoravailability.Display;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.checksensoravailability.databinding.RecyclerviewItemBinding;

import java.util.ArrayList;

public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolderAdapter.ViewHolder >
{
    private ArrayList<String> mDataList;

    public ViewHolderAdapter(ArrayList<String> dataList)
    {
        mDataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerviewItemBinding binding = RecyclerviewItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String itemText = mDataList.get(position);
        holder.binding.myDisplayLine.setText(itemText);
    }

    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        RecyclerviewItemBinding binding;

        public ViewHolder(RecyclerviewItemBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
