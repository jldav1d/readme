let currentCart = null;

document.addEventListener('DOMContentLoaded', async () => {
    const authenticated = await isAuthenticated();
    console.log(authenticated);

    if (!authenticated) {
        alert('Please login to view your cart');
        redirectToLogin();
        return;
    }

    await loadCart();

    setupEventListeners();
});

async function loadCart() {
    const loading = document.getElementById('loading');
    const cartContent = document.getElementById('cart-content');
    const emptyCart = document.getElementById('empty-cart');

    try {
        currentCart = await getCart();

        loading.style.display = 'none';

        if (!currentCart.items || currentCart.items.length === 0) {
            emptyCart.style.display = 'block';
            cartContent.style.display = 'none';
        } else {
            emptyCart.style.display = 'none';
            cartContent.style.display = 'block';
            displayCartItems(currentCart);
            updateCartSummary(currentCart);
        }

    } catch (error) {
        console.error('Error loading cart:', error);
        loading.innerHTML = '<p class="text-muted">Error loading cart. Please try again later.</p>';
    }
}

function displayCartItems(cart) {
    const container = document.getElementById('cart-items-container');
    const itemCount = document.getElementById('item-count');

    itemCount.textContent = cart.totalItems || 0;

    container.innerHTML = cart.items.map(item => createCartItemCard(item)).join('');

    attachQuantityListeners();
    attachRemoveListeners();
}

function createCartItemCard(item) {
    return `
        <div class="cart-item" data-item-id="${item.id}">
            <div class="cart-item-image">
                <div class="book-placeholder-sm">
                    <span>📖</span>
                </div>
            </div>
            
            <div class="cart-item-details">
                <h3 class="cart-item-title">${escapeHtml(item.bookTitle)}</h3>
                <p class="cart-item-author">by ${escapeHtml(item.bookAuthor)}</p>
                <p class="cart-item-price">$${item.bookPrice.toFixed(2)} each</p>
            </div>
            
            <div class="cart-item-quantity">
                <label class="quantity-label">Quantity:</label>
                <div class="quantity-controls">
                    <button class="quantity-btn" data-action="decrease" data-item-id="${item.id}">
                        -
                    </button>
                    <input 
                        type="number" 
                        class="quantity-input" 
                        value="${item.quantity}" 
                        min="1" 
                        data-item-id="${item.id}"
                        readonly
                    />
                    <button class="quantity-btn" data-action="increase" data-item-id="${item.id}">
                        +
                    </button>
                </div>
            </div>
            
            <div class="cart-item-subtotal">
                <p class="subtotal-label">Subtotal:</p>
                <p class="subtotal-amount">$${item.subtotal.toFixed(2)}</p>
            </div>
            
            <div class="cart-item-remove">
                <button class="btn-remove" data-item-id="${item.id}" title="Remove item">
                    ✕
                </button>
            </div>
        </div>
    `;
}

function updateCartSummary(cart) {
    const subtotal = cart.totalPrice || 0;
    const tax = 0; // No tax for now
    const total = subtotal + tax;

    document.getElementById('subtotal').textContent = `$${subtotal.toFixed(2)}`;
    document.getElementById('tax').textContent = `$${tax.toFixed(2)}`;
    document.getElementById('total').textContent = `$${total.toFixed(2)}`;
}

function setupEventListeners() {
    const clearCartBtn = document.getElementById('clear-cart-btn');
    if (clearCartBtn) {
        clearCartBtn.addEventListener('click', handleClearCart);
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', handleCheckout);
    }
}

function attachQuantityListeners() {
    document.querySelectorAll('.quantity-btn').forEach(button => {
        button.addEventListener('click', handleQuantityChange);
    });
}

function attachRemoveListeners() {
    document.querySelectorAll('.btn-remove').forEach(button => {
        button.addEventListener('click', handleRemoveItem);
    });
}

async function handleQuantityChange(event) {
    const button = event.target;
    const action = button.dataset.action;
    const itemId = parseInt(button.dataset.itemId);

    const item = currentCart.items.find(i => i.id === itemId);
    if (!item) return;

    let newQuantity = item.quantity;

    if (action === 'increase') {
        newQuantity++;
    } else if (action === 'decrease') {
        newQuantity--;
        if (newQuantity < 1) {
            if (!confirm('Remove this item from cart?')) {
                return;
            }
            await handleRemoveItem(event);
            return;
        }
    }

    try {
        showLoadingState();
        await updateCartItemQuantity(itemId, newQuantity);
        await loadCart();
        showNotification('Cart updated successfully', 'success');
    } catch (error) {
        console.error('Error updating quantity:', error);
        showNotification(error.message || 'Failed to update quantity', 'error');
        await loadCart();
    }
}

async function handleRemoveItem(event) {
    const button = event.target;
    const itemId = parseInt(button.dataset.itemId);

    if (!confirm('Are you sure you want to remove this item?')) {
        return;
    }

    try {
        showLoadingState();
        await removeCartItem(itemId);
        await loadCart();
        showNotification('Item removed from cart', 'success');
    } catch (error) {
        console.error('Error removing item:', error);
        showNotification(error.message || 'Failed to remove item', 'error');
        await loadCart();
    }
}

async function handleClearCart() {
    if (!confirm('Are you sure you want to clear your entire cart?')) {
        return;
    }

    try {
        showLoadingState();
        await clearCart();
        await loadCart();
        showNotification('Cart cleared successfully', 'success');
    } catch (error) {
        console.error('Error clearing cart:', error);
        showNotification(error.message || 'Failed to clear cart', 'error');
        await loadCart();
    }
}

async function handleCheckout() {
    if (!currentCart || !currentCart.items || currentCart.items.length === 0) {
        showNotification('Your cart is empty', 'error');
        return;
    }

    if (!confirm(`Proceed to checkout?\n\nTotal: $${currentCart.totalPrice.toFixed(2)}\nItems: ${currentCart.totalItems}`)) {
        return;
    }

    const checkoutBtn = document.getElementById('checkout-btn');
    const originalText = checkoutBtn.textContent;

    try {
        checkoutBtn.disabled = true;
        checkoutBtn.textContent = 'Processing...';

        const order = await checkout();

        showNotification('Order placed successfully!', 'success');

        // redirect user to order details
        setTimeout(() => {
            window.location.href = `/orders.html`;
        }, 1500);

    } catch (error) {
        console.error('Checkout error:', error);
        showNotification(error.message || 'Checkout failed. Please try again.', 'error');

        checkoutBtn.disabled = false;
        checkoutBtn.textContent = originalText;
    }
}

function showLoadingState() {
    const loading = document.getElementById('loading');
    const cartContent = document.getElementById('cart-content');
    const emptyCart = document.getElementById('empty-cart');

    loading.style.display = 'block';
    cartContent.style.display = 'none';
    emptyCart.style.display = 'none';
}

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;

    document.body.appendChild(notification);

    setTimeout(() => notification.classList.add('show'), 10);

    setTimeout(() => {
        notification.classList.remove('show');
        setTimeout(() => notification.remove(), 300);
    }, 3000);
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}