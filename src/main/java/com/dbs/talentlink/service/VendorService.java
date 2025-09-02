package com.dbs.talentlink.service;

import com.dbs.talentlink.entity.Vendor;
import com.dbs.talentlink.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {

    private final VendorRepository vendorRepository;

    /**
     * Finds all vendors from the database.
     * @return A list of all Vendor entities.
     */
    public List<Vendor> findAllVendors() {
        return vendorRepository.findAll();
    }
}