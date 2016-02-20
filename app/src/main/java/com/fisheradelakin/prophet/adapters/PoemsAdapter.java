package com.fisheradelakin.prophet.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fisheradelakin.prophet.R;
import com.fisheradelakin.prophet.model.Poem;
import com.fisheradelakin.prophet.ui.NewPoemActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by temidayo on 2/20/16.
 */
public class PoemsAdapter extends RecyclerView.Adapter<PoemsAdapter.ViewHolder> {

    Context mContext;
    RealmResults<Poem> mPoems;

    public PoemsAdapter(Context context, RealmResults<Poem> poems) {
        mContext = context;
        mPoems = poems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.poem_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Poem poem = mPoems.get(position);
        holder.poemTitle.setText(poem.getTitle());
    }

    @Override
    public int getItemCount() {
        return mPoems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        @Bind(R.id.poem_title_tv)
        TextView poemTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, NewPoemActivity.class);
            intent.putExtra("time", mPoems.get(getLayoutPosition()).getTimestamp());
            mContext.startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            // delete poem
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Poem poem = mPoems.get(getLayoutPosition());
            poem.removeFromRealm();
            realm.commitTransaction();
            notifyDataSetChanged();
            return true;
        }
    }
}
