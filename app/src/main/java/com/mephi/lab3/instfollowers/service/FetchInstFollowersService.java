package com.mephi.lab3.instfollowers.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.models.user.User;
import com.mephi.lab3.instfollowers.properties.BatchProperties;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

import java.util.Set;

/**
 * Task считывания количества подписчиков пользователя instagram и запись данных в ElasticSearch
 */
@RequiredArgsConstructor
public class FetchInstFollowersService implements Runnable {

    private final IGClient igClient;
    private final String accountName;
    private final Set<String> registeredProfiles;
    private final BatchProperties batchProperties;

    /**
     * Метод каждые interval миллисекунд count раз считывает данные из instagram и посылает в ElasticSearch
     * */
    @Override
    public void run() {
        for (int i = 0; i < batchProperties.getCount(); i++) {
            Logger log = LogManager.getLogger("elasticsearch");
            User user = (User)igClient.actions().users().findByUsername(accountName).join().getUser();
            log.info(MarkerManager.getMarker(accountName), user.getFollower_count());
            try {
                Thread.sleep(batchProperties.getInterval());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        registeredProfiles.remove(accountName);
    }

}
