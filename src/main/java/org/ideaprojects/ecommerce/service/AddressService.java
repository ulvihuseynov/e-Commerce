package org.ideaprojects.ecommerce.service;

import jakarta.validation.Valid;
import org.ideaprojects.ecommerce.model.User;
import org.ideaprojects.ecommerce.payload.AddressDTO;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(@Valid AddressDTO addressDTO, User user);

    List<AddressDTO> getAddresses();

    AddressDTO getAddressById(Long addressId);

    List<AddressDTO> getUserAddresses(User user);

    AddressDTO updateAddressById(Long addressId, @Valid AddressDTO addressDTO);

    String deleteAddress(Long addressId);
}
