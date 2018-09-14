package com.spring.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.spring.model.*;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<PlaceOrder, Long>{

	PlaceOrder findByOrderId(int orderId);

}
