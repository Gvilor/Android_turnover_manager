package com.liberty.turnovermanagement.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.liberty.turnovermanagement.AppDatabase;

import java.util.List;

public abstract class BaseListViewModel<T> extends AndroidViewModel {
    protected final AppDatabase db;
    protected LiveData<List<T>> items;
    protected SharedPreferences sharedPreferences;

    public BaseListViewModel(Application application) {
        super(application);
        db = AppDatabase.getDatabase(application);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            (sharedPreferences, key) -> {
                if ("isArchivedVisible".equals(key)) {
                    updateItemList();
                }
            };

    protected abstract void updateItemList();

    public LiveData<List<T>> getItems() {
        return items;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }
}
