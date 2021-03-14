package ru.alekssey7227.lifetime.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.MainActivity;
import ru.alekssey7227.lifetime.database.DBHelper;

public class GoalDialogFragment extends DialogFragment {

    public static final String TAG = "GoalDialogFragment";

    private Toolbar toolbar;
    private static MainActivity mainActivity;

    EditText et_Name, et_Time, et_Iteration;
    ImageView iv_palette, iv_icon_chooser;

    public static GoalDialogFragment display(FragmentManager fragmentManager, MainActivity activity) {
        mainActivity = activity;

        GoalDialogFragment goalDialogFragment = new GoalDialogFragment();
        goalDialogFragment.show(fragmentManager, TAG);
        return goalDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.goal_fragment, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        et_Name = view.findViewById(R.id.et_Name);
        et_Time = view.findViewById(R.id.et_Time);
        et_Iteration = view.findViewById(R.id.et_Iteration);
        iv_palette = view.findViewById(R.id.iv_palette);
        iv_icon_chooser = view.findViewById(R.id.iv_icon_chooser);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v ->
                dismiss());
        toolbar.setTitle("Creating Goal");
        toolbar.inflateMenu(R.menu.goal_dialog_menu);
        // добавление новой цели в БД
        toolbar.setOnMenuItemClickListener(item -> {
            SQLiteOpenHelper dbHelper = new DBHelper(getContext());
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues contentValues = getInput();

            database.insert(DBHelper.TABLE_GOALS, null, contentValues);

            mainActivity.rvUpdate();
            dismiss();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    private ContentValues getInput(){
        ContentValues contentValues = new ContentValues();

        String name = et_Name.getText().toString();
        String time = et_Time.getText().toString();
        String iteration = et_Iteration.getText().toString();

        contentValues.put(DBHelper.KEY_NAME, name);
        contentValues.put(DBHelper.KEY_TIME, time);
        contentValues.put(DBHelper.KEY_ITERATION, iteration);

        return contentValues;
    }
}
