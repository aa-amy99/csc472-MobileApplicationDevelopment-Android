package com.amy.stockwatch;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


public class StockViewHolder  extends RecyclerView.ViewHolder {

    TextView symbolView;
    TextView nameView;
    TextView priceView;
    TextView priceChangeView;
    TextView percentChangeView;

    StockViewHolder (View view) {
        super(view);
        symbolView = view.findViewById(R.id.symbolView);
        nameView = view.findViewById(R.id.nameView);
        priceView = view.findViewById(R.id.priceView);
        priceChangeView = view.findViewById(R.id.priceChangeView);
        percentChangeView = view.findViewById(R.id.percentChangeView);

    }

}