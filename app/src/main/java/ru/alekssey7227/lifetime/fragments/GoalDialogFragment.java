package ru.alekssey7227.lifetime.fragments;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

import ru.alekssey7227.lifetime.R;
import ru.alekssey7227.lifetime.activities.MainActivity;
import ru.alekssey7227.lifetime.backend.Goal;
import ru.alekssey7227.lifetime.database.GoalDBHelper;
import ru.alekssey7227.lifetime.database.StatsDBHelper;

public class GoalDialogFragment extends DialogFragment implements IconDialogFragment.CallBack {

    public static final String TAG = "GoalDialogFragment";

    private static Goal _goal;
    private static int imagePosition;

    private Toolbar toolbar;

    private TextInputLayout text_input_name, text_input_time, text_input_iteration;

    private ImageView iv_goalIcon;


    public static GoalDialogFragment display(FragmentManager fragmentManager) {
        _goal = null;
        imagePosition = 0; // TODO: или последнее использованное изображение (удалить строку)
        GoalDialogFragment goalDialogFragment = new GoalDialogFragment();
        goalDialogFragment.show(fragmentManager, TAG);
        return goalDialogFragment;
    }

    public static GoalDialogFragment display(FragmentManager fragmentManager, Goal goal) {
        _goal = goal;
        imagePosition = goal.getImage();
        GoalDialogFragment goalDialogFragment = new GoalDialogFragment();
        goalDialogFragment.show(fragmentManager, TAG);
        return goalDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("night_mode", Context.MODE_PRIVATE);
        int nightMode = preferences.getInt("mode", -1);
        if (nightMode != -1 && nightMode != AppCompatDelegate.MODE_NIGHT_NO) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Lifetime_FullScreenDialog_Night);
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Lifetime_FullScreenDialog_Light);
        }

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
        iv_goalIcon = view.findViewById(R.id.iv_goalIcon);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillFields();

        TypedArray icons = getContext().getResources().obtainTypedArray(R.array.goal_icons);
        iv_goalIcon.setImageDrawable(icons.getDrawable(imagePosition));

        // Смена иконки цели
        iv_goalIcon.setOnClickListener(v -> {
            IconDialogFragment.display(getFragmentManager(), this);
        });

        toolbar.setNavigationOnClickListener(v ->
                dismiss());
        toolbar.inflateMenu(R.menu.goal_dialog_menu);

        if (_goal == null) {
            toolbar.setTitle(R.string.gdf_creating);
            // добавление новой цели в БД
            toolbar.setOnMenuItemClickListener(item -> {
                if (validateInput()) {
                    GoalDBHelper dbHelper = new GoalDBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    ContentValues contentValues = getInput();
                    db.insert(GoalDBHelper.TABLE_GOALS, null, contentValues);

                    db = dbHelper.getReadableDatabase();
                    List<Goal> goals = dbHelper.readAllGoals(db);

                    StatsDBHelper statsDBHelper = new StatsDBHelper(getContext());
                    SQLiteDatabase db2 = statsDBHelper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(StatsDBHelper.KEY_GOAL_ID, goals.get(goals.size() - 1).getId());
                    cv.put(StatsDBHelper.KEY_DAY, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    cv.put(StatsDBHelper.KEY_MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1);
                    cv.put(StatsDBHelper.KEY_YEAR, Calendar.getInstance().get(Calendar.YEAR));
                    cv.put(StatsDBHelper.KEY_ESTIMATED_TIME, 0);
                    db2.insert(StatsDBHelper.TABLE_STATS, null, cv);

                    MainActivity.getInstance().rvUpdate();
                    dismiss();
                    return true;
                } else {
                    return false;
                }
            });
        } else {
            toolbar.setTitle(R.string.gdf_editing);
            // редактирование цели в БД
            toolbar.setOnMenuItemClickListener(item -> {
                if (validateInput()) {
                    GoalDBHelper dbHelper = new GoalDBHelper(getContext());
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    changeGoal();
                    dbHelper.updateGoal(db, _goal);
                    MainActivity.getInstance().rvUpdate();
                    dismiss();
                    return true;
                } else {
                    return false;
                }
            });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Lifetime_Slide);
        }
    }

    private void changeGoal() {
        String name = text_input_name.getEditText().getText().toString();
        double timeInHours = Double.parseDouble(text_input_time.getEditText().getText().toString());
        double iterationInHours = Double.parseDouble(text_input_iteration.getEditText().getText().toString());
        _goal.setName(name);
        _goal.getTime().setTimeInHours(timeInHours);
        _goal.getIteration().setTimeInHours(iterationInHours);
        _goal.setImage(imagePosition);
    }

    private ContentValues getInput() {
        ContentValues contentValues = new ContentValues();

        String name = text_input_name.getEditText().getText().toString();
        double timeInHours = Double.parseDouble(text_input_time.getEditText().getText().toString());
        String time = Double.toString(timeInHours * 60);
        double iterationInHours = Double.parseDouble(text_input_iteration.getEditText().getText().toString());
        String iteration = Double.toString(iterationInHours * 60);
        contentValues.put(GoalDBHelper.KEY_NAME, name);
        contentValues.put(GoalDBHelper.KEY_TIME, time);
        contentValues.put(GoalDBHelper.KEY_ITERATION, iteration);
        contentValues.put(GoalDBHelper.KEY_IMAGE, imagePosition);

        return contentValues;
    }

    private boolean validateInput() {
        return !(!validateName() | !validateTime() | !validateIteration());
    }

    private boolean validateName() {
        String name = text_input_name.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            text_input_name.setError(getString(R.string.gdf_error_empty));
            return false;
        } else {
            text_input_name.setError(null);
            return true;
        }
    }

    private boolean validateTime() {
        String time = text_input_time.getEditText().getText().toString().trim();
        if (time.isEmpty()) {
            text_input_time.setError(getString(R.string.gdf_error_empty));
            return false;
        } else {
            text_input_time.setError(null);
            return true;
        }
    }

    private boolean validateIteration() {
        String iteration = text_input_iteration.getEditText().getText().toString().trim();
        if (iteration.isEmpty()) {
            text_input_iteration.setError(getString(R.string.gdf_error_empty));
            return false;
        } else {
            text_input_iteration.setError(null);
            return true;
        }
    }

    private void fillFields() {
        if (_goal != null) {
            if (text_input_name != null) {
                text_input_name.getEditText().setText(_goal.getName());
            }
            if (text_input_time != null) {
                text_input_time.getEditText().setText(_goal.getTime().getTimeInHoursString());
            }
            if (text_input_iteration != null) {
                text_input_iteration.getEditText().setText(_goal.getIteration().getTimeInHoursString());
            }
        }
    }

    @Override
    public void onIconChanged(int position) {
        TypedArray icons = getContext().getResources().obtainTypedArray(R.array.goal_icons);
        iv_goalIcon.setImageDrawable(icons.getDrawable(position));
        imagePosition = position;
    }
}