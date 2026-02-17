// ============================================
// CONFIGURATION
// ============================================

const API_URL = 'http://localhost:8080/api/faqs';

// ============================================
// STATE
// ============================================

const state = {
    currentPage: 0,
    pageSize: 5,
    currentCategory: '',
    searchQuery: '',
    deleteTargetId: null
};

// ============================================
// API CALLS
// ============================================

async function getAllFAQs() {
    let url = `${API_URL}?page=${state.currentPage}&size=${state.pageSize}`;
    
    if (state.currentCategory) {
        url += `&category=${state.currentCategory}`;
    }
    
    if (state.searchQuery) {
        url += `&q=${encodeURIComponent(state.searchQuery)}`;
    }

    const response = await fetch(url);
    if (!response.ok) throw new Error('Ошибка загрузки данных');
    return await response.json();
}

async function createFAQ(data) {
    const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('Ошибка создания FAQ');
    return await response.json();
}

async function updateFAQ(id, data) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (!response.ok) throw new Error('Ошибка обновления FAQ');
    return await response.json();
}

async function deleteFAQ(id) {
    const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE'
    });
    if (!response.ok) throw new Error('Ошибка удаления FAQ');
}

async function getFAQById(id) {
    const response = await fetch(`${API_URL}/${id}`);
    if (!response.ok) throw new Error('FAQ не найден');
    return await response.json();
}

// ============================================
// UI FUNCTIONS
// ============================================

function getCategoryName(category) {
    const names = {
        'general': 'Общие',
        'technical': 'Технические',
        'billing': 'Оплата',
        'support': 'Поддержка'
    };
    return names[category] || '';
}

function showLoading() {
    document.getElementById('faqContainer').innerHTML = `
        <div class="loading-state">
            <div class="spinner"></div>
            <p>Загрузка...</p>
        </div>
    `;
}

function showError(message) {
    document.getElementById('faqContainer').innerHTML = `
        <div class="empty-state">
            <h3>❌ Ошибка</h3>
            <p>${message}</p>
            <button onclick="loadFAQs()" class="btn-submit" style="max-width: 200px; margin: 20px auto; display: block;">
                Попробовать снова
            </button>
        </div>
    `;
}

