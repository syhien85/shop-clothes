/**
 * @Deprecated
 */
/*package root.repository;

import root.entity.Bill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class BillDAO {

    @PersistenceContext
    EntityManager entityManager;

    // chỉ sử dụng khi ko extends JpaRepository
    @SuppressWarnings("unchecked")
    public List<Bill> searchByDate(Date s) {
        String jpql = "SELECT b FROM Bill b WHERE b.createdAt >= :x";

        return entityManager.createQuery(jpql)
            .setParameter("x", s)
            .setMaxResults(10)
            .setFirstResult(0)
            .getResultList();
    }
}*/
