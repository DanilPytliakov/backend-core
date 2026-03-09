@import ru.mentee.power.crm.domain.Deal
@import ru.mentee.power.crm.domain.DealStatus
@param java.util.Map<DealStatus, java.util.List<Deal>> dealsByStatus

@template.layout.main(content = @`
    <h1 class="text-3xl font-bold mb-6">Воронка продаж (Kanban)</h1>

    <div class="grid grid-cols-6 gap-4">
        @for(DealStatus status : DealStatus.values())
            <div class="bg-white rounded shadow-md p-4">
                <h2 class="font-bold mb-4 text-center">${status}</h2>

                @if(dealsByStatus != null)
                    @for(Deal deal : dealsByStatus.getOrDefault(status, java.util.List.of()))
                        <div class="bg-gray-50 p-3 mb-2 rounded border">
                            <p class="font-semibold">${deal.getAmount()} ₽</p>
                            <p class="text-sm text-gray-600">Lead: ${deal.getLeadId().toString()}</p>
                        </div>
                    @endfor
                @endif
            </div>
        @endfor
    </div>
`)