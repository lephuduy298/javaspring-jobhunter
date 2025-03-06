package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Company;
import com.example.demo.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        // TODO Auto-generated method stub
        return this.companyRepository.save(company);
    }
}
