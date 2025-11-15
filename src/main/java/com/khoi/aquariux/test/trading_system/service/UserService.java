package com.khoi.aquariux.test.trading_system.service;

import com.khoi.aquariux.test.trading_system.infra.repository.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User findUserByUuid(UUID userUuid);
}
