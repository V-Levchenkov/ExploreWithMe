package ru.practicum.users.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.users.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
