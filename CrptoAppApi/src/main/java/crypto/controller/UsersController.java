package crypto.controller;

import crypto.dto.LoginParam;
import crypto.entity.User;
import crypto.service.CryptoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController extends ControllerBase {
    @Autowired
    CryptoService service;

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginParam param) {
        User user = service.login(param.getUsername(), param.getPassword());
        if (user == null) {
            return new ResponseEntity("Wrong username or password.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newUser = service.createAccount(user);
        if (newUser == null) {
            return new ResponseEntity("User already exist.", HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/user/update/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable int userId, @RequestBody LoginParam param) {
        return null;
    }

    @DeleteMapping("/user/delete/{userId}")
    public ResponseEntity<User> deleteUser(@PathVariable int userId) {
        return null;
    }

}
