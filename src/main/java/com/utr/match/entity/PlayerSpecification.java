package com.utr.match.entity;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

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
            query.orderBy(orderBy.isAsc()? criteriaBuilder.asc(root.<String>get(orderBy.getKey()))
                    : criteriaBuilder.desc(root.<String>get(orderBy.getKey())));
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
