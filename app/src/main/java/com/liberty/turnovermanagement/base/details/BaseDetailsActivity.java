package com.liberty.turnovermanagement.base.details;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.liberty.turnovermanagement.base.Constants;


public abstract class BaseDetailsActivity<T, VM extends BaseDetailsViewModel<T, ?>, VB extends ViewBinding> extends AppCompatActivity {

    protected VM viewModel;
    protected long itemId = Constants.UNINITIALIZED_INDICATOR;
    protected VB binding;

    protected abstract VB inflateBinding(LayoutInflater inflater);

    protected abstract Class<VM> getViewModelClass();

    protected abstract void updateUI(T item);

    protected abstract T getItemToSaveOrUpdate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(getViewModelClass());

        itemId = getIntent().getLongExtra(Constants.ITEM_ID, Constants.UNINITIALIZED_INDICATOR);

        if (itemId != -1) {
            viewModel.loadItem(itemId);
        }

        viewModel.getSelectedItem().observe(this, this::updateUI);

        setupButtons();
    }

    protected void saveOrUpdateItem() {
        T item = getItemToSaveOrUpdate();

        if (item == null) {
            return;
        }

        if (itemId == Constants.UNINITIALIZED_INDICATOR) {
            viewModel.addNewItem(item);
        } else {
            viewModel.updateItem(item);
        }

        finishActivity();
    }

    protected abstract void setupButtons();

    protected void finishActivity() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    protected void deleteItem() {
        T item = viewModel.getSelectedItem().getValue();
        if (item != null) {
            viewModel.softDelete(item);
            finishActivity();
        }
    }
}

