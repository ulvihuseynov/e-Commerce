package org.ideaprojects.ecommerce.repository;

import org.ideaprojects.ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    @Query("select ci from CartItem ci where ci.cart.cartId=?1 and ci.product.productId=?2")
    CartItem findCartItemByCartIdAndProductId(Long cartId, Long productId);
}
