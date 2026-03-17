// Базовый URL API
console.log(' app.js загружен');

// ========== ПРОВЕРКА СТРАНИЦЫ ==========
const currentPage = window.location.pathname;
console.log(' Текущая страница:', currentPage);
const isCreateBatchPage = currentPage.includes('createBatch');

// ========== ЗАГРУЗКА ПРОДУКТОВ ==========
async function loadProductsForSelect() {
    console.log('Загрузка продуктов...');
    const mainSelect = document.getElementById('productSelect');
    if (!mainSelect) return;

    try {
        const response = await fetch('http://localhost:8081/api/products/getAllProducts');

        if (response.ok) {
            const products = await response.json();
            console.log('✅ Продукты загружены с сервера:', products);

            mainSelect.innerHTML = '<option value="">Выберите товар</option>';
            products.forEach(product => {
                const option = document.createElement('option');
                option.value = product.id;
                option.textContent = product.name;
                mainSelect.appendChild(option);
            });
        } else {
            console.error('❌ Ошибка загрузки продуктов:', response.status);
            useTestProducts(mainSelect);
        }
    } catch (error) {
        console.error('❌ Ошибка соединения:', error);
        useTestProducts(mainSelect);
    }
}

// ========== ЗАГРУЗКА ПОСТАВЩИКОВ ==========
async function loadSuppliersForSelect() {
    console.log('Загрузка поставщиков...');
    const select = document.getElementById('supplierSelect');
    if (!select) return;

    try {
        const response = await fetch('http://localhost:8081/api/suppliers/getAllSuppliers');

        if (response.ok) {
            const suppliers = await response.json();
            console.log('✅ Поставщики загружены с сервера:', suppliers);

            select.innerHTML = '<option value="">Выберите поставщика</option>';
            suppliers.forEach(supplier => {
                const option = document.createElement('option');
                option.value = supplier.id;
                option.textContent = supplier.name;
                select.appendChild(option);
            });
        } else {
            console.error('❌ Ошибка загрузки поставщиков:', response.status);
            useTestSuppliers(select);
        }
    } catch (error) {
        console.error('❌ Ошибка соединения:', error);
        useTestSuppliers(select);
    }
}

// ========== ЗАГРУЗКА СКЛАДОВ ==========
async function loadWarehousesForSelect() {
    console.log('Загрузка складов...');
    const select = document.getElementById('warehouseSelect');
    if (!select) return;

    try {
        const response = await fetch('http://localhost:8081/api/warehouses/getAllWarehouses');

        if (response.ok) {
            const warehouses = await response.json();
            console.log('✅ Склады загружены с сервера:', warehouses);

            select.innerHTML = '<option value="">Выберите склад</option>';
            warehouses.forEach(warehouse => {
                const option = document.createElement('option');
                option.value = warehouse.id;
                option.textContent = warehouse.name;
                select.appendChild(option);
            });
        } else {
            console.error('❌ Ошибка загрузки складов:', response.status);
            useTestWarehouses(select);
        }
    } catch (error) {
        console.error('❌ Ошибка соединения:', error);
        useTestWarehouses(select);
    }
}

// ========== ТЕСТОВЫЕ ДАННЫЕ ==========
function useTestSuppliers(select) {
    console.log('⚠️ Используем тестовые данные поставщиков');
    const testSuppliers = [
        { id: 1, name: 'ООО "Поставщик 1"' },
        { id: 2, name: 'ЗАО "Поставщик 2"' },
        { id: 3, name: 'ИП Иванов' }
    ];

    select.innerHTML = '<option value="">Выберите поставщика</option>';
    testSuppliers.forEach(supplier => {
        const option = document.createElement('option');
        option.value = supplier.id;
        option.textContent = supplier.name;
        select.appendChild(option);
    });
}

function useTestWarehouses(select) {
    console.log('⚠️ Используем тестовые данные складов');
    const testWarehouses = [
        { id: 1, name: 'Основной склад' },
        { id: 2, name: 'Резервный склад' },
        { id: 3, name: 'Склад готовой продукции' }
    ];

    select.innerHTML = '<option value="">Выберите склад</option>';
    testWarehouses.forEach(warehouse => {
        const option = document.createElement('option');
        option.value = warehouse.id;
        option.textContent = warehouse.name;
        select.appendChild(option);
    });
}

function useTestProducts(select) {
    console.log('⚠️ Используем тестовые данные продуктов');
    const testProducts = [
        { id: 1, name: 'Тестовый товар 1' },
        { id: 2, name: 'Тестовый товар 2' },
        { id: 3, name: 'Тестовый товар 3' }
    ];

    select.innerHTML = '<option value="">Выберите товар</option>';
    testProducts.forEach(product => {
        const option = document.createElement('option');
        option.value = product.id;
        option.textContent = product.name;
        select.appendChild(option);
    });
}

// ========== ФУНКЦИИ ДЛЯ createBatch.html ==========
function getProductOptions() {
    let options = '<option value="">Выберите товар</option>';
    const mainSelect = document.getElementById('productSelect');
    if (mainSelect && mainSelect.options.length > 1) {
        for (let i = 1; i < mainSelect.options.length; i++) {
            const opt = mainSelect.options[i];
            options += `<option value="${opt.value}">${opt.text}</option>`;
        }
    } else {
        options += `
            <option value="1">Тестовый товар 1</option>
            <option value="2">Тестовый товар 2</option>
            <option value="3">Тестовый товар 3</option>
        `;
    }
    return options;
}

