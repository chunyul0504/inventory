package com.management.inventory.stock.domain.repository;

import com.management.inventory.stock.domain.dto.StockDto;
import com.management.inventory.stock.domain.entity.QStock;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Repository
public class StockRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    public StockRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }


    /**
     * 여러건 조회
     *
     * @param productName
     * @return List<StockDto>
     */
    public List<StockDto> findStockList(String productName) {
        QStock qStock = QStock.stock;

        return jpaQueryFactory.select(Projections.constructor(StockDto.class, qStock))
                .from(qStock)
                .where(
                        eqProductName(productName)
                )
                .fetch();
    }

    /**
     * 단건조회
     *
     * @param productName
     * @param optionName
     * @return StockDto
     */
    public StockDto findStock(String productName, String optionName) {
        QStock qStock = QStock.stock;

        return jpaQueryFactory.select(Projections.constructor(StockDto.class, qStock))
                .from(qStock)
                .where(
                        eqProductName(productName)
                        , eqOptionName(optionName)
                )
                .fetchOne();
    }

    /**
     * Stock 의 SEQ 찾기
     *
     * @param productName
     * @param optionName
     * @return
     */
    public Long findStockSeq(String productName, String optionName) {
        QStock qStock = QStock.stock;

        return jpaQueryFactory.select(qStock.seq)
                .from(qStock)
                .where(
                        eqProductName(productName)
                        , eqOptionName(optionName)
                )
                .fetchOne();
    }

    private BooleanExpression eqProductName(String productName) {
        if (ObjectUtils.isEmpty(productName)) {
            return null;
        }
        return QStock.stock.productName.eq(productName);
    }

    private BooleanExpression eqOptionName(String optionName) {
        if (ObjectUtils.isEmpty(optionName)) {
            return null;
        }
        return QStock.stock.optionName.eq(optionName);
    }

}
