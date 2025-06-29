package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.dto.ChangePassword;
import com.tuituidan.openhub.bean.dto.UserDto;
import com.tuituidan.openhub.bean.entity.Role;
import com.tuituidan.openhub.bean.entity.RoleUser;
import com.tuituidan.openhub.bean.entity.User;
import com.tuituidan.openhub.bean.entity.UserStar;
import com.tuituidan.openhub.bean.vo.UserVo;
import com.tuituidan.openhub.consts.Consts;
import com.tuituidan.openhub.repository.RoleUserRepository;
import com.tuituidan.openhub.repository.UserRepository;
import com.tuituidan.openhub.repository.UserStarRepository;
import com.tuituidan.openhub.util.BeanExtUtils;
import com.tuituidan.openhub.util.ListUtils;
import com.tuituidan.openhub.util.StringExtUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * UserService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/11/16
 */
@Service
public class UserService implements UserDetailsService, ApplicationRunner {

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleUserRepository roleUserRepository;

    @Resource
    private UserStarRepository userStarRepository;

    @Resource
    private CacheService cacheService;

    @Value("${spring.security.user.name}")
    private String defUsername;

    @Value("${spring.security.user.password}")
    private String defPassword;

    @Resource
    private EmailSettingService emailSettingService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        insertDefaultUser();
    }

    private void insertDefaultUser() {
        if (userRepository.findById(Consts.DEFAULT_ID).isPresent()) {
            return;
        }
        userRepository.save(new User().setId(Consts.DEFAULT_ID)
                .setUsername(defUsername)
                .setStatus(Consts.DEFAULT_ID)
                .setNickname("管理員")
                .setAvatar("/assets/images/header.png")
                .setPassword(new BCryptPasswordEncoder().encode(defPassword)));
    }

    /**
     * 分页查询用户
     *
     * @param keywords keywords
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @return Page
     */
    public Page<UserVo> selectPage(String keywords, Integer pageIndex, Integer pageSize) {
        Specification<User> search = (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (StringUtils.isNotBlank(keywords)) {
                predicate.getExpressions().add(builder.or(builder.like(root.get("username"), "%" + keywords + "%"),
                        builder.like(root.get("nickname"), "%" + keywords + "%")));
            }
            return predicate;
        };
        Page<User> users = userRepository.findAll(search,
                PageRequest.of(pageIndex, pageSize, Sort.by("updateTime").descending()));
        Map<String, List<Role>> userRolesMap =
                getUserRolesMap(users.stream().map(User::getId).collect(Collectors.toSet()));
        return users.map(user ->
                BeanExtUtils.convert(user, UserVo::new)
                        .setRoles(userRolesMap.get(user.getId()))
        );
    }

    private Map<String, List<Role>> getUserRolesMap(Collection<String> userIds) {
        List<RoleUser> roleUsers = roleUserRepository.findByUserIdIn(userIds);
        return roleUsers.stream().collect(Collectors.groupingBy(RoleUser::getUserId,
                Collectors.mapping(item -> cacheService.getRole(item.getRoleId()),
                        Collectors.toList())));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("帳號或密碼錯誤");
        }
        Assert.isTrue("1".equals(user.getStatus()), "帳號已被禁用");
        return user;
    }

    /**
     * 保存
     *
     * @param id id
     * @param userDto userDto
     */
    public void save(String id, UserDto userDto) {
        Assert.isTrue(!StringUtils.equalsIgnoreCase(userDto.getUsername(), "admin"), "預設管理員不可操作");
        User user;
        User exitUser = userRepository.findByUsername(userDto.getUsername());
        if (StringUtils.isBlank(id)) {
            Assert.isTrue(exitUser == null, "登入帳號已存在");
            user = BeanExtUtils.convert(userDto, User::new);
            user.setId(StringExtUtils.getUuid());
            user.setPassword(setPassword(userDto));
        } else {
            user = userRepository.findById(id).orElseThrow(NullPointerException::new);
            Assert.isTrue(exitUser == null || StringUtils.equals(exitUser.getId(), id), "登入帳號已存在");
            BeanExtUtils.copyNotNullProperties(userDto, user);
            roleUserRepository.deleteByUserId(user.getId());
        }
        roleUserRepository.saveAll(Arrays.stream(userDto.getRoleIds())
                .map(roleId -> new RoleUser().setId(StringExtUtils.getUuid()).setRoleId(roleId)
                        .setUserId(user.getId())).collect(Collectors.toList()));
        userRepository.save(user);
    }

    private String setPassword(UserDto userDto) {
        if (BooleanUtils.isTrue(userDto.getRandomPassword())) {
            Assert.hasText(userDto.getEmail(), "若要使用隨機密碼請填寫信箱");
            SimpleMailMessage message = new SimpleMailMessage();
            String password = RandomStringUtils.randomAlphanumeric(12);
            message.setText(StringExtUtils.format("您的初始密碼為：{}", password));
            message.setTo(userDto.getEmail());
            emailSettingService.send(message);
            return new BCryptPasswordEncoder().encode(password);
        }
        return new BCryptPasswordEncoder().encode(defPassword);
    }

    /**
     * 删除
     *
     * @param id id
     */
    public void delete(String[] id) {
        List<String> ids = Arrays.asList(id);
        Set<String> usernames = userRepository.findAllById(ids).stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        Assert.isTrue(!CollectionUtils.containsAny(usernames, "admin"), "預設管理員不可操作");
        userRepository.deleteAllById(Arrays.asList(id));
        roleUserRepository.deleteByUserIdIn(ids);
    }

    /**
     * changePassword
     *
     * @param userId userId
     * @param changePassword changePassword
     */
    public void changePassword(String userId, ChangePassword changePassword) {
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        Assert.notNull(user, "帳號獲取失敗");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Assert.isTrue(passwordEncoder.matches(changePassword.getOldPassword(),
                user.getPassword()), "原密碼不匹配");
        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * resetPassword
     *
     * @param ids ids
     */
    public void resetPassword(String[] ids) {
        List<User> users = userRepository.findAllById(Arrays.asList(ids));
        users.forEach(user -> user.setPassword(new BCryptPasswordEncoder().encode(defPassword)));
        userRepository.saveAll(users);
    }

    /**
     * changeStatus
     *
     * @param id id
     * @param status status
     */
    public void changeStatus(String id, String status) {
        User user = userRepository.findById(id).orElseThrow(NullPointerException::new).setStatus(status);
        userRepository.save(user);
    }

    /**
     * userStarCard
     *
     * @param userId userId
     * @param cardIds cardIds
     */
    public void userStarCard(String userId, String[] cardIds) {
        Set<String> existIds = userStarRepository.findByUserId(userId).stream()
                .map(UserStar::getCardId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(existIds)) {
            if (ArrayUtils.isEmpty(cardIds)) {
                return;
            }
            userStarRepository.saveAll(Arrays.stream(cardIds).map(cardId -> new UserStar().setUserId(userId)
                    .setCardId(cardId).setId(StringExtUtils.getUuid())).collect(Collectors.toList()));
            return;
        }
        Pair<Set<String>, Set<String>> savePair = ListUtils.compareSaveIds(cardIds, existIds);
        if (CollectionUtils.isNotEmpty(savePair.getLeft())) {
            userStarRepository.deleteByUserIdAndCardIdIn(userId, savePair.getLeft());
        }
        if (CollectionUtils.isNotEmpty(savePair.getRight())) {
            userStarRepository.saveAll(savePair.getRight().stream().map(cardId -> new UserStar().setUserId(userId)
                    .setCardId(cardId).setId(StringExtUtils.getUuid())).collect(Collectors.toList()));
        }
    }

}
