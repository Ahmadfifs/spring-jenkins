package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;


public class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserRepository userRepo;
    @Mock
    private CartRepository cartRepo;
    @Mock
    private BCryptPasswordEncoder encoder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController(userRepo, cartRepo, encoder);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("ahmad");
        user.setPassword("password");
        user.setCart(cart);
        when(userRepo.findByUsername("ahmad")).thenReturn(user);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepo.findByUsername("someone")).thenReturn(null);

    }

    @Test
    public void createUser() {
        when(encoder.encode("password")).thenReturn("Hash value");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("ahmad");
        r.setPassword("password");
        r.setConfirmPassword("password");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("ahmad", u.getUsername());
        assertEquals("Hash value", u.getPassword());

    }

    @Test
    public void createInvalidUser() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("short");
        r.setConfirmPassword("short");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void createUserWithInvalidPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("alongpassword");
        r.setConfirmPassword("aLongpassWord");
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findUserByName() {
        final ResponseEntity<User> response = userController.findByUserName("ahmad");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals("ahmad", u.getUsername());
    }

    @Test
    public void findUserByName404() {
        final ResponseEntity<User> response = userController.findByUserName("someone");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void findUserById() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());;
    }

//    @Test
//    public void find_user_by_id_doesnt_exist() {
//        final ResponseEntity<User> response = userController.findById(1L);
//        assertNotNull(response);
//        assertEquals(404, response.getStatusCodeValue());
//    }

}
