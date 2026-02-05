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

async function checkAdmin(){
    const checkAdminText = document.getElementById('check-admin');
    const currentUser = await getCurrentUser();
    if (!currentUser) {
        return null;
    }

    if (currentUser.role === 'ADMIN') {
        checkAdminText.textContent = "You are a ADMIN";
    }
}

async function updateAuthButton() {
    const authLink = document.getElementById('auth-link');

    const authenticated = await isAuthenticated();

    if (authenticated) {
        const user = await getCurrentUser();

        authLink.textContent = 'Logout';
        authLink.href = '#';


        authLink.onclick = handleLogout;

    } else {
        authLink.textContent = 'Login';
        authLink.href = '/login.html';
        authLink.onclick = null;
    }
}

async function handleLogout(event) {
    event.preventDefault();

    if (!confirm('Are you sure you want to logout?')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/auth/logout`, {
            method: 'POST',
            credentials: 'include'
        });

        if (response.ok) {
            showNotification('Logged out successfully', 'success');

            setTimeout(() => {
                window.location.href = '/index.html';
            }, 1000);
        } else {
            throw new Error('Logout failed');
        }

    } catch (error) {
        console.error('Logout error:', error);
        showNotification('Logout failed. Please try again.', 'error');
    }
}

function redirectToLogin() {
    window.location.href = '/login.html';
}