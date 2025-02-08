package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

class IndexControllerTest {

    @Test
    public void whenRequestGetIndexPageThenReturnIndexPage() {
        var indexController = new IndexController();

        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }
}