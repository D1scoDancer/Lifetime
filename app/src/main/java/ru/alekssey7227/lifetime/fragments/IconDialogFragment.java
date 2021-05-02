package ru.alekssey7227.lifetime.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.MainActivity;
import ru.alekssey7227.lifetime.adapters.IconsRVAdapter;

public class IconDialogFragment extends DialogFragment implements IconsRVAdapter.Callback {

    public static final String TAG = "IconDialogFragment";

    private RecyclerView iconsRV;

    private static GoalDialogFragment goalDialogFragment;

    public static IconDialogFragment display(FragmentManager fragmentManager, GoalDialogFragment goalDFt) {
        goalDialogFragment = goalDFt;
        IconDialogFragment iconDialogFragment = new IconDialogFragment();
        iconDialogFragment.show(fragmentManager, TAG);
        return iconDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.icon_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconsRV = view.findViewById(R.id.iconsRV);
        IconsRVAdapter adapter = new IconsRVAdapter(MainActivity.getInstance(), this);
        iconsRV.setAdapter(adapter);
        iconsRV.setLayoutManager(new GridLayoutManager(view.getContext(), 5));
    }

    @Override
    public void onDestroy() {
        goalDialogFragment = null;
        super.onDestroy();
    }

    @Override
    public void onEvent(int position) {
        ((CallBack) goalDialogFragment).onIconChanged(position);
        dismiss();
    }

    public interface CallBack {
        void onIconChanged(int position);
    }
}
