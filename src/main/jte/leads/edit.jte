@import org.springframework.validation.BindingResult
@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.dto.UpdateLeadForm
@import ru.mentee.power.crm.domain.LeadStatus
@import java.util.UUID;

@param UpdateLeadForm leadForm
@param BindingResult errors = null
@param UUID id

@template.layout.main(content = @`
    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Редактировать лида</h1>

        <form action="/leads/${leadForm.getId().toString()}/edit" method="post" class="space-y-4">
            <div>
                <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Имя</label>
                <input
                        type="name"
                        id="name"
                        name="name"
                        value="${leadForm.getName() != null ? leadForm.getName() : ""}"
                        class="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="Имя"
                />
                @if(errors != null && errors.hasFieldErrors("name"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("name").getDefaultMessage()}</p>
                @endif
            </div>

            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    Почта
                </label>
                <input
                        type="email"
                        id="email"
                        name="email"
                        value="${leadForm.getEmail() != null ? leadForm.getEmail() : ""}"
                        class="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="example@company.com"
                />
                @if(errors != null && errors.hasFieldErrors("email"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("email").getDefaultMessage()}</p>
                @endif
            </div>

            <div>
                <label for="company" class="block text-sm font-medium text-gray-700 mb-1">
                    Компания
                </label>
                <input
                        type="text"
                        id="company"
                        name="company"
                        value="${leadForm.getCompany() != null ? leadForm.getCompany() : ""}"
                        class="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder="Название компании"
                />
                @if(errors != null && errors.hasFieldErrors("company"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("company").getDefaultMessage()}</p>
                @endif
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
                        @if(s == leadForm.getStatus())
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