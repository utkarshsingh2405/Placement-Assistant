package com.placementassist.placement.modules.company;

import com.placementassist.shared.exception.NotFoundException;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Company> searchCompanies(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return companyRepository.findByNameContainingIgnoreCase(search.trim(), pageable);
        }
        return companyRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company", id));
    }

    @Transactional
    public Company createCompany(Company request) {
        return companyRepository.save(request);
    }

    @Transactional
    public Company updateCompany(Long id, Company request) {
        Company existing = getCompanyById(id);
        existing.setName(request.getName());
        existing.setWebsite(request.getWebsite());
        existing.setLogoUrl(request.getLogoUrl());
        existing.setIndustry(request.getIndustry());
        existing.setDescription(request.getDescription());
        return companyRepository.save(existing);
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new NotFoundException("Company", id);
        }
        companyRepository.deleteById(id);
    }
}
