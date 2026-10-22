package com.placementassist.shared.util;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to construct Spring Data JPA Specifications with SQLite-safe queries.
 *
 * @param <T> Entity type
 */
public class SpecificationBuilder<T> {

    private final List<Specification<T>> specs = new ArrayList<>();

    public static <T> SpecificationBuilder<T> create() {
        return new SpecificationBuilder<>();
    }

    /**
     * SQLite-safe case-insensitive LIKE search across multiple string attributes.
     */
    public SpecificationBuilder<T> withSearch(String searchTerm, String... fieldNames) {
        if (searchTerm != null && !searchTerm.trim().isEmpty() && fieldNames.length > 0) {
            String pattern = "%" + searchTerm.trim().toLowerCase() + "%";
            specs.add((root, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();
                for (String field : fieldNames) {
                    Path<String> path = getPath(root, field);
                    predicates.add(cb.like(cb.lower(path), pattern));
                }
                return cb.or(predicates.toArray(new Predicate[0]));
            });
        }
        return this;
    }

    /**
     * Exact equality filter.
     */
    public SpecificationBuilder<T> withEqual(String fieldName, Object value) {
        if (value != null) {
            specs.add((root, query, cb) -> cb.equal(getPath(root, fieldName), value));
        }
        return this;
    }

    /**
     * Case-insensitive string equality filter.
     */
    public SpecificationBuilder<T> withEqualIgnoreCase(String fieldName, String value) {
        if (value != null && !value.isBlank()) {
            specs.add((root, query, cb) -> cb.equal(cb.lower(getPath(root, fieldName)), value.trim().toLowerCase()));
        }
        return this;
    }

    /**
     * Greater than or equal to filter for numbers (e.g. CGPA).
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> withGreaterThanOrEqualTo(String fieldName, Y value) {
        if (value != null) {
            specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(getPath(root, fieldName), value));
        }
        return this;
    }

    /**
     * Less than or equal to filter for numbers (e.g. backlogs).
     */
    public <Y extends Comparable<? super Y>> SpecificationBuilder<T> withLessThanOrEqualTo(String fieldName, Y value) {
        if (value != null) {
            specs.add((root, query, cb) -> cb.lessThanOrEqualTo(getPath(root, fieldName), value));
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private <Y> Path<Y> getPath(Path<?> root, String fieldName) {
        if (fieldName.contains(".")) {
            String[] parts = fieldName.split("\\.");
            Path<?> path = root;
            for (String part : parts) {
                path = path.get(part);
            }
            return (Path<Y>) path;
        }
        return (Path<Y>) root.get(fieldName);
    }

    public Specification<T> build() {
        if (specs.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }
        Specification<T> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = result.and(specs.get(i));
        }
        return result;
    }
}
