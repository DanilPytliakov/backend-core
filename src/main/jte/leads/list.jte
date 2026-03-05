@import java.util.List
@import ru.mentee.power.crm.model.Lead
@import ru.mentee.power.crm.model.LeadStatus

@param List<Lead> leads
@param LeadStatus currentFilter
@param Boolean leadNotFound = false

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
    <div class="mb-4 flex gap-2">
        <a href="/leads"
           class="px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}">
            Все
        </a>
        <a href="/leads?status=NEW"
           class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200"}">
            NEW
        </a>
        <a href="/leads?status=CONTACTED"
           class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200"}">
            CONTACTED
        </a>
        <a href="/leads?status=QUALIFIED"
           class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? "bg-blue-500 text-white" : "bg-gray-200"}">
            QUALIFIED
        </a>
    </div>
    <a href="/leads/new"
       class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
        + Добавить лида
    </a>
</div>

@if(currentFilter != null)
    <p class="text-sm text-gray-600 mb-2">
        Показаны лиды со статусом: ${currentFilter}
    </p>
@endif

<div class="bg-white rounded-lg shadow-md p-6">
    <h2 class="text-2xl font-bold mb-4">Список пользователей</h2>

    <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-100">
        <tr>
            <th class="px-4 py-2 text-left">Почтовый адрес</th>
            <th class="px-4 py-2 text-left">Компания</th>
            <th class="px-4 py-2 text-left">Статус</th>
        </tr>
        </thead>
        <tbody>
        @for(var lead : leads)
            <tr class="border-t hover:bg-gray-50 cursor-pointer"
                onclick="selectRow(this, '${lead.id().toString()}')">
                <td class="px-4 py-2">${lead.email()}</td>
                <td class="px-4 py-2">${lead.company()}</td>
                <td class="px-4 py-2">
                            <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                                ${lead.status()}
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
`)