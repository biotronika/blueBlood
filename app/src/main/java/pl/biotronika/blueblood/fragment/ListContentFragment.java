
package pl.biotronika.blueblood.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pl.biotronika.blueblood.BiotronikaApplication;
import pl.biotronika.blueblood.R;
import pl.biotronika.blueblood.activity.DetailActivity_;
import pl.biotronika.blueblood.json.TerapieApi;


public class ListContentFragment extends RelodableFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    RecyclerView recyclerView;
    private static Bundle mBundleRecyclerViewState;
    private final String KEY_RECYCLER_STATE = "list_recycler_state";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ListContentFragment", "onCreateView");
        recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        return recyclerView;
    }


    @Override
    public void reload() {
        try {
        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        adapter.reload();
        } catch ( Exception ex){}
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatar;
        public TextView name;
        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));
            avatar = itemView.findViewById(R.id.list_avatar);
            name = itemView.findViewById(R.id.list_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity_.class);
                    intent.putExtra(DetailActivity_.EXTRA_POSITION, getAdapterPosition());
                    context.startActivity(intent);

                }
            });
        }
    }

    public class ContentAdapter extends RecyclerView.Adapter<ViewHolder>  {

        int modulo = 0;
        private String[] names;

        public ContentAdapter(Context context) {
           reload();
        }

        public void reload() {
            names = TerapieApi.getNames();
            modulo = names.length ;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Drawable myDrawable = BiotronikaApplication.getContext().getResources().getDrawable(R.drawable.blue_blood2);
            holder.avatar.setImageDrawable(myDrawable);
            holder.name.setText(names[position % modulo]);
        }

        @Override
        public int getItemCount() {
            return modulo;
        }
    }


    @Override
    public void onPause() {

        super.onPause();
        mBundleRecyclerViewState = new Bundle();
        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, listState);

    }


    @Override
    public void onResume() {

        super.onResume();
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals("key_enable_extended_therapies")
                || key.equals("key_dev_freepemf")
                || key.equals("key_dev_multizap")
                || key.equals("key_device_filter")
                || key.equals("key_enable_basic_therapies")
                || key.equals("key_enable_pitchfork_therapies")
                ) {
            TerapieApi.resetList();
            ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(
                isVisibleToUser);
        try {
            if (getFragmentManager() != null) {

                getFragmentManager()
                        .beginTransaction()
                        .detach(this)
                        .attach(this)
                        .commit();
            }
        } catch (Exception ex) {}
    }
}
