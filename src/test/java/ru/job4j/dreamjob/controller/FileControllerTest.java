package ru.job4j.dreamjob.controller;

import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {

    private FileService fileService;

    private FileController fileController;

    @BeforeEach
    public void initService() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    public void  whenGetFileSuccessfulThenReturnOkEntity() {
        int id = 1;
        var content = new byte[] {1, 2, 3};
        var fileDto = new FileDto("name", content);
        var idArgumentCaptor = ArgumentCaptor.forClass(Integer.class);
        when(fileService.getFileById(idArgumentCaptor.capture())).thenReturn(Optional.of(fileDto));
        var expectedResponse = ResponseEntity.ok(content);

        var responseEntity = fileController.getById(id);
        var actualId = idArgumentCaptor.getValue();

        assertThat(actualId).isEqualTo(id);
        assertThat(responseEntity).isEqualTo(expectedResponse);
    }

    @Test
    public void  whenGetFileFailedThenReturnNotFoundEntity() {
        int id = 1;
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.empty());
        var expectedResponse = ResponseEntity.notFound().build();

        var responseEntity = fileController.getById(id);
        assertThat(responseEntity).isEqualTo(expectedResponse);
    }

}