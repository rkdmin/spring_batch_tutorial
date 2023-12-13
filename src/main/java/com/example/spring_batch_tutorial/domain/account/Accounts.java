package com.example.spring_batch_tutorial.domain.account;

import com.example.spring_batch_tutorial.domain.orders.Orders;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String orderItem;
    private Integer price;
    private Date orderDate;
    private Date accountDate;

    @Builder
    public Accounts(int id, String orderItem, int price, Date orderDate, Date accountDate) {
        this.id = id;
        this.orderItem = orderItem;
        this.price = price;
        this.orderDate = orderDate;
        this.accountDate = accountDate;
    }

    public static Accounts migrationFromOrders(Orders orders) {// Orders -> Accounts
        return Accounts.builder()
                .id(orders.getId())
                .orderItem(orders.getOrderItem())
                .price(orders.getPrice())
                .orderDate(orders.getOrderDate())
                .accountDate(new Date())// 새로운 account date 생성
                .build();
    }

}
