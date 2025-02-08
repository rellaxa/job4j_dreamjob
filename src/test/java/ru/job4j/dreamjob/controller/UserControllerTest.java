package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    public void initService() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegisterPageThenGetRegisterPage() {
        assertThat(userController.getRegistrationPage()).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterUserThenGetSameDataAndRedirectToVacanciesPage() {
        var user = new User(1, "email", "name", "password");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var acutalUser = userArgumentCaptor.getValue();

        assertThat(acutalUser).isEqualTo(user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenRegisteredUserAlreadyExistsThenGetErrorPage() {
        var user = new User(1, "email", "name", "password");
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(user, model);
        var message = model.get("message");

        assertThat(message).isEqualTo("Пользователь с такой почтой уже существует");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenRequestGetLoginPageThenGetLoginPage() {
        assertThat(userController.getLoginPage()).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenGetTheSameDataAndRedirectToVacanciesPage() {
        var user = new User(1, "email", "name", "password");
        var emailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailArgumentCaptor.capture(), passwordArgumentCaptor.capture()))
                .thenReturn(Optional.of(user));
        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var session = mock(HttpSession.class);
        when(request.getSession()).thenReturn(session);
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        var nameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        doNothing().when(session).setAttribute(nameArgumentCaptor.capture(), userArgumentCaptor.capture());

        var view = userController.loginUser(user, model, request);
        var email = emailArgumentCaptor.getValue();
        var password = passwordArgumentCaptor.getValue();
        var name = nameArgumentCaptor.getValue();
        var actualUser = userArgumentCaptor.getValue();

        assertThat(email).isEqualTo(user.getEmail());
        assertThat(password).isEqualTo(user.getPassword());
        assertThat(name).isEqualTo("user");
        assertThat(actualUser).isEqualTo(user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginUserThenGetSomeErrorPage() {
        var user = new User(1, "email", "name", "password");
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var request = mock(HttpServletRequest.class);
        var view = userController.loginUser(user, model, request);
        var message = model.get("error");

        assertThat(message).isEqualTo("Почта или пароль введены неверно");
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLogoutUserThenGetErrorPage() {
        var session = mock(HttpSession.class);

        var view = userController.logout(session);
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}