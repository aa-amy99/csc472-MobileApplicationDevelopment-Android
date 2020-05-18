package com.amy.stockwatch;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import android.util.Log;
import android.view.LayoutInflater;

import java.util.ArrayList;

public class StockAdapter extends RecyclerView.Adapter<StockViewHolder> {

    private static final String TAG = "StockAdapter";
    private ArrayList<Stock> stockList;
    private MainActivity mainActivity;

    StockAdapter(ArrayList<Stock> list, MainActivity mainActivity) {
        stockList = list;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Log.d(TAG, "onCreateViewHolder: to create new stock item");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_stock_item, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);
        return new StockViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        //we want to fill in the viewholder
        // the position is # of items in the Arrlist
        Log.d(TAG, "onBindViewHolder: to set the data shown on RecycleView");

        Stock selectedStock = stockList.get(position);
        double priceChange = selectedStock.getPriceChange();
        holder.symbolView.setText(selectedStock.getSymbol());//set nameText to the textview
        holder.nameView.setText(selectedStock.getName());
        holder.priceView.setText(String.format("%.2f",selectedStock.getPrice()));
        holder.percentChangeView.setText(String.format("(%.2f%%)",selectedStock.getPercentChange()));
        if (priceChange < 0) {
            int colorRed = 0xFFFF0202;
            holder.symbolView.setTextColor(colorRed);
            holder.nameView.setTextColor(colorRed);
            holder.priceView.setTextColor(colorRed);
            holder.priceChangeView.setTextColor(colorRed);
            holder.percentChangeView.setTextColor(colorRed);
            holder.priceChangeView.setText(String.format("▼ %.2f", priceChange));
        }
        else {
            int colorGreen = 0xFF78FF02;
            holder.symbolView.setTextColor(colorGreen);
            holder.nameView.setTextColor(colorGreen);
            holder.priceView.setTextColor(colorGreen);
            holder.priceChangeView.setTextColor(colorGreen);
            holder.percentChangeView.setTextColor(colorGreen);
            if (priceChange == 0.0) holder.priceChangeView.setText(String.format(" %.2f", priceChange));
            else holder.priceChangeView.setText(String.format("▲ %.2f", priceChange));

        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }
}