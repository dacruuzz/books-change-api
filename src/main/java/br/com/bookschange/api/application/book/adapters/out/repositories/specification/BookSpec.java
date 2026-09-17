package br.com.bookschange.api.application.book.adapters.out.repositories.specification;

import br.com.bookschange.api.application.book.dtos.BookFilterDTO;
import br.com.bookschange.api.domain.models.Book;
import br.com.bookschange.api.domain.models.BookCategory;
import br.com.bookschange.api.domain.models.Category;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookSpec {

    public static Specification<Book> filter(UUID ownerUuid, BookFilterDTO filter) {
        return (root, query, cb) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            buildOwnerFilter(ownerUuid, root, cb, predicates);
            buildNameFilter(filter, root, cb, predicates);
            buildAuthorFilter(filter, root, cb, predicates);
            buildPublisherFilter(filter, root, cb, predicates);
            buildCategoryFilter(filter, root, cb, predicates);
            buildCurrentConditionFilter(filter, root, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void buildCurrentConditionFilter(BookFilterDTO filter, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.currentCondition() != null) {
            predicates.add(
                cb.equal(
                        root.get("currentCondition"),
                        filter.currentCondition()
                )
            );
        }
    }

    private static void buildCategoryFilter(BookFilterDTO filter, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.bookCategoriesUuids() != null && !filter.bookCategoriesUuids().isEmpty()) {
            Join<Book, BookCategory> bookCategory = root.join("bookCategories");
            Join<BookCategory, Category> category = bookCategory.join("category");

            predicates.add(
                category.get("uuid").in(filter.bookCategoriesUuids())
            );
        }
    }

    private static void buildPublisherFilter(BookFilterDTO filter, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.publisher() != null) {
            predicates.add(
                cb.like(
                        cb.lower(root.get("publisher")),
                        "%" + filter.publisher().toLowerCase() + "%"
                )
            );
        }
    }

    private static void buildAuthorFilter(BookFilterDTO filter, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.author() != null) {
            predicates.add(
                cb.like(
                    cb.lower(root.get("author")),
                    "%" + filter.author().toLowerCase() + "%"
                )
            );
        }
    }

    private static void buildNameFilter(BookFilterDTO filter, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (filter.name() != null) {
            predicates.add(
                cb.like(
                    cb.lower(root.get("name")),
                    "%" + filter.name().toLowerCase() + "%"
                )
            );
        }
    }

    private static void buildOwnerFilter(UUID ownerUuid, Root<Book> root, CriteriaBuilder cb, List<Predicate> predicates) {
        if (ownerUuid != null) {
            predicates.add(
                cb.equal(
                    root.get("owner").get("uuid"),
                    ownerUuid
                )
            );
        }
    }
}
