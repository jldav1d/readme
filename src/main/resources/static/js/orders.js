document.addEventListener('DOMContentLoaded', async () => {
    // check authentication
    const authenticated = await isAuthenticated();

    if (!authenticated) {
        alert('Please login to view your orders');
        redirectToLogin();
        return;
    }

    await loadOrders();
});

async function loadOrders() {
    const loading = document.getElementById('loading');
    const ordersContainer = document.getElementById('orders-container');
    const emptyState = document.getElementById('empty-state');

    try {
        const orders = await getOrders();

        loading.style.display = 'none';

        if (!orders || orders.length === 0) {
            emptyState.style.display = 'block';
            ordersContainer.style.display = 'none';
        } else {
            emptyState.style.display = 'none';
            ordersContainer.style.display = 'block';
            displayOrders(orders);
        }

    } catch (error) {
        console.error('Error loading orders:', error);
        loading.innerHTML = '<p class="text-muted">Error loading orders. Please try again later.</p>';
    }
}

function displayOrders(orders) {
    const container = document.getElementById('orders-container');
    container.innerHTML = orders.map(order => createOrderCard(order)).join('');

    document.querySelectorAll('.order-toggle-btn').forEach(btn => {
        btn.addEventListener('click', handleToggleOrderDetails);
    });
}

function createOrderCard(order) {
    const orderDate = new Date(order.dateOrdered).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    return `
        <div class="order-card" data-order-id="${order.id}">
            <div class="order-header">
                <div class="order-info">
                    <h3>Order #${order.id}</h3>
                    <p class="order-date">${orderDate}</p>
                </div>
                <div class="order-summary">
                    <p class="order-items">${order.totalItems} item${order.totalItems !== 1 ? 's' : ''}</p>
                    <p class="order-total">$${order.totalPrice.toFixed(2)}</p>
                </div>
                <button class="order-toggle-btn" data-order-id="${order.id}">
                    View Details ▼
                </button>
            </div>
            <div class="order-details" id="order-details-${order.id}" style="display: none;">
                <div class="order-details-loading">
                    <p>Loading order details...</p>
                </div>
            </div>
        </div>
    `;
}

async function handleToggleOrderDetails(event) {
    const button = event.target;
    const orderId = button.dataset.orderId;
    const detailsContainer = document.getElementById(`order-details-${orderId}`);

    // toggle visibility
    if (detailsContainer.style.display === 'none') {
        detailsContainer.style.display = 'block';
        button.textContent = 'Hide Details ▲';

        if (!detailsContainer.dataset.loaded) {
            await loadOrderDetails(orderId);
        }
    } else {
        detailsContainer.style.display = 'none';
        button.textContent = 'View Details ▼';
    }
}

async function loadOrderDetails(orderId) {
    const detailsContainer = document.getElementById(`order-details-${orderId}`);

    try {
        const orderDetails = await getOrderDetails(orderId);

        detailsContainer.innerHTML = `
            <div class="order-items-list">
                ${orderDetails.items.map(item => `
                    <div class="order-item">
                        <div class="order-item-info">
                            <h4>${escapeHtml(item.bookTitle)}</h4>
                            <p class="text-muted">by ${escapeHtml(item.bookAuthor)}</p>
                        </div>
                        <div class="order-item-details">
                            <span class="order-item-qty">Qty: ${item.quantity}</span>
                            <span class="order-item-price">$${item.purchasedPrice.toFixed(2)} each</span>
                            <span class="order-item-subtotal">$${item.subtotal.toFixed(2)}</span>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;

        detailsContainer.dataset.loaded = 'true';

    } catch (error) {
        console.error('Error loading order details:', error);
        detailsContainer.innerHTML = '<p class="text-muted">Error loading order details.</p>';
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}