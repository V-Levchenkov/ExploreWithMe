package ru.practicum.users.service;

import ru.practicum.users.dto.UserFullDto;
import ru.practicum.users.model.User;

import java.util.List;

public interface UserService {

    UserFullDto addUser(UserFullDto userDto);

    List<UserFullDto> getAllUsersList(Long[] ids, Integer from, Integer size);

    void removeUserById(Long userId);

    User getUserById(Long userId);

}
