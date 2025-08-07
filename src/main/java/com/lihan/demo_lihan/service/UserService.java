package com.lihan.demo_lihan.service;

import com.lihan.demo_lihan.common.BusinessException;
import com.lihan.demo_lihan.common.Constants;
import com.lihan.demo_lihan.common.ResultCode;
import com.lihan.demo_lihan.common.Utils;
import com.lihan.demo_lihan.dto.RegisterRequest;
import com.lihan.demo_lihan.entity.Role;
import com.lihan.demo_lihan.entity.User;
import com.lihan.demo_lihan.jwt.JwtTokenUtil;
import com.lihan.demo_lihan.repository.RoleRepository;
import com.lihan.demo_lihan.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginAttemptService loginAttemptService;



    /**
     * 根据ID查找用户
     */
    public Optional<User> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return userRepository.findById(id);
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        if (Utils.isEmpty(username)) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username);
    }

    /**
     * 根据邮箱查找用户
     */
    public Optional<User> findByEmail(String email) {
        if (Utils.isEmpty(email)) {
            return Optional.empty();
        }
        return userRepository.findByEmail(email);
    }

    /**
     * 根据用户名或邮箱查找用户
     */
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        if (Utils.isEmpty(usernameOrEmail)) {
            return Optional.empty();
        }
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    /**
     * 分页查询用户
     */
    public Page<User> findByKeyword(String keyword, Pageable pageable) {
        return userRepository.findByKeyword(keyword, pageable);
    }

    /**
     * 获取所有用户
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 创建用户
     */
    @Transactional
    public User createUser(User user) {
        validateUserForCreation(user);

        // 加密密码
        if (Utils.isNotEmpty(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(passwordEncoder.encode(Constants.User.DEFAULT_PASSWORD));
        }

        // 设置默认昵称
        if (Utils.isEmpty(user.getNickname())) {
            user.setNickname(Utils.generateDefaultNickname(user.getUsername()));
        }

        // 设置默认角色
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByRoleCode("STUDENT")
                    .orElseThrow(() -> new BusinessException("默认学生角色不存在"));
            Set<Role> roles = new HashSet<>();
            roles.add(defaultRole);
            user.setRoles(roles);
        }

        // 设置默认状态
        if (user.getIsEnabled() == null) {
            user.setIsEnabled(true);
        }
        if (user.getIsLocked() == null) {
            user.setIsLocked(false);
        }

        User savedUser = userRepository.save(user);
        log.info("创建用户成功: username={}, id={}", savedUser.getUsername(), savedUser.getId());
        return savedUser;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, User userUpdateInfo) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 更新允许修改的字段
        if (Utils.isNotEmpty(userUpdateInfo.getNickname())) {
            existingUser.setNickname(userUpdateInfo.getNickname());
        }
        if (Utils.isNotEmpty(userUpdateInfo.getEmail())
                && !userUpdateInfo.getEmail().equals(existingUser.getEmail())) {
            validateEmail(userUpdateInfo.getEmail());
            existingUser.setEmail(userUpdateInfo.getEmail());
        }
        if (Utils.isNotEmpty(userUpdateInfo.getPhone())
                && !userUpdateInfo.getPhone().equals(existingUser.getPhone())) {
            validatePhone(userUpdateInfo.getPhone());
            existingUser.setPhone(userUpdateInfo.getPhone());
        }
        if (Utils.isNotEmpty(userUpdateInfo.getAvatar())) {
            existingUser.setAvatar(userUpdateInfo.getAvatar());
        }

        User savedUser = userRepository.save(existingUser);
        log.info("更新用户信息成功: id={}, username={}", id, savedUser.getUsername());
        return savedUser;
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 验证新密码格式
        validatePassword(newPassword);

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("用户修改密码成功: id={}, username={}", userId, user.getUsername());
    }

    /**
     * 启用/禁用用户
     */
    @Transactional
    public void toggleUserStatus(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        user.setIsEnabled(enabled);
        userRepository.save(user);

        log.info("用户状态更新成功: id={}, username={}, enabled={}",
                userId, user.getUsername(), enabled);
    }

    /**
     * 锁定/解锁用户
     */
    @Transactional
    public void toggleUserLock(Long userId, boolean locked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        user.setIsLocked(locked);
        userRepository.save(user);

        log.info("用户锁定状态更新成功: id={}, username={}, locked={}",
                userId, user.getUsername(), locked);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        userRepository.delete(user);
        log.info("删除用户成功: id={}, username={}", userId, user.getUsername());
    }

    /**
     * 验证用户创建信息
     */
    private void validateUserForCreation(User user) {
        if (user == null) {
            throw new BusinessException("用户信息不能为空");
        }

        validateUsername(user.getUsername());

        if (Utils.isNotEmpty(user.getEmail())) {
            validateEmail(user.getEmail());
        }

        if (Utils.isNotEmpty(user.getPhone())) {
            validatePhone(user.getPhone());
        }

        if (Utils.isNotEmpty(user.getPassword())) {
            validatePassword(user.getPassword());
        }
    }

    /**
     * 验证用户名
     */
    private void validateUsername(String username) {
        if (Utils.isEmpty(username)) {
            throw new BusinessException("用户名不能为空");
        }
        if (username.length() < Constants.User.USERNAME_MIN_LENGTH
                || username.length() > Constants.User.USERNAME_MAX_LENGTH) {
            throw new BusinessException(String.format("用户名长度必须在%d-%d个字符之间",
                    Constants.User.USERNAME_MIN_LENGTH, Constants.User.USERNAME_MAX_LENGTH));
        }
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException("用户名已存在");
        }
    }

    /**
     * 验证邮箱
     */
    private void validateEmail(String email) {
        if (!Utils.isValidEmail(email)) {
            throw new BusinessException("邮箱格式不正确");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("邮箱已被注册");
        }
    }

    /**
     * 验证手机号
     */
    private void validatePhone(String phone) {
        if (!Utils.isValidPhone(phone)) {
            throw new BusinessException("手机号格式不正确");
        }
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException("手机号已被注册");
        }
    }




    /**
     * 验证密码合法性及复杂度
     * @param password 待验证的密码字符串
     */
    private void validatePassword(String password) {
        if (Utils.isEmpty(password)) {
            throw new BusinessException("密码不能为空");
        }
        if (password.length() < Constants.User.PASSWORD_MIN_LENGTH
                || password.length() > Constants.User.PASSWORD_MAX_LENGTH) {
            throw new BusinessException(String.format("密码长度必须在%d-%d个字符之间",
                    Constants.User.PASSWORD_MIN_LENGTH, Constants.User.PASSWORD_MAX_LENGTH));
        }
        // 复杂度校验
        Utils.validatePasswordComplexity(password);
    }




    public User register(@Valid RegisterRequest request) {
        // 1. 验证用户名、邮箱、电话是否重复
        validateUsername(request.getUsername());

        if (Utils.isNotEmpty(request.getEmail())) {
            validateEmail(request.getEmail());
        }

        if (Utils.isNotEmpty(request.getPhone())) {
            validatePhone(request.getPhone());
        }

        // 2. 创建用户对象
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setNickname(Utils.generateDefaultNickname(request.getUsername()));
        user.setIsEnabled(true);
        user.setIsLocked(false);

        // **打印前端传过来的角色代码，确认是否正确**
        log.info("前端传过来的角色代码: {}", request.getRole().name());

        // 3. 设置角色
        Role selectedRole = roleRepository.findByRoleCode(request.getRole().name())
                .orElseThrow(() -> new BusinessException("角色不存在"));

        // **打印数据库查到的角色信息，确认角色是否匹配**
        log.info("数据库查询到的角色: id={}, roleCode={}, roleName={}",
                selectedRole.getId(),
                selectedRole.getRoleCode(),
                selectedRole.getRoleName());

        Set<Role> roles = new HashSet<>();
        roles.add(selectedRole);
        user.setRoles(roles);

        // 4. 保存用户
        User savedUser = userRepository.save(user);
        log.info("注册成功: username={}, id={}", savedUser.getUsername(), savedUser.getId());

        return savedUser;
    }


    public String login(String usernameOrEmail, String password) {
        // 先检查是否被锁定
        if (loginAttemptService.isBlocked(usernameOrEmail)) {
            throw new BusinessException("登录失败次数过多，账号暂时被锁定，请稍后再试");
        }

        try {
            // 1. 构建认证请求
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(usernameOrEmail, password);

            // 2. 提交认证请求（会调用 UserDetailsServiceImpl 中的 loadUserByUsername）
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 登录成功，清除失败计数
            loginAttemptService.loginSucceeded(usernameOrEmail);

        } catch (Exception e) {
            // 登录失败，增加失败次数
            loginAttemptService.loginFailed(usernameOrEmail);
            throw e; // 继续抛出异常
        }

        // 3. 获取用户信息
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));

        if (!user.getIsEnabled()) {
            throw new BusinessException("用户未启用");
        }

        if (user.getIsLocked()) {
            throw new BusinessException("用户已被锁定");
        }

        // 4. 生成并返回 JWT
        String username = user.getUsername();
        String roleCode = user.getRoles().stream().findFirst()
                .map(Role::getRoleCode).orElse("UNKNOWN");
        Long userId = user.getId();

        return jwtTokenUtil.generateToken(username, roleCode, userId);
    }


}