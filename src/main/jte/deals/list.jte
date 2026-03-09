@import ru.mentee.power.crm.domain.Deal
@param java.util.List<Deal> deals = new java.util.ArrayList<>()

@template.layout.main(content = @`
    <div class="max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-6">
            <h1 class="text-3xl font-bold">Сделки CRM</h1>
            <a href="/deals/kanban" class="bg-blue-500 text-white px-4 py-2 rounded">Воронка продаж</a>
        </div>

        <table class="w-full bg-white shadow-md rounded">
            <thead class="bg-gray-200">
            <tr>
                <th class="p-3 text-left">ID</th>
                <th class="p-3 text-left">Сумма</th>
                <th class="p-3 text-left">Статус</th>
                <th class="p-3 text-left">Дата создания</th>
            </tr>
            </thead>
            <tbody>
            @for(Deal deal : deals)
                <tr class="border-b hover:bg-gray-50 cursor-pointer"
                    onclick="selectRow(this, '${deal.getId().toString()}')">
                    <td class="p-3">${deal.getId().toString()}</td>
                    <td class="p-3">${deal.getAmount()} ₽</td>
                    <td class="p-3">
                        <span class="px-2 py-1 rounded text-sm bg-blue-100">${deal.getStatus()}</span>
                    </td>
                    <td class="p-3">${deal.getCreatedAt().toString()}</td>
                </tr>
            @endfor
            </tbody>
        </table>

        <div id="action-bar" class="hidden mt-4 flex flex-col gap-3">
            <div class="flex gap-3">
                <button onclick="showTransitions()"
                        class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                    Сменить статус
                </button>
                <button onclick="cancelSelection()"
                        class="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400">
                    ✕ Отмена
                </button>
            </div>
            <div id="transitions-container" class="hidden flex gap-2 flex-wrap"></div>
        </div>
    </div>
`)

@raw
<script>
    let selectedRow = null;
    let selectedId = null;

    function selectRow(row, id) {
        if (selectedRow) {
            selectedRow.classList.remove('bg-blue-100');
        }
        if (selectedRow === row) {
            selectedRow = null;
            selectedId = null;
            document.getElementById('action-bar').classList.add('hidden');
            document.getElementById('transitions-container').classList.add('hidden');
            return;
        }
        selectedRow = row;
        selectedId = id;
        row.classList.add('bg-blue-100');
        document.getElementById('transitions-container').classList.add('hidden');
        document.getElementById('action-bar').classList.remove('hidden');
    }

    function cancelSelection() {
        if (selectedRow) {
            selectedRow.classList.remove('bg-blue-100');
            selectedRow = null;
            selectedId = null;
        }
        document.getElementById('action-bar').classList.add('hidden');
        document.getElementById('transitions-container').classList.add('hidden');
    }

    async function showTransitions() {
        const container = document.getElementById('transitions-container');
        if (!container.classList.contains('hidden')) {
            container.classList.add('hidden');
            return;
        }
        const response = await fetch('/deals/' + selectedId + '/transitions');
        const transitions = await response.json();
        container.innerHTML = '';
        if (transitions.length === 0) {
            container.innerHTML = '<p class="text-gray-500 text-sm">Нет доступных переходов</p>';
        } else {
            for (const status of transitions) {
                const lost = status === 'LOST';
                const color = lost ? 'bg-red-500 hover:bg-red-600' : 'bg-green-500 hover:bg-green-600';
                const btn = document.createElement('button');
                btn.textContent = status;
                btn.className = 'px-3 py-2 rounded text-sm text-white ' + color;
                btn.onclick = () => handleTransition(selectedId, status, lost);
                container.appendChild(btn);
            }
        }
        container.classList.remove('hidden');
    }

    function handleTransition(dealId, newStatus, requiresConfirmation) {
        if (requiresConfirmation) {
            const confirmed = confirm('Вы уверены что хотите отметить сделку как проигранную?');
            if (!confirmed) return;
        }

        const form = document.createElement('form');
        form.method = 'post';
        form.action = '/deals/' + dealId + '/transition';

        const input = document.createElement('input');
        input.type = 'hidden';
        input.name = 'newStatus';
        input.value = newStatus;
        form.appendChild(input);

        document.body.appendChild(form);
        form.submit();
    }
</script>
@endraw