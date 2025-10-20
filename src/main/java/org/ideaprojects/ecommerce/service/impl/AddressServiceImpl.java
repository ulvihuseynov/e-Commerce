package org.ideaprojects.ecommerce.service.impl;

import org.ideaprojects.ecommerce.exceptions.ResourceNotFoundException;
import org.ideaprojects.ecommerce.model.Address;
import org.ideaprojects.ecommerce.model.User;
import org.ideaprojects.ecommerce.payload.AddressDTO;
import org.ideaprojects.ecommerce.repository.AddressRepository;
import org.ideaprojects.ecommerce.repository.UserRepository;
import org.ideaprojects.ecommerce.service.AddressService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository, ModelMapper modelMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {

        Address address = modelMapper.map(addressDTO, Address.class);

        List<Address> addresses = user.getAddresses();

        addresses.add(address);

        user.setAddresses(addresses);

        address.setUser(user);

        Address saveAddress = addressRepository.save(address);
        return modelMapper.map(saveAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddresses() {

        List<Address> addresses = addressRepository.findAll();

        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

    }

    @Override
    public AddressDTO getAddressById(Long addressId) {

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        return modelMapper.map(address, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {

        List<Address> addresses = user.getAddresses();

        return addresses.stream().map(address -> modelMapper.map(address, AddressDTO.class)).toList();

    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {

        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        Address address = modelMapper.map(addressDTO, Address.class);

        addressFromDB.setCity(address.getCity());
        addressFromDB.setState(address.getState());
        addressFromDB.setStreet(address.getStreet());
        addressFromDB.setPinCode(address.getPinCode());
        addressFromDB.setBuildingName(address.getBuildingName());
        addressFromDB.setCountry(address.getCountry());
        addressFromDB.setUser(address.getUser());

        Address savedUpdatedAddress = addressRepository.save(addressFromDB);

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(addressUser -> addressUser.getAddressId().equals(addressId));
        user.getAddresses().add(savedUpdatedAddress);

        userRepository.save(user);
        return modelMapper.map(savedUpdatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {

        Address addressFromDB = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        User user = addressFromDB.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(addressFromDB);
        return "Address deleted successfully with addressId "+ addressId;
    }


}
