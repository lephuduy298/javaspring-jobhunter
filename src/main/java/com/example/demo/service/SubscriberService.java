package com.example.demo.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.domain.Job;
import com.example.demo.domain.Skill;
import com.example.demo.domain.Subscriber;
import com.example.demo.domain.dto.email.ResEmailJob;
import com.example.demo.repository.JobRepository;
import com.example.demo.repository.SkillRepository;
import com.example.demo.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;

    private final SkillService skillService;

    private final JobRepository jobRepository;

    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillService skillService,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillService = skillService;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
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

    private ResEmailJob convertToResEmailJob(Job job) {
        ResEmailJob res = new ResEmailJob();
        res.setName(job.getName());
        res.setSalary(job.getSalary());

        // ResEmailJob.CompanyEmail comEmail = new ResEmailJob.CompanyEmail();
        // comEmail.setName(job.getCompany().getName());
        // res.setCompany(comEmail);

        res.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
        List<Skill> skills = job.getSkills();
        List<ResEmailJob.SkillEmail> skillEmails = skills.stream().map(
                s -> new ResEmailJob.SkillEmail(s.getName())).collect(Collectors.toList());

        res.setSkills(skillEmails);
        return res;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skill> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);

                    List<ResEmailJob> resList = listJobs.stream().map(
                            job -> this.convertToResEmailJob(job))
                            .collect(Collectors.toList());

                    this.emailService.sendEmailFromTemplateSync(
                            sub.getEmail(),
                            "Cơ hội việc làm đang chờ đón bạn. Khám phá ngay",
                            "job",
                            sub.getName(),
                            resList);
                }
            }
        }
    }

    public Subscriber findSubByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    // @Scheduled(fixedDelay = 1000)
    // public void testCron() {
    // System.out.println(">>> Test Cron");
    // }

}
