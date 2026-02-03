async function addItemToCart(bookId, quantity = 1) {
    try {
        const response = await fetch(`${API_BASE_URL}/cart/add`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                bookId: bookId,
                quantity: quantity
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to add to cart');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

async function getCart() {
    try {
        const response = await fetch(`${API_BASE_URL}/cart`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch cart');
        }

        return await response.json();
    } catch (error) {
        console.log(error);
        throw error;
    }
}

async function updateCartItemQuantity(cartItemId, quantity) {
    try {
        const response = await fetch(`${API_BASE_URL}/cart/items/${cartItemId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                quantity: quantity
            })
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Failed to update cart item');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

async function removeCartItem(cartItemId) {
    try {
        const response = await fetch(`${API_BASE_URL}/cart/items/${cartItemId}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to remove cart item');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

async function clearCart() {
    try {
        const response = await fetch(`${API_BASE_URL}/cart/clear`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to clear cart');
        }

        return true;
    } catch (error) {
        throw error;
    }
}