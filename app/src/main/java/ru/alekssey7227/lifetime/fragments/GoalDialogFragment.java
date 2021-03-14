package ru.alekssey7227.lifetime.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.MainActivity;
import ru.alekssey7227.lifetime.database.DBHelper;

public class GoalDialogFragment extends DialogFragment {

    public static final String TAG = "GoalDialogFragment";

    private Toolbar toolbar;
    private static MainActivity mainActivity;

    TextInputLayout text_input_name, text_input_time, text_input_iteration;
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
        text_input_name = view.findViewById(R.id.text_input_name);
        text_input_time = view.findViewById(R.id.text_input_time);
        text_input_iteration = view.findViewById(R.id.text_input_iteration);
//        iv_palette = view.findViewById(R.id.iv_palette);
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
            if (validateInput()) {
                SQLiteOpenHelper dbHelper = new DBHelper(getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                ContentValues contentValues = getInput();
                database.insert(DBHelper.TABLE_GOALS, null, contentValues);
                mainActivity.rvUpdate();
                dismiss();
                return true;
            } else {
                return false;
            }
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

    private ContentValues getInput() {
        ContentValues contentValues = new ContentValues();

        String name = text_input_name.getEditText().getText().toString();
        String time = text_input_time.getEditText().getText().toString();
        String iteration = text_input_iteration.getEditText().getText().toString();

        contentValues.put(DBHelper.KEY_NAME, name);
        contentValues.put(DBHelper.KEY_TIME, time);
        contentValues.put(DBHelper.KEY_ITERATION, iteration);

        return contentValues;
    }

    private boolean validateInput() {
        return !(!validateName() | !validateTime() | !validateIteration());
    }

    private boolean validateName() {
        String name = text_input_name.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            text_input_name.setError("Field cannot be empty");
            return false;
        } else {
            text_input_name.setError(null);
            return true;
        }
    }

    private boolean validateTime() {
        String time = text_input_time.getEditText().getText().toString().trim();
        if (time.isEmpty()) {
            text_input_time.setError("Field cannot be empty");
            return false;
        } else {
            text_input_time.setError(null);
            return true;
        }
    }

    private boolean validateIteration() {
        String iteration = text_input_iteration.getEditText().getText().toString().trim();
        if (iteration.isEmpty()) {
            text_input_iteration.setError("Field cannot be empty");
            return false;
        } else {
            text_input_iteration.setError(null);
            return true;
        }
    }
}
