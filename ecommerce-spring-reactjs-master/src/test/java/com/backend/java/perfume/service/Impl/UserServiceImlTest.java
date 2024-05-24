package com.backend.java.perfume.service.Impl;

import com.backend.java.perfume.util.TestConstants;
import com.backend.java.perfume.domain.Perfume;
import com.backend.java.perfume.domain.User;
import com.backend.java.perfume.enums.Role;
import com.backend.java.perfume.repository.PerfumeRepository;
import com.backend.java.perfume.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImlTest {

    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PerfumeRepository perfumeRepository;

    @Test
    public void findUserById() {
        User user = new User();
        user.setId(122L);

        when(userRepository.findById(122L)).thenReturn(java.util.Optional.of(user));
        userService.getUserById(122L);
        assertEquals(122L, user.getId());
        verify(userRepository, times(1)).findById(122L);
    }

    @Test
    public void getUserInfo() {
        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);

        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        userService.getUserInfo(TestConstants.USER_EMAIL);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        verify(userRepository, times(1)).findByEmail(TestConstants.USER_EMAIL);
    }

    @Test
    public void findAllUsers() {
        Pageable pageable = PageRequest.of(0, 20);
        List<User> usersList = new ArrayList<>();
        usersList.add(new User());
        usersList.add(new User());
        userService.getAllUsers(pageable);
        Page<User> users = new PageImpl<>(usersList, pageable, 20);

        when(userRepository.findAllByOrderByIdAsc(pageable)).thenReturn(users);
        assertEquals(2, usersList.size());
        verify(userRepository, times(1)).findAllByOrderByIdAsc(pageable);
    }

    @Test
    public void getCart() {
        List<Long> perfumeIds = new ArrayList<>(Arrays.asList(2L, 4L));
        Perfume firstPerfume = new Perfume();
        firstPerfume.setId(2L);
        Perfume secondPerfume = new Perfume();
        secondPerfume.setId(4L);
        List<Perfume> perfumeList = new ArrayList<>(Arrays.asList(firstPerfume, secondPerfume));
        userService.getCart(perfumeIds);

        when(perfumeRepository.findByIdIn(perfumeIds)).thenReturn(perfumeList);
        assertEquals(2, perfumeList.size());
        assertEquals(2, perfumeIds.size());
        assertNotNull(perfumeList);
        verify(perfumeRepository, times(1)).findByIdIn(perfumeIds);
    }

    @Test
    public void loadUserByUsername() {
        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setActive(true);
        user.setFirstName(TestConstants.FIRST_NAME);
        user.setRoles(Collections.singleton(Role.USER));

        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        assertTrue(user.isActive());
    }

    @Test
    public void updateUserInfo() {
        User user = new User();
        user.setEmail(TestConstants.USER_EMAIL);
        user.setFirstName(TestConstants.FIRST_NAME);

        when(userRepository.findByEmail(TestConstants.USER_EMAIL)).thenReturn(Optional.of(user));
        userService.updateUserInfo(TestConstants.USER_EMAIL, user);
        assertEquals(TestConstants.USER_EMAIL, user.getEmail());
        assertEquals(TestConstants.FIRST_NAME, user.getFirstName());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }
}
