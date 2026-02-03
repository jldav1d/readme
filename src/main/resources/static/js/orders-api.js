async function checkout() {
    try {
        const response = await fetch(`${API_BASE_URL}/orders/checkout`, {
            method: 'POST',
            credentials: 'include',
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || 'Checkout failed');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

async function getOrders() {
    try {
        const response = await fetch(`${API_BASE_URL}/orders`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch orders');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}

async function getOrderDetails(orderId) {
    try {
        const response = await fetch(`${API_BASE_URL}/orders/${orderId}`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!response.ok) {
            throw new Error('Failed to fetch order details');
        }

        return await response.json();
    } catch (error) {
        throw error;
    }
}