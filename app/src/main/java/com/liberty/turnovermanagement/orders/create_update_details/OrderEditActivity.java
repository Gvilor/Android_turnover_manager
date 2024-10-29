package com.liberty.turnovermanagement.orders.create_update_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.liberty.turnovermanagement.base.details.BaseDetailsActivity;
import com.liberty.turnovermanagement.customers.data.Customer;
import com.liberty.turnovermanagement.customers.data.CustomerHistory;
import com.liberty.turnovermanagement.databinding.ActivityEditOrderBinding;
import com.liberty.turnovermanagement.orders.data.Order;
import com.liberty.turnovermanagement.products.data.Product;
import com.liberty.turnovermanagement.products.data.ProductHistory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderEditActivity extends BaseDetailsActivity<Order, OrderEditViewModel, ActivityEditOrderBinding> {

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();

        setupSpinners();
        setupObservers();
    }


    private void setupObservers() {
        viewModel.getProducts().observe(this, this::updateProductSpinner);
        viewModel.getProductVersions().observe(this, this::updateProductVersionSpinner);
        viewModel.getCustomers().observe(this, this::updateCustomerSpinner);
        viewModel.getCustomerVersions().observe(this, this::updateCustomerVersionSpinner);
    }

    @Override
    protected void setupButtons() {
        binding.buttonSave.setOnClickListener(v -> saveOrUpdateItem());
    }

    @Override
    protected Class<OrderEditViewModel> getViewModelClass() {
        return OrderEditViewModel.class;
    }

    @Override
    protected ActivityEditOrderBinding inflateBinding(LayoutInflater inflater) {
        return ActivityEditOrderBinding.inflate(inflater);
    }

    @Override
    protected void updateUI(Order order) {
        if (order == null) {
            return;
        }

        binding.editTextCity.setText(order.getCity());
        binding.editTextStreet.setText(order.getStreet());
        binding.editTextHome.setText(order.getHome());
        binding.editTextAmount.setText(String.valueOf(order.getAmount()));

        // Set the date and time
        calendar.setTime(java.util.Date.from(order.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
        updateSelectedDateTime();

        // Update product spinner
        updateProductSpinner(order.getProductId());

        // Update customer spinner
        updateCustomerSpinner(order.getCustomerId());

        // Load product versions
        viewModel.loadProductVersions(order.getProductId());

        // Load customer versions
        viewModel.loadCustomerVersions(order.getCustomerId());
    }


    private void updateProductSpinner(long productId) {
        viewModel.getProducts().observe(this, products -> {
            if (products != null) {
                int selectedPosition = -1;
                for (int i = 0; i < products.size(); i++) {
                    if (products.get(i).getId() == productId) {
                        selectedPosition = i;
                        break;
                    }
                }
                if (selectedPosition != -1) {
                    binding.productSpinner.setSelection(selectedPosition);
                }
            }
        });
    }

    private void updateCustomerSpinner(long customerId) {
        viewModel.getCustomers().observe(this, customers -> {
            if (customers != null) {
                int selectedPosition = -1;
                for (int i = 0; i < customers.size(); i++) {
                    if (customers.get(i).getId() == customerId) {
                        selectedPosition = i;
                        break;
                    }
                }
                if (selectedPosition != -1) {
                    binding.customerSpinner.setSelection(selectedPosition);
                }
            }
        });
    }


    private void updateSelectedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String formattedDateTime = sdf.format(calendar.getTime());
        binding.tvSelectedDateTime.setText("Selected: " + formattedDateTime);
    }


    @Override
    protected Order getItemToSaveOrUpdate() {
        Order order = viewModel.getSelectedItem().getValue();
        if (order == null) {
            order = new Order();
        }

        // Validate city
        String city = binding.editTextCity.getText().toString().trim();
        if (city.isEmpty()) {
            binding.editTextCity.setError("City cannot be empty");
            return null;
        }
        order.setCity(city);

        // Validate street
        String street = binding.editTextStreet.getText().toString().trim();
        if (street.isEmpty()) {
            binding.editTextStreet.setError("Street cannot be empty");
            return null;
        }
        order.setStreet(street);

        // Validate home
        String home = binding.editTextHome.getText().toString().trim();
        if (home.isEmpty()) {
            binding.editTextHome.setError("Home cannot be empty");
            return null;
        }
        order.setHome(home);

        // Validate amount
        String amountStr = binding.editTextAmount.getText().toString().trim();
        if (amountStr.isEmpty()) {
            binding.editTextAmount.setError("Amount cannot be empty");
            return null;
        }
        try {
            int amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                binding.editTextAmount.setError("Amount must be a positive integer");
                return null;
            }
            order.setAmount(amount);
        } catch (NumberFormatException e) {
            binding.editTextAmount.setError("Invalid amount");
            return null;
        }

        // Validate date and time
        if (calendar.getTime().before(new Date())) {
            // You might want to show an error message here
            Toast.makeText(this, "Order date cannot be in the past", Toast.LENGTH_SHORT).show();
            return null;
        }
        order.setCreatedAt(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()));

        // Validate product selection
        Product selectedProduct = (Product) binding.productSpinner.getSelectedItem();
        if (selectedProduct == null) {
            Toast.makeText(this, "Please select a product", Toast.LENGTH_SHORT).show();
            return null;
        }
        order.setProductId(selectedProduct.getId());

        // Validate customer selection
        Customer selectedCustomer = (Customer) binding.customerSpinner.getSelectedItem();
        if (selectedCustomer == null) {
            Toast.makeText(this, "Please select a customer", Toast.LENGTH_SHORT).show();
            return null;
        }
        order.setCustomerId(selectedCustomer.getId());

        // Handle product version if available
        if (binding.productVersionSpinner.getVisibility() == View.VISIBLE) {
            ProductHistory selectedProductVersion = (ProductHistory) binding.productVersionSpinner.getSelectedItem();
            if (selectedProductVersion != null) {
                order.setProductVersion(selectedProductVersion.getVersion());
            }
        }

        // Handle customer version if available
        if (binding.customerVersionSpinner.getVisibility() == View.VISIBLE) {
            CustomerHistory selectedCustomerVersion = (CustomerHistory) binding.customerVersionSpinner.getSelectedItem();
            if (selectedCustomerVersion != null) {
                order.setCustomerVersion(selectedCustomerVersion.getVersion());
            }
        }

        return order;
    }


    private void setupSpinners() {
        binding.productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Product selectedProduct = (Product) parent.getItemAtPosition(position);
                viewModel.loadProductVersions(selectedProduct.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Customer selectedCustomer = (Customer) parent.getItemAtPosition(position);
                viewModel.loadCustomerVersions(selectedCustomer.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void updateProductSpinner(List<Product> products) {
//        ArrayAdapter<Product> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, products);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.productSpinner.setAdapter(adapter);

        ProductSpinnerAdapter adapter = new ProductSpinnerAdapter(this, products);
        binding.productSpinner.setAdapter(adapter);
    }

    private void updateProductVersionSpinner(List<ProductHistory> productVersions) {
        if (productVersions.isEmpty()) {
            binding.productVersionSpinner.setVisibility(View.GONE);
        } else {
            binding.productVersionSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<ProductHistory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productVersions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.productVersionSpinner.setAdapter(adapter);
        }
    }

    private void updateCustomerSpinner(List<Customer> customers) {
//        ArrayAdapter<Customer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customers);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        binding.customerSpinner.setAdapter(adapter);

        CustomerSpinnerAdapter adapter = new CustomerSpinnerAdapter(this, customers);
        binding.customerSpinner.setAdapter(adapter);
    }

    private void updateCustomerVersionSpinner(List<CustomerHistory> customerVersions) {
        if (customerVersions.isEmpty()) {
            binding.customerVersionSpinner.setVisibility(View.GONE);
        } else {
            binding.customerVersionSpinner.setVisibility(View.VISIBLE);
            ArrayAdapter<CustomerHistory> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customerVersions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.customerVersionSpinner.setAdapter(adapter);
        }
    }
}
