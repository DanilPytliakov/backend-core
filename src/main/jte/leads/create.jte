@import ru.mentee.power.crm.dto.CreateLeadForm
@import ru.mentee.power.crm.domain.LeadStatus
@import org.springframework.validation.BindingResult

@param CreateLeadForm leadForm
@param BindingResult errors = null
@param Boolean leadAlreadyExist = false

@template.layout.main(content = @`
    @if(leadAlreadyExist)
        <div id="alert-box"
             class="fixed top-6 left-1/2 -translate-x-1/2 z-50
                bg-red-500 text-white text-center
                px-6 py-4 rounded-lg shadow-lg w-fit">
            <p class="font-semibold">Клиент с данной почтой уже существует</p>
        </div>

        <script>
            setTimeout(() => {
                document.getElementById('alert-box').style.display = 'none';
            }, 2000);
        </script>
    @endif

    <div class="max-w-md mx-auto mt-8">
        <h1 class="text-2xl font-bold mb-6">Добавить нового лида</h1>

        <form action="/leads" method="post" class="space-y-4">
            <!-- Name -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Имя</label>
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
            <!-- Email -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Почта</label>
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

            <!-- Company -->
            <div>
                <label for="company" class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
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
                    @for(var status : LeadStatus.values())
                        @if(status == leadForm.getStatus())
                            <option value="${status}" selected>${status}</option>
                        @else
                            <option value="${status}">${status}</option>
                        @endif
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
`)