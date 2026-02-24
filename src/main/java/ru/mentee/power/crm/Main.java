package ru.mentee.power.crm;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.InMemoryLeadRepository;
import ru.mentee.power.crm.service.LeadService;
import ru.mentee.power.crm.servlet.LeadListServlet;

public class Main {

    public static void main(String[] args) throws Exception {
        InMemoryLeadRepository repository = new InMemoryLeadRepository();
        LeadService leadService = new LeadService(repository);
        for (int i = 0; i < 5; i++) {
            leadService.addLead(
                    "User" + i + "@gmail.com",
                    "Company" + i,
                    LeadStatus.NEW);
        }

        // Создаём экземпляр Tomcat
        Tomcat tomcat = new Tomcat();

        // Устанавливаем порт
        tomcat.setPort(8080);
        tomcat.getConnector();

        // Создаём контекст приложения
        Context context = tomcat.addContext("/leads", new File(".").getAbsolutePath());

        // Сохраняем LeadService в контекст
        context.getServletContext().setAttribute("leadService", leadService);

        // Регистрируем LeadListServlet
        tomcat.addServlet(context, "LeadListServlet", new LeadListServlet());

        // Задаём URL маппинг
        context.addServletMappingDecoded("", "LeadListServlet");

        // Запускаем сервер
        tomcat.start();

        System.out.println("Tomcat started on port 8080");
        System.out.println("Open http://localhost:8080/leads in browser");

        // Блокируем main поток через tomcat.getServer().await() (чтобы сервер не завершился)
        tomcat.getServer().await();
    }
}