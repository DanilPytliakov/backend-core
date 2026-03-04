@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param Lead lead

@template.layout.main(content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Редактировать лида</h1>

        <form action="/leads/${lead.id().toString()}/edit" method="post" class="space-y-4">
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Почта
                </label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        required
                        value="${lead.email()}"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="example@company.com"
                />
            </div>

            <div>
                <label for="company" class="block text-sm font-medium text-gray-700 mb-1">
                    Компания
                </label>
                <input
                        type="text"
                        id="company"
                        name="company"
                        required
                        value="${lead.company()}"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="Название компании"
                />
            </div>

            <div>
                <label for="status" class="block text-sm font-medium text-gray-700 mb-1">
                    Статус
                </label>
                <select
                        id="status"
                        name="status"
                        required
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                >
                    @for(var s : LeadStatus.values())
                        @if(s == lead.status())
                            <option value="${s}" selected>${s}</option>
                        @else
                            <option value="${s}">${s}</option>
                        @endif
                    @endfor
                </select>
            </div>

            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600"
            >
                Сохранить
            </button>

            <a href="/leads"
               class="block text-center text-sm text-gray-600 hover:text-gray-900">
                Отмена
            </a>
        </form>
    </div>
`)