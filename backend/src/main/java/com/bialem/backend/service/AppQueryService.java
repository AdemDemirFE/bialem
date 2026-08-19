package com.bialem.backend.service;

import com.bialem.backend.web.rest.vm.AppQueryRequest;
import com.bialem.backend.web.rest.vm.AppQueryRequest.AppFilter;
import com.bialem.backend.web.rest.vm.AppQueryRequest.AppQueryResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AppQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(AppQueryService.class);

    @PersistenceContext
    private EntityManager em;

    private final AppSupport support;
    private final TransactionTemplate transactions;

    public AppQueryService(AppSupport support, PlatformTransactionManager transactionManager) {
        this.support = support;
        this.transactions = new TransactionTemplate(transactionManager);
    }

    public AppQueryResponse execute(AppQueryRequest request) {
        return transactions.execute(status -> {
            try {
                Class<?> type = AppSupport.TABLES.get(request.getTable());
                if (type == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bilinmeyen tablo: " + request.getTable());
                }
                String action = request.getAction() == null ? "select" : request.getAction();
                return switch (action) {
                    case "insert" -> write(type, request, false);
                    case "update" -> write(type, request, true);
                    case "upsert" -> upsert(type, request);
                    case "delete" -> delete(type, request);
                    default -> select(type, request);
                };
            } catch (Exception ex) {
                status.setRollbackOnly();
                LOG.error("App query failed for table {}", request.getTable(), ex);
                return AppSupport.failure(ex);
            }
        });
    }

    private AppQueryResponse select(Class<?> type, AppQueryRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = cb.createQuery(Object.class);
        Root<?> root = cq.from(type);
        List<Predicate> predicates = predicates(cb, root, type, request.getFilters());
        cq.select(root).where(predicates.toArray(Predicate[]::new));
        if (request.getOrderColumn() != null) {
            Path<?> orderPath = path(root, type, request.getOrderColumn());
            cq.orderBy(Boolean.FALSE.equals(request.getOrderAsc()) ? cb.desc(orderPath) : cb.asc(orderPath));
        }
        TypedQuery<Object> query = em.createQuery(cq);
        if (request.getOffset() != null) {
            query.setFirstResult(request.getOffset());
        }
        if (request.getLimit() != null) {
            query.setMaxResults(request.getLimit());
        }
        List<Object> rows = Boolean.TRUE.equals(request.getHead()) ? List.of() : query.getResultList();
        Long count = null;
        if (Boolean.TRUE.equals(request.getCount()) || Boolean.TRUE.equals(request.getHead())) {
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(type);
            countQuery.select(cb.count(countRoot)).where(predicates(cb, countRoot, type, request.getFilters()).toArray(Predicate[]::new));
            count = em.createQuery(countQuery).getSingleResult();
        }
        List<Map<String, Object>> data = rows.stream().map(support::toMap).toList();
        if (Boolean.TRUE.equals(request.getSingle()) || Boolean.TRUE.equals(request.getHead()) && request.getLimit() == null) {
            return new AppQueryResponse(data.isEmpty() ? null : data.get(0), null, count);
        }
        return new AppQueryResponse(data, null, count);
    }

    @SuppressWarnings("unchecked")
    private AppQueryResponse write(Class<?> type, AppQueryRequest request, boolean update) throws Exception {
        List<Map<String, Object>> payloads = payloads(request.getPayload());
        List<Map<String, Object>> saved = new ArrayList<>();
        for (Map<String, Object> payload : payloads) {
            Object entity;
            if (update) {
                List<?> matches = loadMatches(type, request.getFilters());
                if (matches.isEmpty()) {
                    continue;
                }
                entity = matches.get(0);
            } else {
                entity = type.getDeclaredConstructor().newInstance();
            }
            support.applyPayload(entity, payload);
            Object merged = em.merge(entity);
            em.flush();
            saved.add(support.toMap(merged));
        }
        if (Boolean.TRUE.equals(request.getSingle()) || saved.size() == 1 && update) {
            return new AppQueryResponse(saved.isEmpty() ? null : saved.get(0), null, (long) saved.size());
        }
        return new AppQueryResponse(saved, null, (long) saved.size());
    }

    private AppQueryResponse upsert(Class<?> type, AppQueryRequest request) throws Exception {
        List<?> matches = loadMatches(type, request.getFilters());
        if (matches.isEmpty() && request.getOnConflict() != null) {
            matches = loadByConflict(type, request);
        }
        request.setAction(matches.isEmpty() ? "insert" : "update");
        if (!matches.isEmpty()) {
            List<AppFilter> filters = new ArrayList<>();
            AppFilter idFilter = new AppFilter();
            idFilter.setOp("eq");
            idFilter.setColumn("id");
            idFilter.setValue(support.parseLong(support.toMap(matches.get(0)).get("id")));
            filters.add(idFilter);
            request.setFilters(filters);
        }
        return write(type, request, !matches.isEmpty());
    }

    private AppQueryResponse delete(Class<?> type, AppQueryRequest request) {
        List<?> matches = loadMatches(type, request.getFilters());
        matches.forEach(em::remove);
        return new AppQueryResponse(List.of(), null, (long) matches.size());
    }

    private List<?> loadMatches(Class<?> type, List<AppFilter> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object> cq = cb.createQuery(Object.class);
        Root<?> root = cq.from(type);
        cq.select(root).where(predicates(cb, root, type, filters).toArray(Predicate[]::new));
        return em.createQuery(cq).getResultList();
    }

    @SuppressWarnings("unchecked")
    private List<?> loadByConflict(Class<?> type, AppQueryRequest request) {
        List<Map<String, Object>> payloads = payloads(request.getPayload());
        if (payloads.isEmpty() || request.getOnConflict() == null) {
            return List.of();
        }
        Map<String, Object> payload = payloads.get(0);
        List<AppFilter> filters = new ArrayList<>();
        for (String column : request.getOnConflict().split(",")) {
            AppFilter filter = new AppFilter();
            filter.setOp("eq");
            filter.setColumn(column.trim());
            filter.setValue(payload.get(column.trim()));
            filters.add(filter);
        }
        return loadMatches(type, filters);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> payloads(Object payload) {
        if (payload == null) {
            return List.of();
        }
        if (payload instanceof List<?> list) {
            return list.stream().map(item -> (Map<String, Object>) item).toList();
        }
        return List.of((Map<String, Object>) payload);
    }

    private List<Predicate> predicates(CriteriaBuilder cb, Root<?> root, Class<?> type, List<AppFilter> filters) {
        List<Predicate> predicates = new ArrayList<>();
        if (filters == null) {
            return predicates;
        }
        for (AppFilter filter : filters) {
            Path<?> path = path(root, type, filter.getColumn());
            Predicate predicate = predicate(cb, path, filter);
            if (filter.isNegate()) {
                predicate = cb.not(predicate);
            }
            predicates.add(predicate);
        }
        return predicates;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Predicate predicate(CriteriaBuilder cb, Path<?> path, AppFilter filter) {
        String op = filter.getOp() == null ? "eq" : filter.getOp();
        if ("in".equals(op)) {
            Collection<?> values = filter.getValue() instanceof Collection<?> collection ? collection : List.of(filter.getValue());
            return path.in(values.stream().map(item -> coerce(path.getJavaType(), item)).toList());
        }
        Object value = coerce(path.getJavaType(), filter.getValue());
        return switch (op) {
            case "neq" -> value == null ? path.isNotNull() : cb.notEqual(path, value);
            case "is" -> value == null ? path.isNull() : cb.equal(path, value);
            case "ilike", "like" -> cb.like(cb.lower(path.as(String.class)), String.valueOf(filter.getValue()).toLowerCase(Locale.ROOT));
            case "gte" -> cb.greaterThanOrEqualTo((Path) path, (Comparable) value);
            case "lte" -> cb.lessThanOrEqualTo((Path) path, (Comparable) value);
            case "gt" -> cb.greaterThan((Path) path, (Comparable) value);
            case "lt" -> cb.lessThan((Path) path, (Comparable) value);
            default -> value == null ? path.isNull() : cb.equal(path, value);
        };
    }

    private Path<?> path(Root<?> root, Class<?> type, String column) {
        AppSupport.PathRef ref = support.path(type, column);
        Path<?> path = root.get(ref.field());
        return ref.nested() == null ? path : path.get(ref.nested());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object coerce(Class<?> type, Object raw) {
        if (raw == null) {
            return null;
        }
        if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, String.valueOf(raw).trim().toUpperCase(Locale.ROOT).replace('-', '_'));
        }
        if ((type == Long.class || type == long.class) && !(raw instanceof Long)) {
            return support.parseLong(raw);
        }
        if (type == Instant.class && !(raw instanceof Instant)) {
            return Instant.parse(String.valueOf(raw));
        }
        return raw;
    }
}
