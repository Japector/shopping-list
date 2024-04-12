package org.japector.progtech.controller;



import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.japector.progtech.entity.UserEntity;
import org.japector.progtech.model.ItemDto;
import org.japector.progtech.model.LoginDto;
import org.japector.progtech.model.UserDto;
import org.japector.progtech.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class Controller {

    @Autowired
    private UserService userService;


    @PostMapping("/registration")
    public ResponseEntity<?> registration(@Valid @RequestBody UserDto userDto) {
        UserEntity userEntity = userService.registerUser(userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword());
        if (userEntity != null) {
            return ResponseEntity.ok().body("Successful registration!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot be registered");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody UserDto userDto, HttpSession session) {
        UserDto userDtoUpdated = userService.updateUser(userDto);
        if (userDtoUpdated != null) {
            session.setAttribute("user", userDtoUpdated);
            return ResponseEntity.ok().body("Successful update!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client cannot be found!");
        }
    }

    @PostMapping("/saveShoppingList")
    public ResponseEntity<?> saveShoppingList(@Valid @RequestBody List<ItemDto> itemDtoList, HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            userService.saveItemsForUser(itemDtoList, userDto);
            return ResponseEntity.ok().body("Successful update!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot update shopping list!");
        }
    }

    @GetMapping("/fetchShoppingList")
    public ResponseEntity<?> fetchShoppingList(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            List<ItemDto> itemDtoList = userService.findAllItemsForUser(userDto);
            return ResponseEntity.ok().body(itemDtoList);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Cannot get shopping list!");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?>  deleteUser(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        if (userDto.getEmail() != null) {
            UserEntity userEntity = userService.findUserByEmail(userDto.getEmail());
            System.out.println(userEntity.toString());
            session.invalidate();
            userService.deleteUserById(userEntity.getId());
            return ResponseEntity.ok().body("User deleted");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found in session!");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>>  getUsers() {
        List<UserDto> userDtoList = userService.findAllUsers();
        return ResponseEntity.ok(userDtoList);
    }


    @GetMapping("/getUser")
    public ResponseEntity<UserDto>  getAUser(HttpSession session) {
        UserDto userDto = (UserDto) session.getAttribute("user");
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDto loginDto, HttpSession session) {
        UserDto userDto = userService.authenticateUser(loginDto);
        if (userDto != null) {
            session.setAttribute("user", userDto);
            return ResponseEntity.ok().body("User authenticated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "<h2>Logged out successfully!</h2>";
    }

}
