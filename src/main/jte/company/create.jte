@import ru.mentee.power.crm.dto.CreateCompanyForm
@import org.springframework.validation.BindingResult

@param CreateCompanyForm companyForm
@param BindingResult errors = null

@template.layout.main(content = @`

    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6"></h1>

        <form action="/companies" method="post" class="space-y-4">
            <!-- Name -->
            <div>
                <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Название</label>
                <input
                        type="name"
                        id="name"
                        name="name"
                        value="${companyForm.getName() != null ? companyForm.getName() : ""}"
                        class="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder=""
                />
                @if(errors != null && errors.hasFieldErrors("name"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("name").getDefaultMessage()}</p>
                @endif
            </div>
            <!-- Industry -->
            <div>
                <label for="industry" class="block text-sm font-medium text-gray-700 mb-1">Отрасль</label>
                <input
                        type="industry"
                        id="industry"
                        name="industry"
                        value="${companyForm.getIndustry() != null ? companyForm.getIndustry() : ""}"
                        class="w-full px-3 py-2 border rounded-md focus:ring-2 focus:ring-blue-500"
                        placeholder=""
                />
                @if(errors != null && errors.hasFieldErrors("industry"))
                    <p class="text-red-600 text-sm mt-1">${errors.getFieldError("industry").getDefaultMessage()}</p>
                @endif
            </div>

            <!-- Submit Button -->
            <button
                    type="submit"
                    class="w-full bg-blue-500 text-white px-4 py-2 rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500"
            >
                Создать компанию
            </button>

            <!-- Cancel Link -->
            <a
                    href="/companies"
                    class="block text-center text-sm text-gray-600 hover:text-gray-900"
            >
                Отмена
            </a>
        </form>
    </div>
`)