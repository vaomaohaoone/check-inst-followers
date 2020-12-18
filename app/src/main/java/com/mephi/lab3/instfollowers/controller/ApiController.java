package com.mephi.lab3.instfollowers.controller;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.responses.users.UsersSearchResponse;
import com.mephi.lab3.instfollowers.properties.BatchProperties;
import com.mephi.lab3.instfollowers.service.FetchInstFollowersService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Контроллер запуска считывания информации о количестве подписчиков для аккаунта
 * */
@RestController
@RequiredArgsConstructor
public class ApiController {
    private final IGClient igClient;
    private final TaskExecutor taskExecutor;
    private final BatchProperties batchProperties;
    private final Set<String> registeredProfiles = new ConcurrentSkipListSet<>();

    /**
     * Регистрация аккаунта instagram для считывания информации о подписчиках
     * @param account - login пользователя (пример cristiano, arianagrande)
     * */
    @PostMapping("/run/{account}")
    public HttpStatus addProfile(@PathVariable String account) {
        UsersSearchResponse usersSearchResponse = igClient.actions().search().searchUser(account).join();
        if (usersSearchResponse.getNum_results() != 0)
        {
            if (!registeredProfiles.contains(account)) {
                taskExecutor.execute(new FetchInstFollowersService(igClient, account, registeredProfiles, batchProperties));
                registeredProfiles.add(account);
                return HttpStatus.CREATED;
            } else
                return HttpStatus.ALREADY_REPORTED;
        }
        else {
            return HttpStatus.NO_CONTENT;
        }
    }
}