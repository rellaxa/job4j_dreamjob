package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.configuration.IMockitoConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VacancyControllerTest {

    private VacancyService vacancyService;

    private CityService cityService;

    private VacancyController vacancyController;

    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        var vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        var expectedVacancies = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expectedVacancies);

        var model = new ConcurrentModel();
        var view = vacancyController.getAll(model);
        var actualVacancies = model.getAttribute("vacancies");

        assertThat(actualVacancies).isEqualTo(expectedVacancies);
        assertThat(view).isEqualTo("vacancies/list");
    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = vacancyController.getCreationPage(model);
        var actualVacancies = model.getAttribute("cities");

        assertThat(actualVacancies).isEqualTo(expectedCities);
        assertThat(view).isEqualTo("vacancies/create");
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws IOException {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(vacancy);

        var model = new ConcurrentModel();
        var view = vacancyController.create(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.create(new Vacancy(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenRequestOneVacancyPageThenGetPageWithOneVacancy() {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var id =  vacancy.getId();
        var cities = List.of(new City(1, "Санкт-Петербург"), new City(2, "Сочи"));
        when(vacancyService.findById(id)).thenReturn(Optional.of(vacancy));
        when(cityService.findAll()).thenReturn(cities);

        var model = new ConcurrentModel();
        var view = vacancyController.getById(model, id);
        var actualVacancy = model.getAttribute("vacancy");
        var actualCities = model.getAttribute("cities");

        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(actualCities).isEqualTo(cities);
        assertThat(view).isEqualTo("vacancies/one");
    }

    @Test
    public void whenUpdateOneVacancyPageThenGetPageWithOneVacancy() throws IOException {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenUpdatedUnSuccessfulVacancyPageThenGetMessage() throws IOException {
        var vacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.update(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.update(vacancy, testFile, model);
        var actualVacancy = vacancyArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();
        var actualMessage = model.getAttribute("message");

        assertThat(actualVacancy).isEqualTo(vacancy);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
        assertThat(actualMessage).isEqualTo("Вакансия с указанным идентификатором не найдена");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenSomeErrorWhileUpdatingVacancyPageThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to update vacancy");
        when(vacancyService.update(any(Vacancy.class), any(FileDto.class))).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = vacancyController.update(new Vacancy(), testFile, model);
        var actualMessage = model.getAttribute("message");

        assertThat(actualMessage).isEqualTo(expectedException.getMessage());
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenDeleteVacancyThenGetRedirectToVacanciesPage() {
        int id = 1;
        var idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(vacancyService.deleteById(idArgumentCaptor.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, id);
        var actualId = idArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenUnsuccessfulDeleteVacancyPageThenGetErrorPageWithMessage() {
        int id = 1;
        var idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(vacancyService.deleteById(idArgumentCaptor.capture())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = vacancyController.delete(model, id);
        var actualId = idArgumentCaptor.getValue();
        var message = model.getAttribute("message");

        assertThat(actualId).isEqualTo(id);
        assertThat(message).isEqualTo("Вакансия с указанным идентификатором не найдена");
        assertThat(view).isEqualTo("errors/404");
    }
}
