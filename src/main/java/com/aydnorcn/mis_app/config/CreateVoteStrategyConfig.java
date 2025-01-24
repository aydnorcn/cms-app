package com.aydnorcn.mis_app.config;

import com.aydnorcn.mis_app.strategy.CreateVoteStrategy;
import com.aydnorcn.mis_app.utils.PollType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CreateVoteStrategyConfig {

    private final List<CreateVoteStrategy> createVoteStrategies;

    @Bean
    public Map<PollType, CreateVoteStrategy> getCreateVoteStrategyMap() {
        Map<PollType, CreateVoteStrategy> map = new EnumMap<>(PollType.class);
        createVoteStrategies.forEach(strategy -> map.put(strategy.getPollType(), strategy));
        return map;
    }
}