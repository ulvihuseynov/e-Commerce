package org.ideaprojects.ecommerce.repository;

import org.ideaprojects.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("select c from Cart c where c.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("select c from Cart c where c.user.email=?1 and  c.cartId=?2")
    Cart findCartByEmailAndCartId(String emailId, Long cartId);
}
