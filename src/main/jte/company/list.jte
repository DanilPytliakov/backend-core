@import java.util.List
@import ru.mentee.power.crm.domain.Company

@param List<Company> companies

@template.layout.main(content = @`
    <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-3xl font-bold">Компании</h1>
            <a href="/company/new"
               class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600">
                + Добавить компанию
            </a>
        </div>

        <table class="w-full bg-white shadow-md rounded">
            <thead class="bg-gray-200">
            <tr>
                <th class="p-3 text-left">Название</th>
                <th class="p-3 text-left">Отрасль</th>
            </tr>
            </thead>
            <tbody>
            @for(Company company : companies)
                <tr class="border-b hover:bg-gray-50 cursor-pointer"
                    onclick="selectRow(this, '${company.getId().toString()}')">
                    <td class="p-3">${company.getName()}</td>
                    <td class="p-3">${company.getIndustry()}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>

    <div id="action-bar" class="hidden mt-4 flex gap-3 max-w-6xl mx-auto">
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
`)

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
        document.getElementById('delete-form').action = '/companies/' + id + '/delete';
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