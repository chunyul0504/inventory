package com.management.inventory.test;

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
@Table(name = "COUNT_TEST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CountTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
    private Long count;

    public CountTest(Long seq, Long count) {
        this.seq = seq;
        this.count = count;
    }

    public void quantityManagement(Long count){
        if (this.count + count < 0) {
            throw new RuntimeException();
        }
        this.count = this.count + count;
    }

}
