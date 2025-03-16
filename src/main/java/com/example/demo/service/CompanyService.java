package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Company;
import com.example.demo.domain.dto.ResultPaginationDTO;
import com.example.demo.repository.CompanyRepository;

import jakarta.validation.Valid;

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

    public void handleDeleteCompany(long id) {
        // TODO Auto-generated method stub
        this.companyRepository.deleteById(id);
        ;

    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> spec, Pageable pageable) {
        // TODO Auto-generated method stub
        Page<Company> companyPage = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(companyPage.getNumber() + 1);
        mt.setPageSize(companyPage.getSize());
        mt.setPages(companyPage.getTotalPages());
        mt.setTotal(companyPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(companyPage.getContent());
        return rs;
    }

    public Company handleUpdateCompany(Company company) {
        // TODO Auto-generated method stub
        Optional<Company> newCompany = this.companyRepository.findById(company.getId());
        if (newCompany.isPresent()) {
            Company currentCompany = newCompany.get();
            currentCompany.setName(company.getName());
            currentCompany.setAddress(company.getAddress());
            currentCompany.setLogo(company.getLogo());
            currentCompany.setDescription(company.getDescription());
            return this.companyRepository.save(currentCompany);
        }
        return null;
    }
}
