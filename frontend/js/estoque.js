const API_BEBIDAS_URL = 'http://localhost:8000/bebidas';
const API_MOVIMENTACOES_URL = 'http://localhost:8000/movimentacoes';
const API_SALDO_URL = 'http://localhost:8000/estoque/saldo/';

let bebidas = [];
let movimentacaoPendente = null;

document.addEventListener('DOMContentLoaded', () => {
    carregarBebidas();
    configurarEventos();
    configurarModalEstoque();
    
    // Definir data atual como padrão no filtro
    const hoje = new Date().toISOString().split('T')[0];
    document.getElementById('filtro-data').value = hoje;
});

function configurarEventos() {
    document.getElementById('movimentacao-form').addEventListener('submit', prepararMovimentacao);
    document.getElementById('bebida').addEventListener('change', carregarSaldoBebida);
    document.getElementById('btn-carregar').addEventListener('click', carregarHistorico);
    
    // Configurar botão OK do modal de sucesso
    document.getElementById('ok-btn').addEventListener('click', () => {
        document.getElementById('success-modal').style.display = 'none';
    });
}

function configurarModalEstoque() {
    const modal = document.getElementById('confirm-modal-estoque');
    const confirmBtn = document.getElementById('confirm-mov-btn');
    const cancelBtn = document.getElementById('cancel-mov-btn');

    confirmBtn.addEventListener('click', async () => {
        modal.style.display = 'none';
        if (movimentacaoPendente) {
            await registrarMovimentacao(movimentacaoPendente);
        }
    });

    cancelBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        movimentacaoPendente = null;
    });
    
    // Fechar modais ao clicar fora
    window.addEventListener('click', (event) => {
        if (event.target === modal) {
            modal.style.display = 'none';
            movimentacaoPendente = null;
        }
        
        const successModal = document.getElementById('success-modal');
        if (event.target === successModal) {
            successModal.style.display = 'none';
        }
    });
}

function showConfirmModal(message) {
    document.getElementById('confirm-message').textContent = message;
    document.getElementById('confirm-modal-estoque').style.display = 'flex';
}

function showSuccessModal(message) {
    document.getElementById('success-message').textContent = message;
    document.getElementById('success-modal').style.display = 'flex';
}

async function carregarBebidas() {
    try {
        const response = await fetch(API_BEBIDAS_URL);
        if (!response.ok) throw new Error('Erro ao carregar bebidas');
        
        bebidas = await response.json();
        preencherDropdownBebidas();
        preencherFiltroBebidas();
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao carregar bebidas');
    }
}

function preencherDropdownBebidas() {
    const select = document.getElementById('bebida');
    select.innerHTML = '<option value="">Selecione uma bebida</option>';
    
    bebidas.forEach(bebida => {
        const option = document.createElement('option');
        option.value = bebida.id;
        option.textContent = bebida.nome;
        select.appendChild(option);
    });
}

function preencherFiltroBebidas() {
    const select = document.getElementById('filtro-bebida');
    select.innerHTML = '<option value="">Todas as bebidas</option>';
    
    bebidas.forEach(bebida => {
        const option = document.createElement('option');
        option.value = bebida.id;
        option.textContent = bebida.nome;
        select.appendChild(option);
    });
}

async function carregarSaldoBebida() {
    const bebidaId = document.getElementById('bebida').value;
    if (!bebidaId) return;

    try {
        const response = await fetch(API_SALDO_URL + bebidaId);
        if (!response.ok) throw new Error('Erro ao carregar saldo');
        
        const saldoData = await response.json();
        const bebida = bebidas.find(b => b.id == bebidaId);
        
        const saldoHTML = `
            <div class="saldo-card">
                <h3>${bebida.nome}</h3>
                <p>Saldo Atual: <span class="saldo-valor">${saldoData.saldo}</span></p>
                <p>Tipo: ${bebida.tipo}</p>
            </div>
        `;
        
        document.getElementById('saldo-container').innerHTML = saldoHTML;
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao carregar saldo');
    }
}

