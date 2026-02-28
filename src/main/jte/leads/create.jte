@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param Lead lead

@template.layout.main(
content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Добавить нового лида</h1>

        <form action="/leads" method="post" class="space-y-4">
            <!-- Email -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Почта
                </label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="example@company.com"
                />
            </div>

            <!-- Company -->
            <div>
                <label for="Company" class="block text-sm font-medium text-gray-700 mb-1">
                    Компания
                </label>
                <input
                        type="company"
                        id="company"
                        name="company"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="Название компании"
                />
            </div>

            <!-- Status -->
            <div>
                <label for="Status" class="block text-sm font-medium text-gray-700 mb-1">
                    Статус
                </label>
                <select
                        id="status"
                        name="status"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                >
                    @for (var status : LeadStatus.values())
                        <option value="${status}">${status}</option>
                    @endfor
                </select>
            </div>

            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
            >
                Создать лида
            </button>

            <!-- Cancel Link -->
            <a
                    href="/leads"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`
)