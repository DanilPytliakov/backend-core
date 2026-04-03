@import java.util.List
@import java.util.Objects
@import ru.mentee.power.crm.domain.Company
@import ru.mentee.power.crm.domain.Lead
@import ru.mentee.power.crm.domain.LeadStatus

@param List<Lead> leads
@param List<String> industries
@param LeadStatus currentStatus
@param String currentName
@param String currentEmail
@param String currentCompanyName
@param String currentCompanyIndustry
@param Boolean leadNotFound = false
@param List<Company> companies

@template.layout.main(content = @`
@if(leadNotFound)
    <div id="alert-box"
         class="fixed top-6 left-1/2 -translate-x-1/2 z-50
                bg-red-500 text-white text-center
                px-6 py-4 rounded-lg shadow-lg w-fit">
        <p class="font-semibold">Не удалось найти клиента с данным id</p>
    </div>

    <script>
        setTimeout(() => {
            document.getElementById('alert-box').style.display = 'none';
        }, 2000);
    </script>
@endif

    <div class="mb-4 flex justify-between items-center">
        <button onclick="toggleFilter()"
                class="bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300">
            🔍 Фильтр
        </button>
        <a href="/leads/new"
           class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
            + Добавить лида
        </a>
    </div>

    <div id="filter-panel"
         class="${currentName!= null
                || currentEmail != null
                || currentCompanyName != null
                || currentCompanyIndustry != null
                || currentStatus != null ? "" : "hidden"}
         mb-4 p-4 bg-white rounded-lg shadow-md">
        <form method="get" action="/leads" class="flex gap-4 items-end flex-wrap">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Имя</label>
                <input
                        type="text"
                        name="name"
                        value="${currentName != null ? currentName : ""}"
                        class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Почта</label>
                <input
                        type="text"
                        name="email"
                        value="${currentEmail != null ? currentEmail : ""}"
                        class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
                <select
                        name="companyName"
                        class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500">
                    @if(currentCompanyName == null)
                        <option value="" selected>Все</option>
                    @else
                        <option value="">Все</option>
                    @endif
                    @for(var company : companies)
                        @if(Objects.equals(company.getName(), currentCompanyName))
                            <option value="${company.getName()}" selected>${company.getName()}</option>
                        @else
                            <option value="${company.getName()}">${company.getName()}</option>
                        @endif
                    @endfor
                </select>
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Индустрия</label>
                <select
                        name="industry"
                        class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500">
                    @if(currentCompanyIndustry == null)
                        <option value="" selected>Все</option>
                    @else
                        <option value="">Все</option>
                    @endif
                    @for(String industry : industries)
                        @if(Objects.equals(industry, currentCompanyIndustry))
                            <option value="${industry}" selected>${industry}</option>
                        @else
                            <option value="${industry}">${industry}</option>
                        @endif
                    @endfor
                </select>
            </div>

            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
                <select
                        name="status"
                        class="px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-blue-500">
                    @if(currentStatus == null)
                        <option value="" selected>Все</option>
                    @else
                        <option value="">Все</option>
                    @endif
                    @for(var status : LeadStatus.values())
                        @if(status == currentStatus)
                            <option value="${status}" selected>${status}</option>
                        @else
                            <option value="${status}">${status}</option>
                        @endif
                    @endfor
                </select>
            </div>

            <div class="flex gap-2">
                <button type="submit"
                        class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                    Применить
                </button>
                <a href="/leads"
                   class="bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300">
                    Сбросить
                </a>
            </div>
        </form>
    </div>

<div class="bg-white rounded-lg shadow-md p-6">
    <h2 class="text-2xl font-bold mb-4">Список пользователей</h2>

    <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-100">
        <tr>
            <th class="px-4 py-2 text-left">Имя</th>
            <th class="px-4 py-2 text-left">Почтовый адрес</th>
            <th class="px-4 py-2 text-left">Компания</th>
            <th class="px-4 py-2 text-left">Отрасль</th>
            <th class="px-4 py-2 text-left">Статус</th>
        </tr>
        </thead>
        <tbody>
        @for(var lead : leads)
            <tr class="border-t hover:bg-gray-50 cursor-pointer"
                onclick="selectRow(this, '${lead.getId().toString()}')">
                <td class="px-4 py-2">${lead.getName()}</td>
                <td class="px-4 py-2">${lead.getEmail()}</td>
                <td class="px-4 py-2">
                    @if(lead.getCompany() != null)
                        ${lead.getCompany().getName()}
                    @else
                        —
                    @endif
                </td>
                <td class="px-4 py-2">
                    @if(lead.getCompany() != null)
                        ${lead.getCompany().getIndustry()}
                    @else
                        —
                    @endif
                </td>
                <td class="px-4 py-2">
                            <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                                ${lead.getStatus()}
                            </span>
                </td>
            </tr>
        @endfor
        </tbody>
    </table>

    <div id="action-bar" class="hidden mt-4 flex gap-3">
        <a id="edit-btn"
           href="#"
           class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
            Редактировать
        </a>
        <a id="convert-btn"
           href="#"
           class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
            Создать сделку
        </a>
        <form id="delete-form" method="post" action="#" class="inline">
            <button type="submit"
                    onclick="return confirm('Вы уверены что хотите удалить лида?')"
                    class="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600">
                Удалить
            </button>
        </form>
        <button onclick="cancelSelection()"
                class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400">
            ✕ Отмена
        </button>
    </div>
</div>

<script>
    let selectedRow = null;

    function selectRow(row, id) {
        if (selectedRow) {
            selectedRow.classList.remove('bg-blue-100');
        }

        if (selectedRow === row) {
            selectedRow = null;
            document.getElementById('action-bar').classList.add('hidden');
            return;
        }

        selectedRow = row;
        row.classList.add('bg-blue-100');

        document.getElementById('edit-btn').href = '/leads/' + id + '/edit';
        document.getElementById('convert-btn').href = '/deals/convert/' + id;
        document.getElementById('delete-form').action = '/leads/' + id + '/delete';
        document.getElementById('action-bar').classList.remove('hidden');
        }

        function cancelSelection() {
            if (selectedRow) {
                selectedRow.classList.remove('bg-blue-100');
                selectedRow = null;
            }
        document.getElementById('action-bar').classList.add('hidden');
        }

</script>
<script>
    function toggleFilter() {
        const panel = document.getElementById('filter-panel');
        panel.classList.toggle('hidden');
    }
</script>
`)