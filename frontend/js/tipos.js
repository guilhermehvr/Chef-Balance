const API_TIPOS_URL = 'http://localhost:8000/tipos';
let tipos = [];
let currentDeleteTipoId = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarTipos();
    configurarEventosTipo();
    configurarModalTipo();
});

function configurarEventosTipo() {
    document.getElementById('tipo-form').addEventListener('submit', salvarTipo);
    document.getElementById('cancel-btn-tipo').addEventListener('click', limparFormularioTipo);
}

function configurarModalTipo() {
    const modal = document.getElementById('confirm-modal-tipo');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn-tipo');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn-tipo');

    confirmDeleteBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        if (currentDeleteTipoId) deletarTipo(currentDeleteTipoId);
    });

    cancelDeleteBtn.addEventListener('click', () => {
        modal.style.display = 'none';
    });
}

async function carregarTipos() {
    try {
        const response = await fetch(API_TIPOS_URL);
        tipos = await response.json();
        renderizarTabelaTipos(tipos);
    } catch (error) {
        console.error('Erro:', error);
    }
}

async function salvarTipo(event) {
    event.preventDefault();
    
    const tipo = {
        id: document.getElementById('tipo-id').value || null,
        nome: document.getElementById('nome-tipo').value
    };

    if (!tipo.nome || tipo.nome.trim() === '') {
        alert('Nome do tipo é obrigatório!');
        return;
    }

    const metodo = tipo.id ? 'PUT' : 'POST';
    const url = tipo.id ? `${API_TIPOS_URL}/${tipo.id}` : API_TIPOS_URL;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(tipo)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Erro ao salvar tipo');
        }
        
        limparFormularioTipo();
        await carregarTipos();
        
        // NOTIFICA A JANELA PRINCIPAL PARA ATUALIZAR
        if (window.opener && !window.opener.closed) {
            window.opener.postMessage('TIPOS_ATUALIZADOS', '*');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert(error.message);
    }
}

function editarTipo(id) {
    const tipo = tipos.find(t => t.id === id);
    if (!tipo) return;

    document.getElementById('form-title-tipo').textContent = 'Editar Tipo';
    document.getElementById('tipo-id').value = tipo.id;
    document.getElementById('nome-tipo').value = tipo.nome;
}

async function deletarTipo(id) {
    try {
        const response = await fetch(`${API_TIPOS_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Erro ao deletar');
        }

        carregarTipos();
    } catch (error) {
        console.error('Erro:', error);
        alert(error.message);
    }
}

function renderizarTabelaTipos(tipos) {
    const tbody = document.querySelector('#tipos-table tbody');
    tbody.innerHTML = '';

    tipos.forEach(tipo => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${tipo.id}</td>
            <td>${tipo.nome}</td>
            <td>
                <button class="action-btn edit-btn" onclick="editarTipo(${tipo.id})">Editar</button>
                <button class="action-btn delete-btn" onclick="showDeleteModalTipo(${tipo.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function limparFormularioTipo() {
    document.getElementById('form-title-tipo').textContent = 'Adicionar Novo Tipo';
    document.getElementById('tipo-form').reset();
    document.getElementById('tipo-id').value = '';
}

function showDeleteModalTipo(id) {
    currentDeleteTipoId = id;
    document.getElementById('confirm-modal-tipo').style.display = 'flex';
}

// Para acesso global
window.editarTipo = editarTipo;
window.showDeleteModalTipo = showDeleteModalTipo;