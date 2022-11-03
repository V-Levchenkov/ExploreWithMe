package ru.practicum.users.dto;

import org.springframework.stereotype.Component;
import ru.practicum.users.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserFullDto toUserFullDtoList(User user) {
        return new UserFullDto(user.getId(), user.getName(), user.getEmail());
    }

    public User fromDtoToUser(UserFullDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public List<UserFullDto> toUserFullDtoList(List<User> users) {
        return users
                .stream()
                .map(this::toUserFullDtoList)
                .collect(Collectors.toList());
    }
}
