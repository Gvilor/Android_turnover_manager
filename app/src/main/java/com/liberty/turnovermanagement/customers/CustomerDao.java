package com.liberty.turnovermanagement.customers;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CustomerDao {
    @Query("SELECT * FROM customers WHERE isDeleted = 0")
    LiveData<List<Customer>> getAllCustomers();

    @Insert
    void insert(Customer customer);

    @Update
    void update(Customer customer);

    @Query("UPDATE customers SET isDeleted = 1 WHERE id = :customerId")
    void softDelete(long customerId);
}
