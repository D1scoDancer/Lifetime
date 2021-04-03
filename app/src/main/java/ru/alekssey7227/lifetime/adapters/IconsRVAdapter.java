package ru.alekssey7227.lifetime.adapters;

import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.MainActivity;

public class IconsRVAdapter extends RecyclerView.Adapter<IconsRVAdapter.ViewHolder> {

    private TypedArray icons;

    private MainActivity mainActivity;

    public IconsRVAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        icons = mainActivity.getResources().obtainTypedArray(R.array.goal_icons);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.icon_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.iv_icon.setImageDrawable(icons.getDrawable(position));

        holder.parent.setOnClickListener(v -> {
            // TODO: return intent with id and status OK and then IconFragment returns this id to GoalFragment and closes itself
        });

    }

    @Override
    public int getItemCount() {
        return icons.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        RelativeLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            parent = itemView.findViewById(R.id.icon_parent);
        }
    }
}
