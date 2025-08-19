const API_URL = 'http://localhost:8000/bebidas';
let bebidas = [];
let tipos = [];
let currentDeleteId = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarTipos();
    carregarBebidas();
    configurarEventos();
    configurarModal();
    
    // OUVINTE PARA ATUALIZAÇÕES DE TIPOS
    window.addEventListener('message', (event) => {
        if (event.data === 'TIPOS_ATUALIZADOS') {
            carregarTipos();
        }
    });
});

function configurarEventos() {
    document.getElementById('bebida-form').addEventListener('submit', salvarBebida);
    document.getElementById('cancel-btn').addEventListener('click', limparFormulario);
}

function configurarModal() {
    const confirmModal = document.getElementById('confirm-modal');
    const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
    const cancelDeleteBtn = document.getElementById('cancel-delete-btn');

    confirmDeleteBtn.addEventListener('click', async () => {
        confirmModal.style.display = 'none';
        if (currentDeleteId) {
            await performDelete(currentDeleteId);
        }
    });

    cancelDeleteBtn.addEventListener('click', () => {
        confirmModal.style.display = 'none';
    });
}

function showDeleteModal(id) {
    currentDeleteId = id;
    document.getElementById('confirm-modal').style.display = 'flex';
}

async function performDelete(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        });

        if (!response.ok) throw new Error('Erro ao deletar');
        carregarBebidas();
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao excluir bebida');
    }
}

function validarBebida(bebida) {
    if (!bebida.nome || bebida.nome.trim() === '') {
        alert('O nome da bebida é obrigatório!');
        return false;
    }

    if (bebida.precoCusto <= 0 || isNaN(bebida.precoCusto)) {
        alert('O preço de custo deve ser maior que zero!');
        return false;
    }

    if (bebida.precoVenda <= 0 || isNaN(bebida.precoVenda)) {
        alert('O preço de venda deve ser maior que zero!');
        return false;
    }

    if (!bebida.tipo || bebida.tipo.trim() === '') {
        alert('Selecione um tipo válido!');
        return false;
    }

    return true;
}

async function carregarTipos() {
    try {
        const response = await fetch('http://localhost:8000/tipos');
        if (!response.ok) throw new Error('Erro ao carregar tipos');
        tipos = await response.json();
        preencherDropdownTipos();
    } catch (error) {
        console.error('Erro ao carregar tipos:', error);
    }
}

function preencherDropdownTipos() {
    const select = document.getElementById('tipo');
    select.innerHTML = '';
    
    const defaultOption = document.createElement('option');
    defaultOption.value = '';
    defaultOption.textContent = 'Selecione um tipo';
    select.appendChild(defaultOption);
    
    tipos.forEach(tipo => {
        const option = document.createElement('option');
        option.value = tipo.id;
        option.textContent = tipo.nome;
        select.appendChild(option);
    });
}

async function carregarBebidas() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error('Erro ao carregar bebidas');
        
        bebidas = await response.json();
        renderizarTabela(bebidas);
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao carregar bebidas');
    }
}

async function salvarBebida(event) {
    event.preventDefault();
    
    // Obter o nome do tipo selecionado
    const tipoSelect = document.getElementById('tipo');
    const tipoTexto = tipoSelect.options[tipoSelect.selectedIndex].text;
    
    const bebida = {
        id: document.getElementById('bebida-id').value || null,
        nome: document.getElementById('nome').value,
        tipo: tipoTexto,
        precoCusto: parseFloat(document.getElementById('preco-custo').value),
        precoVenda: parseFloat(document.getElementById('preco-venda').value),
    };

    if (!validarBebida(bebida)) {
        return;
    }

    const metodo = bebida.id ? 'PUT' : 'POST';
    const url = bebida.id ? `${API_URL}/${bebida.id}` : API_URL;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bebida)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Erro ao salvar');
        }

        limparFormulario();
        carregarBebidas();
    } catch (error) {
        console.error('Erro:', error);
        alert(error.message || 'Falha ao salvar bebida');
    }
}

function editarBebida(id) {
    const bebida = bebidas.find(b => b.id === id);
    if (!bebida) return;

    document.getElementById('form-title').textContent = 'Editar Bebida';
    document.getElementById('bebida-id').value = bebida.id;
    document.getElementById('nome').value = bebida.nome;
    
    // Selecionar o tipo correto no dropdown
    const tipoSelect = document.getElementById('tipo');
    for (let i = 0; i < tipoSelect.options.length; i++) {
        if (tipoSelect.options[i].text === bebida.tipo) {
            tipoSelect.selectedIndex = i;
            break;
        }
    }
    
    document.getElementById('preco-custo').value = bebida.precoCusto;
    document.getElementById('preco-venda').value = bebida.precoVenda;
}

async function deletarBebida(id) {
    showDeleteModal(id);
}

function renderizarTabela(bebidas) {
    const tbody = document.querySelector('#bebidas-table tbody');
    tbody.innerHTML = '';

    bebidas.forEach(bebida => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${bebida.id}</td>
            <td>${bebida.nome}</td>
            <td>${bebida.tipo}</td>
            <td>R$ ${bebida.precoCusto?.toFixed(2) || '0.00'}</td>
            <td>R$ ${bebida.precoVenda?.toFixed(2) || '0.00'}</td>
            <td>
                <button class="action-btn edit-btn" onclick="editarBebida(${bebida.id})">Editar</button>
                <button class="action-btn delete-btn" onclick="deletarBebida(${bebida.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function limparFormulario() {
    document.getElementById('form-title').textContent = 'Adicionar Nova Bebida';
    document.getElementById('bebida-form').reset();
    document.getElementById('bebida-id').value = '';
}

window.editarBebida = editarBebida;
window.deletarBebida = deletarBebida;


//teste