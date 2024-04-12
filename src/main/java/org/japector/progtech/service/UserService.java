package org.japector.progtech.service;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.japector.progtech.entity.ItemEntity;
import org.japector.progtech.entity.UserEntity;
import org.japector.progtech.exception.EmailAlreadyInUseException;
import org.japector.progtech.exception.UnknownUserException;
import org.japector.progtech.model.ItemDto;
import org.japector.progtech.model.LoginDto;
import org.japector.progtech.model.UserDto;
import org.japector.progtech.repository.ItemRepository;
import org.japector.progtech.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Service
public class UserService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, ItemRepository itemRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public UserEntity  registerUser(String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        String hashedPassword = passwordEncoder.encode(password);

        UserEntity newUser = new UserEntity();
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        return userRepository.save(newUser);

    }

    public UserEntity findUserByEmail(String email) {

        return userRepository.findByEmail(email);
    }

    public List<UserDto> findAllUsers() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public UserDto findUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UnknownUserException("User not found with id: " + id));
        return convertToDto(userEntity);
    }

    public List<ItemDto> findAllItemsForUser(UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        List<ItemEntity> items = itemRepository.findByUser(userEntity);

        return items.stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveItemsForUser(List<ItemDto> itemDtoList, UserDto userDto) {

        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        itemRepository.deleteAllByUser(userEntity);

        for (ItemDto item : itemDtoList) {
            ItemEntity itemEntity = ItemEntity.builder()
                    .user(userEntity)
                    .name(item.getName())
                    .price(item.getPrice())
                    .quantity(item.getQuantity())
                    .build();
            itemRepository.save(itemEntity);
        }
    }

    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        userEntity = userRepository.save(userEntity);

        return convertToDto(userEntity);
    }

    public void deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new UnknownUserException("User not found with id: " + id);
        }
    }


    public UserDto authenticateUser(LoginDto loginDto) {
        UserEntity user = findUserByEmail(loginDto.getEmail());
        if (user == null) {
            throw new UnknownUserException("User is not registered!");
        }
        if (passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return convertToDto(user);
        }
        return null;
    }

    public UserDto convertToDto(UserEntity userEntity) {
        return UserDto.builder()
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .email(userEntity.getEmail())
                .build();
    }

    public ItemDto convertToItemDto(ItemEntity itemEntity) {
        return ItemDto.builder()
                .name(itemEntity.getName())
                .price(itemEntity.getPrice())
                .quantity(itemEntity.getQuantity())
                .build();
    }

}