function prepararMovimentacao(event) {
    event.preventDefault();
    
    const bebidaId = document.getElementById('bebida').value;
    const tipoMovimento = document.getElementById('tipo-movimento').value;
    const quantidade = document.getElementById('quantidade').value;
    const motivo = document.getElementById('motivo').value || null;
    
    if (!bebidaId || !quantidade) {
        alert('Selecione uma bebida e informe a quantidade!');
        return;
    }
    
    const bebida = bebidas.find(b => b.id == bebidaId);
    const tipoTexto = tipoMovimento === 'REPOSICAO' ? 'Reposição' : 
                     tipoMovimento === 'VENDA' ? 'Venda' : 'Ajuste';
    
    movimentacaoPendente = {
        bebidaId: parseInt(bebidaId),
        tipoMovimento: tipoMovimento,
        quantidade: parseInt(quantidade),
        motivo: motivo
    };
    
    const message = `Confirmar ${tipoTexto} de ${quantidade} unidade(s) de ${bebida.nome}?`;
    showConfirmModal(message);
}

async function registrarMovimentacao(movimentacao) {
    try {
        const response = await fetch(API_MOVIMENTACOES_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(movimentacao)
        });
        
        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Erro ao registrar movimentação');
        }
        
        // Substituímos o alert pelo modal de sucesso
        showSuccessModal('Movimentação registrada com sucesso!');
        
        document.getElementById('movimentacao-form').reset();
        document.getElementById('saldo-container').innerHTML = '';
        
        // Atualizar saldo
        const bebidaId = movimentacao.bebidaId;
        document.getElementById('bebida').value = bebidaId;
        await carregarSaldoBebida();
        
        // Atualizar histórico
        carregarHistorico();
        
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao registrar: ' + error.message);
    } finally {
        movimentacaoPendente = null;
    }
}

async function carregarHistorico() {
    const bebidaId = document.getElementById('filtro-bebida').value;
    const dataFiltro = document.getElementById('filtro-data').value;
    
    try {
        // Construir URL corretamente com o parâmetro bebida_id
        let url = `${API_MOVIMENTACOES_URL}/historico`;
        
        // Adicionar parâmetros se existirem
        const params = new URLSearchParams();
        if (bebidaId) params.append('bebida_id', bebidaId);
        
        if (params.toString()) {
            url += '?' + params.toString();
        }
        
        const response = await fetch(url);
        if (!response.ok) throw new Error('Erro ao carregar histórico');
        
        const historico = await response.json();
        renderizarHistorico(historico, dataFiltro);
    } catch (error) {
        console.error('Erro:', error);
        alert('Falha ao carregar histórico: ' + error.message);
    }
}

function renderizarHistorico(historico, dataFiltro) {
    const tbody = document.querySelector('#historico-table tbody');
    tbody.innerHTML = '';
    
    // Filtrar por data se necessário (CÓDIGO CORRIGIDO)
    if (dataFiltro) {
        // Cria datas de início e fim em UTC
        const inicioDia = new Date(dataFiltro);
        inicioDia.setUTCHours(0, 0, 0, 0); // Início do dia em UTC
        
        const fimDia = new Date(inicioDia);
        fimDia.setUTCDate(fimDia.getUTCDate() + 1); // Próximo dia em UTC
        
        historico = historico.filter(mov => {
            const movDate = new Date(mov.dataHora);
            return movDate >= inicioDia && movDate < fimDia;
        });
    }
    
    if (historico.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align: center;">Nenhuma movimentação encontrada</td></tr>';
        return;
    }
    
    historico.forEach(mov => {
        const bebida = bebidas.find(b => b.id == mov.bebidaId) || {};
        const tipoTexto = mov.tipoMovimento === 'REPOSICAO' ? 'Reposição' : 
                         mov.tipoMovimento === 'VENDA' ? 'Venda' : 'Ajuste';
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${formatarData(mov.dataHora)}</td>
            <td>${bebida.nome || 'Desconhecido'}</td>
            <td>${tipoTexto}</td>
            <td>${mov.quantidade}</td>
            <td>${mov.motivo || '-'}</td>
        `;
        tbody.appendChild(tr);
    });
}

function formatarData(dataString) {
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR') + ' ' + data.toLocaleTimeString('pt-BR');
}

// Para acesso global
window.carregarSaldoBebida = carregarSaldoBebida;
window.carregarHistorico = carregarHistorico;