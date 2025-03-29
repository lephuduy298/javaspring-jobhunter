package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Subscriber;
import com.example.demo.service.SubscriberService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create a subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber sub) throws IdInvalidException {
        boolean existEmail = this.subscriberService.isExistEmail(sub.getEmail());
        if (existEmail) {
            throw new IdInvalidException("Email " + sub.getEmail() + " đã tồn tại");
        }
        Subscriber currentSub = this.subscriberService.handleCreateSub(sub);
        return ResponseEntity.created(null).body(currentSub);
    }

    @PutMapping("/subscribers")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber sub) throws IdInvalidException {
        Subscriber currentSub = this.subscriberService.findSubById(sub.getId());
        if (currentSub == null) {
            throw new IdInvalidException("Subscriber với id=" + sub.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.subscriberService.handleUpdateSub(currentSub, sub));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() {

        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findSubByEmail(email));
    }
}
