package com.dergachev.blog.repository;

import com.dergachev.blog.entity.article.Article;
import com.dergachev.blog.entity.article.ArticlePage;
import com.dergachev.blog.entity.article.ArticleSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ArticleCriteriaRepository {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    @Autowired
    public ArticleCriteriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Article> findAllWithFilters(ArticlePage articlePage,
                                            ArticleSearchCriteria articleSearchCriteria) {

        CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
        Root<Article> articleRoot = criteriaQuery.from(Article.class);
        Predicate predicate = getPredicate(articleSearchCriteria, articleRoot);
        criteriaQuery.where(predicate);
        setOrder(articlePage, criteriaQuery, articleRoot);

        TypedQuery<Article> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(articlePage.getSkip() * articlePage.getLimit());
        typedQuery.setMaxResults(articlePage.getLimit());

        Pageable pageable = getPageable(articlePage);

        long articleCount = getArticlesCount(predicate);

        return new PageImpl<Article>(typedQuery.getResultList(), pageable, articleCount);
    }

    private Predicate getPredicate(ArticleSearchCriteria articleSearchCriteria,
                                   Root<Article> articleRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(articleSearchCriteria.getTitle())) {
            predicates.add(
                    criteriaBuilder.like(articleRoot.get("title"),
                            "%" + articleSearchCriteria.getTitle() + "%s")
            );
        }
        if (Objects.nonNull(articleSearchCriteria.getAuthorId())) {
            predicates.add(
                    criteriaBuilder.like(articleRoot.get("authorId"),
                            "%" + articleSearchCriteria.getAuthorId() + "%s")
            );
        }
        if (Objects.nonNull(articleSearchCriteria.getTags())) {
            predicates.add(
                    criteriaBuilder.like(articleRoot.get("tags"),
                            "%" + articleSearchCriteria.getTags() + "%s")
            );
        }
        return criteriaBuilder.and(predicates.toArray(predicates.toArray(new Predicate[0])));
    }

    private void setOrder(ArticlePage articlePage, CriteriaQuery<Article> criteriaQuery,
                          Root<Article> articleRoot) {

        if (articlePage.getSortDirection().equals(Sort.Direction.ASC)) {
            criteriaQuery.orderBy(criteriaBuilder.asc(articleRoot.get(articlePage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(articleRoot.get(articlePage.getSortBy())));
        }
    }

    private Pageable getPageable(ArticlePage articlePage) {
        Sort sort = Sort.by(articlePage.getSortDirection(), articlePage.getSortBy());
        return PageRequest.of(articlePage.getSkip(), articlePage.getLimit(), sort);
    }

    private long getArticlesCount(Predicate predicate) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Article> countRoot = countQuery.from(Article.class);
        countQuery.select(criteriaBuilder.count(countRoot)).where(predicate);
        return entityManager.createQuery(countQuery).getSingleResult();
    }
}