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

public class IconDialogFragment extends DialogFragment {

    public static final String TAG = "IconDialogFragment";

    private RecyclerView iconsRV;


    public static IconDialogFragment display(FragmentManager fragmentManager){
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
        View view = inflater.inflate(R.layout.icon_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconsRV = view.findViewById(R.id.iconsRV);
        IconsRVAdapter adapter = new IconsRVAdapter(MainActivity.getInstance());
        iconsRV.setAdapter(adapter);
        iconsRV.setLayoutManager(new GridLayoutManager(view.getContext(), 5));

    }
}
