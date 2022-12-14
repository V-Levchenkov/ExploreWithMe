package ru.practicum.users.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.dto.UserFullDto;
import ru.practicum.users.dto.UserMapper;
import ru.practicum.users.model.User;
import ru.practicum.users.storage.UserRepository;
import ru.practicum.utilits.PageableRequest;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserFullDto addUser(UserFullDto userDto) {
        User user = userRepository.save(userMapper.fromDtoToUser(userDto));
        log.info("Добавлен пользователь {}", user);
        return userMapper.toUserFullDtoList(user);
    }

    @Override
    public List<UserFullDto> getAllUsersList(Long[] ids, Integer from, Integer size) {
        List<User> users;
        users = (ids == null) ? userRepository.findAll(getPageable(from, size)).getContent()
                : userRepository.findAllById(Arrays.asList(ids));
        log.info("Получен список пользователей {}", users);
        return userMapper.toUserFullDtoList(users);
    }

    @Override
    public void removeUserById(Long userId) {
        userValidation(userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удален", userId);
    }

    @Override
    public User getUserById(Long userId) {
        return userValidation(userId);
    }

    private User userValidation(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
         new NotFoundException("Пользователь с id: {} не найден", userId));
    }

    private Pageable getPageable(Integer from, Integer size) {
        return new PageableRequest(from, size, Sort.unsorted());
    }
}
