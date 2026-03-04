package gg.jte.generated.ondemand.layout;
@SuppressWarnings("unchecked")
public final class JtemainGenerated {
	public static final String JTE_NAME = "layout/main.jte";
	public static final int[] JTE_LINE_INFO = {0,0,0,0,0,16,16,16,16,23,23,23,0,0,0,0};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, gg.jte.Content content) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"ru\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n    <title>CRM - Lead Management</title>\r\n    <script src=\"https://cdn.tailwindcss.com\"></script>\r\n</head>\r\n<body class=\"bg-gray-50\">\r\n    <header class=\"bg-blue-600 text-white p-4 shadow-md\">\r\n        <h1 class=\"text-2xl font-bold\">CRM System</h1>\r\n    </header>\r\n\r\n    <main class=\"container mx-auto p-6\">\r\n        ");
		jteOutput.setContext("main", null);
		jteOutput.writeUserContent(content);
		jteOutput.writeContent("\r\n    </main>\r\n\r\n    <footer class=\"bg-gray-800 text-white p-4 text-center mt-8\">\r\n        <p>&copy; 2025 CRM Project</p>\r\n    </footer>\r\n</body>\r\n</html>");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		gg.jte.Content content = (gg.jte.Content)params.get("content");
		render(jteOutput, jteHtmlInterceptor, content);
	}
}
