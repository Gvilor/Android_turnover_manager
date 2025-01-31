package com.liberty.turnovermanagement.orders.create_update_details;

import android.app.Application;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.liberty.turnovermanagement.AppDatabase;
import com.liberty.turnovermanagement.base.details.BaseDetailsViewModel;
import com.liberty.turnovermanagement.customers.data.Customer;
import com.liberty.turnovermanagement.customers.data.CustomerDao;
import com.liberty.turnovermanagement.customers.data.CustomerHistory;
import com.liberty.turnovermanagement.orders.data.Order;
import com.liberty.turnovermanagement.orders.data.OrderDao;
import com.liberty.turnovermanagement.products.data.Product;
import com.liberty.turnovermanagement.products.data.ProductDao;
import com.liberty.turnovermanagement.products.data.ProductHistory;

import java.util.List;

public class OrderEditViewModel extends BaseDetailsViewModel<Order, Void> {
    private final ProductDao productDao;
    private final CustomerDao customerDao;
    private final OrderDao orderDao;


    private final LiveData<List<Product>> products;

    // I can setup products versions only after I get selected product AND product versions for the product
    private final MutableLiveData<Product> selectedProduct = new MutableLiveData<>();
    private final MutableLiveData<List<ProductHistory>> productVersions = new MutableLiveData<>();
    private final MediatorLiveData<Pair<Product, List<ProductHistory>>> selectedProductAndVersions = new MediatorLiveData<>();

    private final LiveData<List<Customer>> customers;
    private final MutableLiveData<List<CustomerHistory>> customerVersions = new MutableLiveData<>();

    public OrderEditViewModel(Application application) {
        super(application);
        productDao = db.productDao();
        customerDao = db.customerDao();
        orderDao = db.orderDao();
        products = productDao.getAll();
        customers = customerDao.getAll();

        selectedProductAndVersions.addSource(selectedProduct, product -> {
            Pair<Product, List<ProductHistory>> currentValue = selectedProductAndVersions.getValue();
            selectedProductAndVersions.setValue(new Pair<>(product, currentValue != null ? currentValue.second : null));
        });

        selectedProductAndVersions.addSource(productVersions, productHistoryList -> {
            Pair<Product, List<ProductHistory>> currentValue = selectedProductAndVersions.getValue();
            selectedProductAndVersions.setValue(new Pair<>(currentValue != null ? currentValue.first : null, productHistoryList));
        });
    }

    public LiveData<Pair<Product, List<ProductHistory>>> getSelectedProductAndVersions() {
        return selectedProductAndVersions;
    }

    @Override
    public void loadItem(long itemId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Order order = orderDao.getOrderById(itemId);
            Product product = productDao.getProductById(order.getProductId());
            selectedItem.postValue(order);
            selectedProduct.postValue(product);
        });
    }

    public MutableLiveData<Product> getSelectedProduct() {
        return selectedProduct;
    }


    @Override
    public void updateItem(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.update(order);
            selectedItem.postValue(order);
        });
    }

    @Override
    public void softDelete(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.delete(order);
        });
    }


    @Override
    public void addNewItem(Order order) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            long productVersion = productDao.getProductVersion(order.getProductId());
            long customerVersion = customerDao.getCustomerVersion(order.getCustomerId());

            order.setProductVersion(productVersion);
            order.setCustomerVersion(customerVersion);

            long id = orderDao.insert(order);
            order.setId(id);
            selectedItem.postValue(order);
        });
    }


    public void loadProductVersions(long productId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<ProductHistory> versions = productDao.getProductHistory(productId);
            productVersions.postValue(versions);
        });
    }

    public void loadCustomerVersions(long customerId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<CustomerHistory> versions = customerDao.getCustomerHistory(customerId);
            customerVersions.postValue(versions);
        });
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public LiveData<List<ProductHistory>> getProductVersions() {
        return productVersions;
    }

    public LiveData<List<Customer>> getCustomers() {
        return customers;
    }

    public LiveData<List<CustomerHistory>> getCustomerVersions() {
        return customerVersions;
    }
}
