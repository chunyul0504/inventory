package com.management.inventory.stock.domain.entity;

import com.management.inventory.exception.ApiException;
import com.management.inventory.response.StockResponseMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Entity
@ToString
@DynamicInsert
@DynamicUpdate
@Table(name = "STOCK")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private String productName;
    private String optionName;
    private Long quantity;

    public Stock(Long seq, String productName, String optionName, Long quantity) {
        this.seq = seq;
        this.productName = productName;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public void quantityManagement(Long quantity) {
        if (this.quantity + quantity < 0) {
            throw ApiException.by(StockResponseMessage.NO_STOCK);
        }
        this.quantity = this.quantity + quantity;
    }


}
