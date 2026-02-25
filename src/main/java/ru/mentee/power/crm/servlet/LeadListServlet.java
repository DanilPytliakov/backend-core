package ru.mentee.power.crm.servlet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.output.WriterOutput;
import gg.jte.resolve.DirectoryCodeResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.service.LeadService;

@WebServlet("/leads")
public class LeadListServlet extends HttpServlet {

    private TemplateEngine templateEngine;

    @Override
    public void init() throws ServletException {
        Path templatePath = Path.of("src/main/jte");
        DirectoryCodeResolver codeResolver = new DirectoryCodeResolver(templatePath);
        this.templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Получаем объект из контекста
        Object obj = getServletContext().getAttribute("leadService");

        // Проверяем, что это действительно LeadService
        LeadService leadService = null;
        if (obj instanceof LeadService) {
            leadService = (LeadService) obj;
        } else {
            throw new ServletException("Объект leadService отсутствует или имеет неверный тип");
        }

        // Теперь вызываем метод на корректном сервисе
        List<Lead> leads = leadService.findAll();

        // Создаём Map model, для leads
        Map<String, Object> model = new HashMap<>();
        model.put("leads", leads);

        // Устанавливаем Content-Type
        response.setContentType("text/html; charset=UTF-8");

        // Создаём ответ
        templateEngine.render(
                "leads/list.jte",
                model,
                new WriterOutput(response.getWriter())
        );
    }
}
