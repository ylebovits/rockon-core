package com.cis.rockon.controller;

import com.cis.rockon.model.User;
import com.cis.rockon.repository.UserRepository;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-queue")
public class UserQueueController {


    private final Logger logger = LoggerFactory.getLogger(UserQueueController.class);

    private final UserRepository repository;

    public UserQueueController(UserRepository repository) {
        this.repository = repository;
    }


    @GetMapping("/{id}")
    @ApiOperation(value = "getUsersSwipeQueue",
            notes = "Returns up to 25 users that match the requesting user's preferences")
    public ResponseEntity<List<User>> getUsersSwipeQueue(@PathVariable Long id)  {

        if (!repository.existsById(id))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        User user = repository.getById(id);

        List<User> users = repository.findAll();

        List<User> filteredUsers = users
                .stream()
                .unordered()
                .filter(otherUser ->
                        !user.equals(otherUser) && user.preferenceMatch(otherUser) &&
                        !user.getSwipes().contains(otherUser))
                .limit(25)
                .collect(Collectors.toList());

        return ResponseEntity.ok(filteredUsers);
    }

    /* returns 204 if swipe worked but no match, 200 if swipe worked and match */
    @GetMapping("/swipe/{id}")
    public ResponseEntity<Void> swipeOnUser(@PathVariable Long id, @RequestParam("user") Long otherUser) {

        if ( !(repository.existsById(id) && repository.existsById(otherUser)) )
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        /* resolve the persistence issues here!! the sets are not persisting
         * properly */
        User user = repository.getById(id);
        User other = repository.getById(otherUser);

        user.getSwipes().add(other);
        user = repository.save(user);

        if (user.getSwipes().contains(other) && other.getSwipes().contains(user)) {
            user.getConnections().add(other);
            other.getConnections().add(user);

            repository.save(user);
            repository.save(other);

            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
