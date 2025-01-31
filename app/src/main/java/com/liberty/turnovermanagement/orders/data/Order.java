package com.liberty.turnovermanagement.orders.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.liberty.turnovermanagement.DateTimeStringConverter;
import com.liberty.turnovermanagement.base.Constants;
import com.liberty.turnovermanagement.base.Identifiable;
import com.liberty.turnovermanagement.customers.data.Customer;
import com.liberty.turnovermanagement.products.data.Product;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(tableName = "orders",
        foreignKeys = {
                @ForeignKey(entity = Customer.class,
                        parentColumns = "id",
                        childColumns = "customerId"),
                @ForeignKey(entity = Product.class,
                        parentColumns = "id",
                        childColumns = "productId")
        },
        indices = {
                @Index("customerId"),
                @Index("productId")

        }
)
public class Order implements Serializable, Identifiable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int amount;
    public long customerId = Constants.UNINITIALIZED_INDICATOR;
    public long productId = Constants.UNINITIALIZED_INDICATOR;
    private long customerVersion;
    private long productVersion;
    @TypeConverters(DateTimeStringConverter.class)
    @NonNull
    private LocalDateTime createdAt = LocalDateTime.now();
    private String city;
    private String street;
    private String home;


    // Constructor with all fields
    public Order(long productId, int amount, long customerId, @NonNull LocalDateTime createdAt, String city, String street, String home) {
        this.productId = productId;
        this.amount = amount;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.city = city;
        this.street = street;
        this.home = home;
    }

    @Ignore
    public Order() {
    }

    public long getCustomerVersion() {
        return customerVersion;
    }

    public void setCustomerVersion(long customerVersion) {
        this.customerVersion = customerVersion;
    }

    public long getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(long productVersion) {
        this.productVersion = productVersion;
    }

    // Getters
    public long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    @NonNull
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHome() {
        return home;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getProductId() {
        return productId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public void setCreatedAt(@NonNull LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHome(String home) {
        this.home = home;
    }

    @NonNull
    @Override
    public String toString() {
        return "Order\n" +
                "id = " + id +
                "\namount = " + amount +
                "\ndatetime = " + createdAt +
                "\ncity = '" + city + '\'' +
                "\nstreet = '" + street + '\'' +
                "\nhome = '" + home + '\'';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return id == order.id;
    }

}

