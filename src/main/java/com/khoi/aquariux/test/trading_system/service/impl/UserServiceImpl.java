package com.khoi.aquariux.test.trading_system.service.impl;

import com.khoi.aquariux.test.trading_system.exception.ResourceNotFoundException;
import com.khoi.aquariux.test.trading_system.infra.repository.UserRepository;
import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;
import com.khoi.aquariux.test.trading_system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Override
    public User findUserByUuid(UUID userUuid) {
        log.info("handle find user for user uuid {}", userUuid);
        return userRepository.findByUserUuid(userUuid)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for UUID: %s", userUuid.toString()));
    }
}
