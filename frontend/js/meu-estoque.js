// Variáveis globais
let stockData = [];
let filteredData = [];

// Carregar estoque ao iniciar a página
document.addEventListener('DOMContentLoaded', function() {
    loadStockData();
});

// Função para carregar dados do estoque
function loadStockData() {
    // Simulação de chamada AJAX - substitua pela chamada real ao seu backend
    fetch('/chefbalance/api/meu-estoque')
        .then(response => response.json())
        .then(data => {
            stockData = data;
            filteredData = [...stockData];
            updateStockTable();
            updateStats();
        })
        .catch(error => {
            console.error('Erro ao carregar estoque:', error);
            // Dados de exemplo para demonstração
            const sampleData = [
                { id: 1, nome: "Vinho Tinto Seco", tipo: "Vinho", quantidade: 45, precoCusto: 18.50, precoVenda: 29.90, minEstoque: 10 },
                { id: 2, nome: "Cerveja Artesanal", tipo: "Cerveja", quantidade: 120, precoCusto: 6.80, precoVenda: 12.90, minEstoque: 24 },
                { id: 3, nome: "Whisky Premium", tipo: "Destilado", quantidade: 15, precoCusto: 95.00, precoVenda: 159.90, minEstoque: 5 },
                { id: 4, nome: "Refrigerante Cola", tipo: "Refrigerante", quantidade: 3, precoCusto: 2.10, precoVenda: 5.90, minEstoque: 12 },
                { id: 5, nome: "Suco de Laranja", tipo: "Suco", quantidade: 22, precoCusto: 3.50, precoVenda: 8.50, minEstoque: 10 },
                { id: 6, nome: "Água Mineral", tipo: "Água", quantidade: 56, precoCusto: 0.90, precoVenda: 3.50, minEstoque: 20 }
            ];
            
            stockData = sampleData;
            filteredData = [...stockData];
            updateStockTable();
            updateStats();
        });
}

// Função para atualizar a tabela de estoque
function updateStockTable() {
    const tableBody = document.getElementById('stockTableBody');
    tableBody.innerHTML = '';
    
    if (filteredData.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="8" class="loading">Nenhuma bebida encontrada</td></tr>';
        return;
    }
    
    filteredData.forEach(item => {
        const status = getStockStatus(item.quantidade, item.minEstoque || 10);
        const statusClass = getStatusClass(status);
        
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.id}</td>
            <td>${item.nome}</td>
            <td>${item.tipo}</td>
            <td>${item.quantidade}</td>
            <td>R$ ${item.precoCusto.toFixed(2)}</td>
            <td>R$ ${item.precoVenda.toFixed(2)}</td>
            <td><span class="status ${statusClass}">${status}</span></td>
            <td class="actions">
                <button class="action-btn edit-btn" onclick="editItem(${item.id})"><i class="fas fa-edit"></i></button>
                <button class="action-btn delete-btn" onclick="deleteItem(${item.id})"><i class="fas fa-trash"></i></button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

// Função para determinar o status do estoque
function getStockStatus(quantity, minStock) {
    if (quantity <= 0) return "Sem Estoque";
    if (quantity < minStock) return "Estoque Baixo";
    return "Em Estoque";
}

// Função para obter a classe CSS do status
function getStatusClass(status) {
    switch (status) {
        case "Em Estoque": return "in-stock";
        case "Estoque Baixo": return "low-stock";
        case "Sem Estoque": return "out-of-stock";
        default: return "";
    }
}

// Função para atualizar as estatísticas
function updateStats() {
    const totalItems = filteredData.length;
    const lowStockItems = filteredData.filter(item => item.quantidade > 0 && item.quantidade < (item.minEstoque || 10)).length;
    const totalValue = filteredData.reduce((sum, item) => sum + (item.quantidade * item.precoCusto), 0);
    
    document.getElementById('totalItems').textContent = totalItems;
    document.getElementById('lowStockItems').textContent = lowStockItems;
    document.getElementById('totalValue').textContent = `R$ ${totalValue.toFixed(2)}`;
}

// Função para filtrar o estoque por busca
function filterStock() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    filteredData = stockData.filter(item => 
        item.nome.toLowerCase().includes(searchText) || 
        item.tipo.toLowerCase().includes(searchText) ||
        item.id.toString().includes(searchText)
    );
    updateStockTable();
    updateStats();
}

// Função para filtrar por tipo (todos ou estoque baixo)
function filterByType(type) {
    if (type === 'all') {
        filteredData = [...stockData];
    } else if (type === 'low') {
        filteredData = stockData.filter(item => item.quantidade < (item.minEstoque || 10) && item.quantidade > 0);
    }
    updateStockTable();
    updateStats();
}

// Funções de edição e exclusão (para implementar)
function editItem(id) {
    alert(`Editar bebida com ID: ${id}`);
    // Redirecionar para página de edição ou abrir modal
}

function deleteItem(id) {
    if (confirm('Tem certeza que deseja excluir esta bebida?')) {
        // Implementar exclusão via API
        alert(`Excluir bebida com ID: ${id}`);
    }
}