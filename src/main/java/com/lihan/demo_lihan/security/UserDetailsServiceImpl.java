package com.lihan.demo_lihan.security;

import com.lihan.demo_lihan.entity.User;
import com.lihan.demo_lihan.repository.UserRepository;
import com.lihan.demo_lihan.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户未找到：" + username));

        // 登录成功，清除失败记录
        loginAttemptService.loginSucceeded(username);

        return new UserPrincipal(user);
    }
}
