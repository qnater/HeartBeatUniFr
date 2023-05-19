package com.example.angerdetection.Display;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.angerdetection.databinding.RecyclerviewItemBinding;

import java.util.ArrayList;

/**
 * Adapter for displaying the result of the query on a view holder
 *
 * @autor Quentin Nater
 */
public class ViewHolderAdapter extends RecyclerView.Adapter<ViewHolderAdapter.ViewHolder >
{
    private ArrayList<String> mDataList;

    public ViewHolderAdapter(ArrayList<String> dataList)
    {
        mDataList = dataList;
    }

    /**
     * Create the layout the the recycler view based on the xml file
     *
     * @autor Quentin Nater
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerviewItemBinding binding = RecyclerviewItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }


    /**
     * Bind the text of each line of the view holder
     *
     * @autor Quentin Nater
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        String itemText = mDataList.get(position);
        holder.binding.myDisplayLine.setText(itemText);
    }

    /**
     * To get the size of the data (number of line in the view holder)
     *
     * @autor Quentin Nater
     */
    @Override
    public int getItemCount()
    {
        return mDataList.size();
    }

    /**
     * To bind the view holder with the root of the main layout of the project
     *
     * @autor Quentin Nater
     */
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
