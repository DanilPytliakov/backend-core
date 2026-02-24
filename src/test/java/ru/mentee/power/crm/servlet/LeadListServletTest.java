package ru.mentee.power.crm.servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LeadListServletTest {

    @Test
    void givenLeadsInService_whenDoGet_thenHtmlTableIsReturned() throws Exception {
        // Given

        // Mock request/response/context
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext context = mock(ServletContext.class);
        LeadService leadService = mock(LeadService.class);

        // Тестовые данные
        List<Lead> leads = List.of(
                new Lead("test1@mail.com", "Company1", LeadStatus.NEW),
                new Lead("test2@mail.com", "Company2", LeadStatus.CONTACTED)
        );

        when(context.getAttribute("leadService")).thenReturn(leadService);
        when(leadService.findAll()).thenReturn(leads);

        // Подготавливаем writer для перехвата HTML
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(writer);

        // Создаём сервлет и подменяем контекст
        LeadListServlet servlet = new LeadListServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        // When

        servlet.doGet(request, response);

        // Then

        String result = stringWriter.toString();

        assertTrue(result.contains("Lead List"));
        assertTrue(result.contains("test1@mail.com"));
        assertTrue(result.contains("Company1"));
        assertTrue(result.contains("NEW"));

        verify(response).setContentType("text/html; charset=UTF-8");
        verify(leadService).findAll();
    }
}