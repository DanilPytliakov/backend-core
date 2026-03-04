package gg.jte.generated.ondemand.leads;
import java.util.List;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
@SuppressWarnings("unchecked")
public final class JtelistGenerated {
	public static final String JTE_NAME = "leads/list.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,4,4,4,4,8,8,8,8,9,9,22,22,27,27,27,27,31,31,31,31,35,35,35,35,39,39,39,39,49,49,51,51,51,53,53,67,67,69,69,69,69,70,70,70,71,71,71,74,74,74,78,78,124,124,124,124,124,4,5,6,6,6,6};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<Lead> leads, LeadStatus currentFilter, Boolean leadNotFound) {
		jteOutput.writeContent("\r\n");
		gg.jte.generated.ondemand.layout.JtemainGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\r\n");
				if (leadNotFound) {
					jteOutput.writeContent("\r\n    <div id=\"alert-box\"\r\n         class=\"fixed top-6 left-1/2 -translate-x-1/2 z-50\r\n                bg-red-500 text-white text-center\r\n                px-6 py-4 rounded-lg shadow-lg w-fit\">\r\n        <p class=\"font-semibold\">Не удалось найти клиента с данным id</p>\r\n    </div>\r\n\r\n    <script>\r\n        setTimeout(() => {\r\n            document.getElementById('alert-box').style.display = 'none';\r\n        }, 2000);\r\n    </script>\r\n");
				}
				jteOutput.writeContent("\r\n\r\n<div class=\"mb-4 flex justify-between items-center\">\r\n    <div class=\"mb-4 flex gap-2\">\r\n        <a href=\"/leads\"\r\n           class=\"px-4 py-2 rounded ");
				jteOutput.setContext("a", "class");
				jteOutput.writeUserContent(currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200");
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">\r\n            Все\r\n        </a>\r\n        <a href=\"/leads?status=NEW\"\r\n           class=\"px-4 py-2 rounded ");
				jteOutput.setContext("a", "class");
				jteOutput.writeUserContent(currentFilter == LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200");
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">\r\n            NEW\r\n        </a>\r\n        <a href=\"/leads?status=CONTACTED\"\r\n           class=\"px-4 py-2 rounded ");
				jteOutput.setContext("a", "class");
				jteOutput.writeUserContent(currentFilter == LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200");
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">\r\n            CONTACTED\r\n        </a>\r\n        <a href=\"/leads?status=QUALIFIED\"\r\n           class=\"px-4 py-2 rounded ");
				jteOutput.setContext("a", "class");
				jteOutput.writeUserContent(currentFilter == LeadStatus.QUALIFIED ? "bg-blue-500 text-white" : "bg-gray-200");
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">\r\n            QUALIFIED\r\n        </a>\r\n    </div>\r\n    <a href=\"/leads/new\"\r\n       class=\"bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600\">\r\n        + Добавить лида\r\n    </a>\r\n</div>\r\n\r\n");
				if (currentFilter != null) {
					jteOutput.writeContent("\r\n    <p class=\"text-sm text-gray-600 mb-2\">\r\n        Показаны лиды со статусом: ");
					jteOutput.setContext("p", null);
					jteOutput.writeUserContent(currentFilter);
					jteOutput.writeContent("\r\n    </p>\r\n");
				}
				jteOutput.writeContent("\r\n\r\n<div class=\"bg-white rounded-lg shadow-md p-6\">\r\n    <h2 class=\"text-2xl font-bold mb-4\">Список пользователей</h2>\r\n\r\n    <table class=\"min-w-full bg-white border border-gray-200\">\r\n        <thead class=\"bg-gray-100\">\r\n        <tr>\r\n            <th class=\"px-4 py-2 text-left\">Почтовый адрес</th>\r\n            <th class=\"px-4 py-2 text-left\">Компания</th>\r\n            <th class=\"px-4 py-2 text-left\">Статус</th>\r\n        </tr>\r\n        </thead>\r\n        <tbody>\r\n        ");
				for (var lead : leads) {
					jteOutput.writeContent("\r\n            <tr class=\"border-t hover:bg-gray-50 cursor-pointer\"\r\n                onclick=\"selectRow(this, '");
					jteOutput.setContext("tr", "onclick");
					jteOutput.writeUserContent(lead.id().toString());
					jteOutput.setContext("tr", null);
					jteOutput.writeContent("')\">\r\n                <td class=\"px-4 py-2\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(lead.email());
					jteOutput.writeContent("</td>\r\n                <td class=\"px-4 py-2\">");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(lead.company());
					jteOutput.writeContent("</td>\r\n                <td class=\"px-4 py-2\">\r\n                            <span class=\"px-2 py-1 rounded text-sm bg-green-100 text-green-800\">\r\n                                ");
					jteOutput.setContext("span", null);
					jteOutput.writeUserContent(lead.status());
					jteOutput.writeContent("\r\n                            </span>\r\n                </td>\r\n            </tr>\r\n        ");
				}
				jteOutput.writeContent("\r\n        </tbody>\r\n    </table>\r\n\r\n    <div id=\"action-bar\" class=\"hidden mt-4 flex gap-3\">\r\n        <a id=\"edit-btn\"\r\n           href=\"/leads/{id}/edit\"\r\n           class=\"bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600\">\r\n            ✏️ Редактировать\r\n        </a>\r\n        <button onclick=\"cancelSelection()\"\r\n                class=\"bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400\">\r\n            ✕ Отмена\r\n        </button>\r\n    </div>\r\n</div>\r\n\r\n<script>\r\n    let selectedRow = null;\r\n\r\n    function selectRow(row, id) {\r\n        if (selectedRow) {\r\n            selectedRow.classList.remove('bg-blue-100');\r\n        }\r\n\r\n        if (selectedRow === row) {\r\n            selectedRow = null;\r\n            document.getElementById('action-bar').classList.add('hidden');\r\n            return;\r\n        }\r\n\r\n        selectedRow = row;\r\n        row.classList.add('bg-blue-100');\r\n\r\n        document.getElementById('edit-btn').href = '/leads/' + id + '/edit';\r\n        document.getElementById('action-bar').classList.remove('hidden');\r\n        }\r\n\r\n        function cancelSelection() {\r\n            if (selectedRow) {\r\n                selectedRow.classList.remove('bg-blue-100');\r\n                selectedRow = null;\r\n            }\r\n        document.getElementById('action-bar').classList.add('hidden');\r\n        }\r\n</script>\r\n");
			}
		});
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<Lead> leads = (List<Lead>)params.get("leads");
		LeadStatus currentFilter = (LeadStatus)params.get("currentFilter");
		Boolean leadNotFound = (Boolean)params.getOrDefault("leadNotFound", false);
		render(jteOutput, jteHtmlInterceptor, leads, currentFilter, leadNotFound);
	}
}
