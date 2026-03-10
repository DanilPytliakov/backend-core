@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.dto.ConvertLeadForm
@param ConvertLeadForm leadForm

@template.layout.main(content = @`
    <div class="max-w-md mx-auto">
        <h1 class="text-2xl font-bold mb-6">Конвертировать лид в сделку</h1>
        <form method="post" action="/deals/convert" class="space-y-4">
            <input type="hidden" name="leadId" value="${leadForm.getId().toString()}">

            <div>
                <label class="block text-sm font-medium mb-1">Клиент</label>
                <p class="p-2 bg-white rounded">${leadForm.getName()}</p>
            </div>

            <div>
                <label class="block text-sm font-medium mb-1">Сумма сделки (₽)</label>
                <input type="number" step="0.01" name="amount" required
                       class="w-full px-3 py-2 border border-gray-300 rounded">
            </div>

            <button type="submit" class="w-full bg-green-500 text-white py-2 rounded">
                Создать сделку
            </button>
            <a href="/leads" class="block text-center text-gray-600 mt-2">Отмена</a>
        </form>
    </div>
`)