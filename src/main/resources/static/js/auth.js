async function isAuthenticated() {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/me`, {
            method: 'GET',
            credentials: 'include'
        });
        return response.ok;
    } catch (error) {
        return false;
    }
}

async function getCurrentUser() {
    try {
        const response = await fetch(`${API_BASE_URL}/auth/me`, {
            method: 'GET',
            credentials: 'include'
        });

        if (response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Error getting current user:', error);
        return null;
    }
}

function redirectToLogin() {
    window.location.href = '/login.html';
}