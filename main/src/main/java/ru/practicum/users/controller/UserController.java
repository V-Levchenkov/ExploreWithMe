package ru.practicum.users.controller;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.users.service.UserService;
import ru.practicum.users.dto.UserFullDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping
    public List<UserFullDto> get(@RequestParam(value = "ids", required = false) Long[] ids,
                                 @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                 @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return userService.getAllUsersList(ids, from, size);
    }

    @PostMapping
    public UserFullDto add(@Valid @RequestBody UserFullDto userDto) {
        return userService.addUser(userDto);
    }

    @DeleteMapping(path = "/{userId}")
    public void remove(@PathVariable Long userId) {
        userService.removeUserById(userId);
    }
}
