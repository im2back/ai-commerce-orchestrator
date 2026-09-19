package io.github.froideexplica.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import io.github.froideexplica.model.Customer;
import io.github.froideexplica.model.Status;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CustomerRepository implements PanacheRepositoryBase<Customer, Long> {

    public List<Customer> findByEmailOrDocumentOrPhone(String email, String document, String phone) {
        return list(
                "email = ?1 or document = ?2 or phone = ?3",
                email,
                document,
                phone
        );
    }

    public Optional<Customer> findByDocument(String document) {
        return find(
                "select distinct c from Customer c " +
                        "left join fetch c.purchaseRecord " +
                        "where c.document = ?1",
                document
        ).firstResultOptional();
    }

    public Optional<Customer> findByEmail(String email) {
        return find("email", email).firstResultOptional();
    }

    public Optional<Customer> findByPhone(String phone) {
        return find("phone", phone).firstResultOptional();
    }

    public Optional<Customer> findByPurchase(Long purchaseId) {
        return find(
                "select c from Customer c join c.purchaseRecord p where p.id = ?1",
                purchaseId
        ).firstResultOptional();
    }

    public Double totalValueForLastMonth() {
        BigDecimal result = getEntityManager()
                .createQuery(
                        "select sum(pr.productprice * pr.quantity) " +
                                "from Customer c join c.purchaseRecord pr " +
                                "where " +
                                "(year(pr.purchaseDate) = year(current_date) " +
                                "and month(pr.purchaseDate) = month(current_date) - 1) " +
                                "or " +
                                "(year(pr.purchaseDate) = year(current_date) - 1 " +
                                "and month(pr.purchaseDate) = 12 " +
                                "and month(current_date) = 1)",
                        BigDecimal.class
                )
                .getSingleResult();

        return toDouble(result);
    }

    public Double partialValueOfTheCurrentMonth() {
        BigDecimal result = getEntityManager()
                .createQuery(
                        "select sum(pr.productprice * pr.quantity) " +
                                "from Customer c join c.purchaseRecord pr " +
                                "where pr.purchaseDate >= function('DATE_FORMAT', current_date, '%Y-%m-01')",
                        BigDecimal.class
                )
                .getSingleResult();

        return toDouble(result);
    }

    public Double partialVAlueForCurrentDay() {
        BigDecimal result = getEntityManager()
                .createQuery(
                        "select sum(pr.productprice * pr.quantity) " +
                                "from Customer c join c.purchaseRecord pr " +
                                "where cast(pr.purchaseDate as date) = current_date",
                        BigDecimal.class
                )
                .getSingleResult();

        return toDouble(result);
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> findTotalValueForLast7DaysExcludingToday() {
        return getEntityManager()
                .createNativeQuery(
                        "SELECT DATE(purchase_date) AS purchaseDate, " +
                                "SUM(product_price * product_quantity) AS totalValue " +
                                "FROM tb_purchase " +
                                "WHERE DATE(purchase_date) BETWEEN CURDATE() - INTERVAL 7 DAY " +
                                "AND CURDATE() - INTERVAL 1 DAY " +
                                "GROUP BY DATE(purchase_date) " +
                                "ORDER BY purchaseDate DESC"
                )
                .getResultList();
    }

    public Double totalOutstandingAmount() {
        BigDecimal result = getEntityManager()
                .createQuery(
                        "select sum(pr.productprice * pr.quantity) " +
                                "from PurchaseRecord pr " +
                                "where pr.status = :status",
                        BigDecimal.class
                )
                .setParameter("status", Status.EM_ABERTO)
                .getSingleResult();

        return toDouble(result);
    }

    @Transactional
    public void updateStatusByCustomerDocumentNative(
            String newStatus,
            String document,
            String currentStatus
    ) {
        getEntityManager()
                .createNativeQuery(
                        "UPDATE tb_purchase p " +
                                "SET p.payment_status = :newStatus " +
                                "WHERE p.customer_id = (" +
                                "SELECT c.id FROM tb_customer c WHERE c.document = :document" +
                                ") " +
                                "AND p.payment_status = :currentStatus"
                )
                .setParameter("newStatus", newStatus)
                .setParameter("document", document)
                .setParameter("currentStatus", currentStatus)
                .executeUpdate();
    }

    @Transactional
    public void undoPurchase(Long idPurchase) {
        getEntityManager()
                .createNativeQuery("DELETE FROM tb_purchase WHERE id = :idPurchase")
                .setParameter("idPurchase", idPurchase)
                .executeUpdate();
    }

    @Transactional
    public void individualPayment(String status, Long idPurchase) {
        getEntityManager()
                .createNativeQuery(
                        "UPDATE tb_purchase p " +
                                "SET p.payment_status = :paid " +
                                "WHERE p.id = :idPurchase"
                )
                .setParameter("paid", status)
                .setParameter("idPurchase", idPurchase)
                .executeUpdate();
    }

    public Long existsPurchaseRecordById(Long purchaseId) {
        Object result = getEntityManager()
                .createNativeQuery(
                        "SELECT EXISTS (" +
                                "SELECT 1 FROM tb_purchase WHERE id = :purchaseId" +
                                ")"
                )
                .setParameter("purchaseId", purchaseId)
                .getSingleResult();

        if (result instanceof Number number) {
            return number.longValue();
        }

        return 0L;
    }

    private Double toDouble(BigDecimal value) {
        return value == null ? 0.0 : value.doubleValue();
    }
}