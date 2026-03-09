@param gg.jte.Content content

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CRM - Lead Management</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">
<header class="bg-blue-600 text-white p-4 shadow-md">
    <div class="flex justify-between items-center">
        <h1 class="text-2xl font-bold">CRM System</h1>

        <nav class="flex gap-2">
            <a href="/leads"
               class="px-4 py-2 rounded-md text-white hover:bg-blue-700 transition">
                Лиды
            </a>
            <a href="/deals"
               class="px-4 py-2 rounded-md text-white hover:bg-blue-700 transition">
                Сделки
            </a>
            <a href="/deals/kanban"
               class="px-4 py-2 rounded-md text-white hover:bg-blue-700 transition">
                Воронка продаж
            </a>
        </nav>

        <div class="w-32"></div>
    </div>
</header>

<main class="container mx-auto p-6">
    ${content}
</main>
</body>
</html>