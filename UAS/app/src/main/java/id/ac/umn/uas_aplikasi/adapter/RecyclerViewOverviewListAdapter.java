package id.ac.umn.uas_aplikasi.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.detail.EatenHelper;

public class RecyclerViewOverviewListAdapter extends ArrayAdapter<EatenHelper> {

    private List<EatenHelper> items;
    private int layoutResourceId;
    private Context context;

    public RecyclerViewOverviewListAdapter(Context context, int layoutResourceId, List<EatenHelper> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EatenHelperHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new EatenHelperHolder();
        holder.EatenHelper = items.get(position);
        holder.removeEatenHelperButton = (ImageButton)row.findViewById(R.id.deleteBtn);
        holder.removeEatenHelperButton.setTag(holder.EatenHelper);
        holder.detailEatenHelperButton = (ImageButton)row.findViewById(R.id.detailBtn);
        holder.detailEatenHelperButton.setTag(holder.EatenHelper);

        holder.name = (TextView)row.findViewById(R.id.eatenMealName);

        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(EatenHelperHolder holder) {
        holder.name.setText(holder.EatenHelper.getFoodName());
    }

    public static class EatenHelperHolder {
        EatenHelper EatenHelper;
        TextView name;
        ImageButton removeEatenHelperButton;
        ImageButton detailEatenHelperButton;
    }
}