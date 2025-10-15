package org.ideaprojects.ecommerce.repository;

import org.ideaprojects.ecommerce.model.Category;
import org.ideaprojects.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Page<Product> findByCategoryOrderByPriceAsc(Category category, PageRequest pageRequest);

    Page<Product> findByProductNameLikeIgnoreCase(String keyword, PageRequest pageRequest);
}
