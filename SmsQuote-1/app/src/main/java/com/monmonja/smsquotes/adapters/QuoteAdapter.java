package com.monmonja.smsquotes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.monmonja.smsquotes.R;
import com.monmonja.smsquotes.models.Quote;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by almondjoseph on 29/1/15.
 */
public class QuoteAdapter  extends RecyclerView.Adapter <QuoteAdapter.ItemViewHolder> {

    private final Context mContext;
    private ArrayList<Quote> arrayList;

    public QuoteAdapter(Context context, Quote[] quotes) {
        mContext = context;
        arrayList = new ArrayList<Quote>();
        addItems(quotes);
    }

    public void addItems(Quote[] quotes) {
        for (Quote quote: quotes) {
            arrayList.add(quote);
        }
        notifyDataSetChanged();
    }

    public void reset() {
        arrayList = new ArrayList<Quote>();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.quote_text_view) TextView quoteTxt;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_quote, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, int i) {
        Quote quote = arrayList.get(i);
        viewHolder.quoteTxt.setText(quote.quote);
    }
}
