function editTodo(label) {
    var li = label.closest('li');
    li.classList.add('editing');
    var input = li.querySelector('.edit');
    input.dataset.originalValue = input.value;
    input.focus();
    input.setSelectionRange(input.value.length, input.value.length);
}

function cancelEdit(input) {
    var li = input.closest('li');
    if (li.classList.contains('editing')) {
        li.classList.remove('editing');
        input.value = input.dataset.originalValue || input.value;
    }
}

// Track current filter client-side
var currentFilter = 'all';

document.addEventListener('click', function(e) {
    if (e.target.classList.contains('filter-link')) {
        document.querySelectorAll('.filter-link').forEach(function(a) { a.classList.remove('selected'); });
        e.target.classList.add('selected');
        currentFilter = e.target.dataset.filter;
    }
});

// Append filter parameter to all HTMX requests
document.addEventListener('htmx:configRequest', function(e) {
    if (e.detail.path && !e.detail.path.includes('filter=')) {
        if (e.detail.parameters) {
            e.detail.parameters['filter'] = currentFilter;
        }
    }
});
