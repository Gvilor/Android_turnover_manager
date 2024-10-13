package com.liberty.turnovermanagement.products;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.liberty.turnovermanagement.R;

public class AddProductActivity extends AppCompatActivity {

    private EditText editTextName, editTextAmount, editTextPrice;
    private Button buttonSave;
    private Product existingProduct;
    private Button buttonDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextPrice = findViewById(R.id.editTextPrice);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);

        existingProduct = (Product) getIntent().getSerializableExtra("product");
        if (existingProduct != null) {
            // Pre-fill fields if editing an existing product
            editTextName.setText(existingProduct.getName());
            editTextAmount.setText(String.valueOf(existingProduct.getAmount()));
            editTextPrice.setText(String.valueOf(existingProduct.getPrice()));

            // Show delete button only for existing products
            buttonDelete.setVisibility(View.VISIBLE);
        }

        buttonSave.setOnClickListener(v -> saveProduct());
        buttonDelete.setOnClickListener(v -> deleteProduct());
    }

    private void deleteProduct() {
        if (existingProduct != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("product", existingProduct);
                        resultIntent.putExtra("delete", true);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    private void saveProduct() {
        String name = editTextName.getText().toString().trim();
        int amount = Integer.parseInt(editTextAmount.getText().toString());
        double price = Double.parseDouble(editTextPrice.getText().toString());

        Product product;
        if (existingProduct != null) {
            // Update existing product
            existingProduct.setName(name);
            existingProduct.setAmount(amount);
            existingProduct.setPrice(price);
            product = existingProduct;
        } else {
            // Create new product
            product = new Product(name, amount, price);
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra("product", product);
        resultIntent.putExtra("isNewProduct", existingProduct == null);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}