function addItem() {
    console.log('➕ Добавление товара');
    const container = document.getElementById('itemsList');
    if (!container) {
        console.error('❌ itemsList не найден');
        return;
    }

    const itemId = Date.now();
    const today = new Date().toISOString().split('T')[0];
    const nextYear = new Date();
    nextYear.setFullYear(nextYear.getFullYear() + 1);
    const nextYearStr = nextYear.toISOString().split('T')[0];

    const options = getProductOptions();

    const itemHtml = `
        <div class="item-row" id="item-${itemId}">
            <div>
                <select class="product-select" required>${options}</select>
            </div>
            <div><input type="number" class="quantity-input" min="1" value="1" required></div>
            <div><input type="date" class="expiry-input" value="${nextYearStr}" required></div>
            <div><input type="number" step="0.01" class="cost-input" min="0" value="100.00" required></div>
            <div><input type="date" class="production-input" value="${today}" required></div>
            <div><button type="button" class="remove-btn" onclick="removeItem(${itemId})">×</button></div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', itemHtml);
    console.log('✅ Товар добавлен');
}

function removeItem(itemId) {
    console.log('🗑️ Удаление товара', itemId);
    const item = document.getElementById(`item-${itemId}`);
    if (item) item.remove();
}

async function createBatch(event) {
    event.preventDefault();
    console.log('📦 Создание партии...');

    const batchNumber = document.getElementById('batchNumber')?.value;
    const supplierId = parseInt(document.getElementById('supplierSelect')?.value);
    const warehouseId = parseInt(document.getElementById('warehouseSelect')?.value);
    const deliveryDate = document.getElementById('deliveryDate')?.value;

    if (!batchNumber) { alert('Введите номер поставки'); return; }
    if (!supplierId) { alert('Выберите поставщика'); return; }
    if (!warehouseId) { alert('Выберите склад'); return; }
    if (!deliveryDate) { alert('Выберите дату поставки'); return; }

    const rows = document.querySelectorAll('.item-row');
    if (rows.length === 0) { alert('Добавьте товары'); return; }

    const items = [];
    for (let i = 0; i < rows.length; i++) {
        const row = rows[i];
        const productId = parseInt(row.querySelector('.product-select')?.value);
        const quantity = parseInt(row.querySelector('.quantity-input')?.value);
        const expiryDate = row.querySelector('.expiry-input')?.value;
        const costprice = parseFloat(row.querySelector('.cost-input')?.value);
        const productionDate = row.querySelector('.production-input')?.value;

        if (!productId) { alert(`Выберите товар в строке ${i+1}`); return; }
        if (!quantity || quantity < 1) { alert(`Количество в строке ${i+1} должно быть больше 0`); return; }
        if (!expiryDate) { alert(`Укажите срок годности в строке ${i+1}`); return; }
        if (!costprice || costprice <= 0) { alert(`Себестоимость в строке ${i+1} должна быть больше 0`); return; }
        if (!productionDate) { alert(`Укажите дату производства в строке ${i+1}`); return; }

        items.push({
            productId,
            batchNumber,
            productionDate,
            expirydate: expiryDate,
            costprice,
            supplier: supplierId,
            warehouseId,
            quantity
        });
    }

    console.log('✅ Данные готовы к отправке:', items);
    alert(`✅ Данные готовы к отправке! Товаров: ${items.length}`);
}

// ========== НАВИГАЦИЯ ==========
function loadNavbar() {
    fetch('/components/navbar.html')
        .then(r => r.text())
        .then(d => document.getElementById('navbar').innerHTML = d)
        .catch(e => console.error('Ошибка загрузки навигации:', e));
}

// ========== ИНИЦИАЛИЗАЦИЯ ДЛЯ createBatch.html ==========
function initCreateBatchForm() {
    console.log('🚀 Инициализация формы создания поставки...');

    const deliveryDate = document.getElementById('deliveryDate');
    if (deliveryDate) {
        const today = new Date().toISOString().split('T')[0];
        deliveryDate.value = today;
    }

    setTimeout(() => {
        addItem();
        console.log('✅ Первый товар добавлен');
    }, 200);
}

// ========== ЗАПУСК В ЗАВИСИМОСТИ ОТ СТРАНИЦЫ ==========
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 DOM загружен');

    // Загружаем навигацию на всех страницах
    if (typeof loadNavbar === 'function') {
        loadNavbar();
    }

    // Инициализация только для страницы создания поставки
    if (isCreateBatchPage) {
        console.log('📄 На странице создания поставки');

        // Проверяем наличие элементов
        console.log('📋 Элементы формы:', {
            form: document.getElementById('batchForm'),
            addBtn: document.getElementById('addItemBtn'),
            itemsList: document.getElementById('itemsList')
        });

        // Загружаем данные для селектов
        Promise.all([
            loadSuppliersForSelect(),
            loadWarehousesForSelect(),
            loadProductsForSelect()
        ]).then(() => {
            console.log('✅ Все данные загружены');
            initCreateBatchForm();
        }).catch(error => {
            console.error('❌ Ошибка загрузки данных:', error);
        });

        // Назначаем обработчики
        const form = document.getElementById('batchForm');
        if (form) {
            form.addEventListener('submit', createBatch);
            console.log('✅ Обработчик формы назначен');
        }

        const addBtn = document.getElementById('addItemBtn');
        if (addBtn) {
            addBtn.addEventListener('click', addItem);
            console.log('✅ Обработчик кнопки добавления назначен');
        }
    } else {
        console.log('📄 На другой странице, пропускаем инициализацию формы создания');
    }
});