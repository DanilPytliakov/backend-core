package ru.mentee.power.crm.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.WriterOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.service.LeadService;

class LeadListServletTest {

    private LeadListServlet servlet;
    private LeadService leadService;
    private TemplateEngine templateEngine;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ServletContext context;
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws Exception {
        leadService = mock(LeadService.class);
        templateEngine = mock(TemplateEngine.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        context = mock(ServletContext.class);

        // Создаем экземпляр сервлета через анонимный класс
        servlet = new LeadListServlet() {
            @Override
            public ServletContext getServletContext() {
                return context;
            }
        };

        // Внедряем мок templateEngine через рефлексию
        Field field = LeadListServlet.class.getDeclaredField("templateEngine");
        field.setAccessible(true);
        field.set(servlet, templateEngine);

        // Базовые настройки заглушек
        when(context.getAttribute("leadService")).thenReturn(leadService);
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void shouldRenderLeadsCorrectly() throws Exception {
        List<Lead> leads = List.of(new Lead("test@mail.com", "Company", LeadStatus.NEW));
        when(leadService.findAll()).thenReturn(leads);

        servlet.doGet(request, response);

        verify(leadService).findAll();
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(templateEngine).render(
                eq("leads/list.jte"),
                eq(Map.of("leads", leads)),
                any(WriterOutput.class)
        );
    }

    @Test
    void shouldThrowExceptionWhenLeadServiceMissing() {
        when(context.getAttribute("leadService")).thenReturn(null);

        ServletException exception = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response));

        assertEquals("Объект leadService отсутствует или имеет неверный тип", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenLeadServiceWrongType() {
        when(context.getAttribute("leadService")).thenReturn("wrongType");

        ServletException exception = assertThrows(ServletException.class,
                () -> servlet.doGet(request, response));

        assertEquals("Объект leadService отсутствует или имеет неверный тип", exception.getMessage());
    }

    @Test
    void shouldRenderEmptyLeadsList() throws Exception {
        when(leadService.findAll()).thenReturn(Collections.emptyList());

        servlet.doGet(request, response);

        verify(leadService).findAll();
        verify(response).setContentType("text/html; charset=UTF-8");
        verify(templateEngine).render(
                eq("leads/list.jte"),
                eq(Map.of("leads", Collections.emptyList())),
                any(WriterOutput.class)
        );
    }


    // Тест с реальным TemplateEngine для покрытия JTE
    void shouldRenderRealJteTemplates() throws Exception {
        // Настраиваем реальный TemplateEngine
        Path templatePath = Path.of("src/main/jte");
        TemplateEngine realEngine = TemplateEngine.create(new DirectoryCodeResolver(templatePath), ContentType.Html);

        // Внедряем его в сервлет
        Field field = LeadListServlet.class.getDeclaredField("templateEngine");
        field.setAccessible(true);
        field.set(servlet, realEngine);

        // Данные для теста
        List<Lead> leads = List.of(
                new Lead("test1@mail.com", "Company1", LeadStatus.NEW),
                new Lead("test2@mail.com", "Company2", LeadStatus.CONTACTED)
        );
        when(leadService.findAll()).thenReturn(leads);

        // Реальный Writer
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        // Вызов сервлета
        servlet.doGet(request, response);

        // Проверка Content-Type
        verify(response).setContentType("text/html; charset=UTF-8");

        // Проверка, что что-то реально записалось
        String output = writer.toString();
        assertFalse(output.isEmpty(), "JTE-шаблон должен сгенерировать HTML");

        // Можно проверить, что строки из шаблона присутствуют
        assertTrue(output.contains("Company1"));
        assertTrue(output.contains("Company2"));
    }
}