package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.domain.Skill;
import com.example.demo.domain.Subscriber;
import com.example.demo.repository.SkillRepository;
import com.example.demo.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    private final SkillService skillService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillService skillService) {
        this.subscriberRepository = subscriberRepository;
        this.skillService = skillService;
    }

    public Subscriber handleCreateSub(Subscriber sub) {
        if (sub.getSkills() != null) {
            List<Long> listIds = sub.getSkills()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> skillsValid = this.skillService.getIdIn(listIds);

            sub.setSkills(skillsValid);
        }

        return this.subscriberRepository.save(sub);
    }

    public Subscriber handleUpdateSub(Subscriber subInDB, Subscriber sub) {
        if (sub.getSkills() != null) {
            List<Long> listIds = sub.getSkills()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Skill> skillsValid = this.skillService.getIdIn(listIds);

            subInDB.setSkills(skillsValid);
        }
        return this.subscriberRepository.save(subInDB);
    }

    public Subscriber findSubById(long id) {
        Optional<Subscriber> subOptional = this.subscriberRepository.findById(id);
        if (subOptional.isPresent()) {
            return subOptional.get();
        }
        return null;
    }

    public boolean isExistEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

}