function showEmpty() {
    document.getElementById('faqContainer').innerHTML = `
        <div class="empty-state">
            <h3>😔 Ничего не найдено</h3>
            <p>Попробуйте изменить параметры поиска или добавьте новый вопрос</p>
        </div>
    `;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function renderFAQs(faqs) {
    if (faqs.length === 0) {
        showEmpty();
        return;
    }

    const html = faqs.map(faq => `
        <div class="faq-item">
            <div class="faq-question">
                <span>${escapeHtml(faq.question)}</span>
                ${faq.category ? `<span class="faq-category">${getCategoryName(faq.category)}</span>` : ''}
            </div>
            <div class="faq-answer">${escapeHtml(faq.answer)}</div>
            <div class="faq-actions">
                <button class="btn-edit" onclick="editFAQ(${faq.id})">✏️ Редактировать</button>
                <button class="btn-delete" onclick="confirmDeleteFAQ(${faq.id})">🗑️ Удалить</button>
            </div>
        </div>
    `).join('');

    document.getElementById('faqContainer').innerHTML = html;
}

function renderPagination(data) {
    const container = document.getElementById('pagination');
    
    if (data.totalPages <= 1) {
        container.innerHTML = '';
        return;
    }

    let html = '';
    const page = data.page;
    const total = data.totalPages;

    // Previous
    html += `<button class="page-btn" ${page === 0 ? 'disabled' : ''} onclick="goToPage(${page - 1})">←</button>`;

    // Pages
    for (let i = 0; i < total; i++) {
        if (i === 0 || i === total - 1 || (i >= page - 1 && i <= page + 1)) {
            html += `<button class="page-btn ${i === page ? 'active' : ''}" onclick="goToPage(${i})">${i + 1}</button>`;
        } else if (i === page - 2 || i === page + 2) {
            html += `<span style="padding: 10px">...</span>`;
        }
    }

    // Next
    html += `<button class="page-btn" ${page === total - 1 ? 'disabled' : ''} onclick="goToPage(${page + 1})">→</button>`;

    container.innerHTML = html;
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast show ${type}`;
    setTimeout(() => toast.classList.remove('show'), 3000);
}

function showModal() {
    document.getElementById('deleteModal').classList.add('show');
}

function hideModal() {
    document.getElementById('deleteModal').classList.remove('show');
}

function resetForm() {
    document.getElementById('faqForm').reset();
    document.getElementById('editId').value = '';
    document.getElementById('formTitle').textContent = '➕ Добавить вопрос';
    document.getElementById('submitBtn').textContent = 'Добавить вопрос';
    document.getElementById('cancelBtn').style.display = 'none';
    updateCharCount('question', 0);
    updateCharCount('answer', 0);
}

function updateCharCount(field, count) {
    document.getElementById(`${field}Count`).textContent = `${count}/2000`;
}

// ============================================
// MAIN FUNCTIONS
// ============================================

async function loadFAQs() {
    try {
        showLoading();
        const data = await getAllFAQs();
        renderFAQs(data.content);
        renderPagination(data);
    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
        showToast(error.message, 'error');
    }
}

async function handleSubmit(e) {
    e.preventDefault();

    const question = document.getElementById('question').value.trim();
    const answer = document.getElementById('answer').value.trim();
    const category = document.getElementById('category').value;
    const editId = document.getElementById('editId').value;

    if (!question || !answer) {
        showToast('Заполните все обязательные поля', 'error');
        return;
    }

    const data = { question, answer, category: category || null };

    try {
        if (editId) {
            await updateFAQ(editId, data);
            showToast('Вопрос успешно обновлен!', 'success');
        } else {
            await createFAQ(data);
            showToast('Вопрос успешно добавлен!', 'success');
        }
        resetForm();
        state.currentPage = 0;
        await loadFAQs();
    } catch (error) {
        console.error('Error:', error);
        showToast(error.message, 'error');
    }
}

async function editFAQ(id) {
    try {
        const faq = await getFAQById(id);
        document.getElementById('editId').value = faq.id;
        document.getElementById('question').value = faq.question;
        document.getElementById('answer').value = faq.answer;
        document.getElementById('category').value = faq.category || '';
        document.getElementById('formTitle').textContent = '✏️ Редактировать вопрос';
        document.getElementById('submitBtn').textContent = 'Сохранить изменения';
        document.getElementById('cancelBtn').style.display = 'block';
        updateCharCount('question', faq.question.length);
        updateCharCount('answer', faq.answer.length);
        document.querySelector('.add-faq-form').scrollIntoView({ behavior: 'smooth' });
    } catch (error) {
        console.error('Error:', error);
        showToast(error.message, 'error');
    }
}

function confirmDeleteFAQ(id) {
    state.deleteTargetId = id;
    showModal();
}

async function handleDelete() {
    if (!state.deleteTargetId) return;

    try {
        await deleteFAQ(state.deleteTargetId);
        showToast('Вопрос успешно удален!', 'success');
        hideModal();
        state.deleteTargetId = null;
        await loadFAQs();
    } catch (error) {
        console.error('Error:', error);
        showToast(error.message, 'error');
    }
}

function cancelDelete() {
    hideModal();
    state.deleteTargetId = null;
}

function cancelEdit() {
    resetForm();
}

function goToPage(page) {
    state.currentPage = page;
    loadFAQs();
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function filterByCategory(category) {
    state.currentCategory = category;
    state.currentPage = 0;
    
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
        if (tab.dataset.category === category) {
            tab.classList.add('active');
        }
    });
    
    loadFAQs();
}

let searchTimeout;
function handleSearch(query) {
    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        state.searchQuery = query;
        state.currentPage = 0;
        loadFAQs();
    }, 500);
}

// ============================================
// EVENT LISTENERS
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    // Load FAQs
    loadFAQs();

    // Form submit
    document.getElementById('faqForm').addEventListener('submit', handleSubmit);

    // Search
    document.getElementById('searchInput').addEventListener('input', (e) => {
        handleSearch(e.target.value);
    });

    // Category tabs
    document.querySelectorAll('.tab').forEach(tab => {
        tab.addEventListener('click', (e) => {
            filterByCategory(e.target.dataset.category);
        });
    });

    // Cancel buttons
    document.getElementById('cancelBtn').addEventListener('click', cancelEdit);
    document.getElementById('cancelDelete').addEventListener('click', cancelDelete);
    document.getElementById('confirmDelete').addEventListener('click', handleDelete);

    // Modal close on outside click
    document.getElementById('deleteModal').addEventListener('click', (e) => {
        if (e.target.id === 'deleteModal') cancelDelete();
    });

    // Character count
    document.getElementById('question').addEventListener('input', (e) => {
        updateCharCount('question', e.target.value.length);
    });

    document.getElementById('answer').addEventListener('input', (e) => {
        updateCharCount('answer', e.target.value.length);
    });
});
