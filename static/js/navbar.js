// /js/navbar.js
const BASE_URL = 'http://localhost:8081';

async function loadNavbar() {
    try {
        const response = await fetch(`${BASE_URL}/api/user/current`, {
            credentials: 'include'
        });

        if (!response.ok) {
            if (window.location.pathname !== '/login.html') {
                window.location.href = '/login.html';
            }
            return;
        }

        const user = await response.json();
        console.log('Current user:', user);

        const navbar = document.getElementById('navbar');
        if (!navbar) return;

        const roleMap = {
            'ADMIN': 'Администратор',
            'MANAGER': 'Менеджер',
            'STOREKEEPER': 'Кладовщик',
            'SENIOR_STOREKEEPER': 'Старший кладовщик'
        };

        // Определяем пункты меню в зависимости от роли
        const menuItems = [];

        // Главная (доступна всем)
        menuItems.push('<a href="/">Главная</a>');

        // Складские страницы (доступны всем кроме админа? админ тоже может)
        if (user.role !== 'ADMIN') {
            menuItems.push('<a href="/batches">Поставки</a>');
            menuItems.push('<a href="/inventory">Остатки</a>');
            menuItems.push('<a href="/movements">Перемещения</a>');
        } else {
            // Админ тоже может видеть складские страницы
            menuItems.push('<a href="/batches">Поставки</a>');
            menuItems.push('<a href="/inventory">Остатки</a>');
            menuItems.push('<a href="/movements">Перемещения</a>');
        }

        // Админские страницы
        if (user.role === 'ADMIN' || user.role === 'MANAGER') {
            menuItems.push('<a href="/admin/pannel">Админ панель</a>');
            menuItems.push('<a href="/warehouses">Склады</a>');
            menuItems.push('<a href="/suppliers">Поставщики</a>');
        }

        // Профиль (доступен всем)
        menuItems.push('<a href="/profile">Профиль</a>');

        // Кнопка выхода
        menuItems.push('<a href="#" id="logoutBtn">Выйти</a>');

        navbar.innerHTML = `
            <div class="nav-container">
                <a href="/" class="logo">ShopCRM</a>
                <div class="nav-links">
                    ${menuItems.join('')}
                </div>
            </div>
        `;

        // Добавляем обработчик выхода
        document.getElementById('logoutBtn').addEventListener('click', async (e) => {
            e.preventDefault();
            try {
                await fetch('/logout', { method: 'POST', credentials: 'include' });
                window.location.href = '/login.html';
            } catch (error) {
                window.location.href = '/login.html';
            }
        });

    } catch (error) {
        console.error('Ошибка загрузки навигации:', error);
        if (window.location.pathname !== '/login.html') {
            window.location.href = '/login.html';
        }
    }
}

// Функция для показа уведомлений
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, 3000);
}

// Функция для форматирования даты
function formatDate(dateString) {
    if (!dateString) return 'н/д';
    return new Date(dateString).toLocaleDateString('ru-RU');
}

// Функция для получения названия склада по ID
async function getWarehouseName(warehouseId) {
    if (!warehouseId) return 'Не указан';
    try {
        const response = await fetch(`${BASE_URL}/api/warehouses/findWarehouse?id=${warehouseId}`, {
            credentials: 'include'
        });
        if (response.ok) {
            const warehouse = await response.json();
            return warehouse.name || `Склад №${warehouseId}`;
        }
    } catch (error) {
        console.error('Ошибка загрузки склада:', error);
    }
    return `Склад №${warehouseId}`;
}

// Загружаем навигацию при загрузке страницы
document.addEventListener('DOMContentLoaded', loadNavbar);