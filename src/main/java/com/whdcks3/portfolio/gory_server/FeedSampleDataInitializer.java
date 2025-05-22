package com.whdcks3.portfolio.gory_server;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.whdcks3.portfolio.gory_server.repositories.FeedRepository;
import com.whdcks3.portfolio.gory_server.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FeedSampleDataInitializer implements CommandLineRunner {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    private static final List<String> CATEGORIES = Arrays.asList("건강/운동", "맛집/카페", "독서/영화");
    private static final int FEED_COUNT_PER_USER = 40;
    private static final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // if (feedRepository.count() > 0) {
        // System.out.println("이미 Feed data 가 존재함! 추가할 필요 없음!");
        // return;
        // }

        // List<User> users = userRepository.findAllById(Arrays.asList(1L, 2L, 3L));

        // if (users.isEmpty()) {
        // System.err.println("사용자 데이터가 존재하지 않음! User 생성 ㄱㄱ");
        // return;
        // }

        // for (User user : users) {
        // for (int i = 1; i <= FEED_COUNT_PER_USER; i++) {
        // Feed feed = new Feed();
        // feed.setUser(user);
        // feed.setCategory(CATEGORIES.get(random.nextInt(3)));
        // feed.setContent("샘플 피드 내용 " + i + " (" + user.getNickname());
        // feed.setLikeCount(random.nextInt(100));
        // feed.setViewCount(random.nextInt(1000));
        // feedRepository.save(feed);
        // }
        // }

        // System.out.println("총 " + (FEED_COUNT_PER_USER * users.size()) + "개의 피드가
        // 생성됨!");
    }
}
