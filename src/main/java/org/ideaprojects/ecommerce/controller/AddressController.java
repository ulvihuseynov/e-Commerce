package org.ideaprojects.ecommerce.controller;

import jakarta.validation.Valid;
import org.ideaprojects.ecommerce.model.User;
import org.ideaprojects.ecommerce.payload.AddressDTO;
import org.ideaprojects.ecommerce.service.AddressService;
import org.ideaprojects.ecommerce.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;
    private final AuthUtil authUtil;

    public AddressController(AddressService addressService, AuthUtil authUtil) {
        this.addressService = addressService;
        this.authUtil = authUtil;
    }

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO){

        User user = authUtil.loggedInUser();

        AddressDTO savedAddressDTO=addressService.createAddress(addressDTO,user);

        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAddresses(){

        List<AddressDTO>addressDTOS=addressService.getAddresses();

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId){

       AddressDTO addressDTOS=addressService.getAddressById(addressId);

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(  ){

        User user = authUtil.loggedInUser();

        List<AddressDTO> addressDTOS=addressService.getUserAddresses(user);

        return new ResponseEntity<>(addressDTOS,HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressById(@PathVariable Long addressId,
                                                    @Valid @RequestBody AddressDTO addressDTO){


        AddressDTO savedAddressDTO=addressService.updateAddressById(addressId,addressDTO);

        return new ResponseEntity<>(savedAddressDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId){

        String status=addressService.deleteAddress(addressId);

        return new ResponseEntity<>(status,HttpStatus.OK);
    }

}
