package com.utr.match.entity;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerSpecification implements Specification<PlayerEntity> {

    private SearchCriteria criteria;

    private OrderByCriteria orderBy;

    public PlayerSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public PlayerSpecification(SearchCriteria criteria, OrderByCriteria orderBy) {
        this.criteria = criteria;
        this.orderBy = orderBy;
    }

    @Override
    public Predicate toPredicate(Root<PlayerEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        if (orderBy != null) {
            List<Order> orders = new ArrayList<Order>();
            for (OrderByCriteria.SortOrder order : orderBy.getOrders()) {
                Order orderOps = order.isAsc()? criteriaBuilder.asc(root.<String>get(order.getKey()))
                        :criteriaBuilder.desc(root.<String>get(order.getKey()));
                orders.add(orderOps);
            }
            query.orderBy(orders);
        }

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return criteriaBuilder.greaterThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return criteriaBuilder.lessThanOrEqualTo(
                    root.<String> get(criteria.getKey()), criteria.getValue().toString());
        }
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return criteriaBuilder.like(
                        root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        } else if (criteria.getOperation().equals("isNull")) {
            return   criteriaBuilder.isNull(root.get(criteria.getKey()));
        } else if (criteria.getOperation().equals("isNotNull")) {
            return   criteriaBuilder.isNotNull(root.get(criteria.getKey()));
        }


        return null;
    }
}
