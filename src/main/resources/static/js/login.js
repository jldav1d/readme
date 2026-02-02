const LOGIN_API_URL = 'http://localhost:8080/api/v1/auth/login';
const loginForm = document.querySelector('#login-form');
const errorText = document.querySelector('#error-text');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    await sendLoginCredentials();
})

async function sendLoginCredentials() {


    // reset error state on new attempt
    errorText.classList.add('hidden');
    errorText.textContent = '';

    const formData = new FormData(loginForm);

    try {
        const response = await fetch(LOGIN_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: formData.get('username'),
                password: formData.get('password')
            }),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Invalid username or password');
        }


        console.log("Login successful!");

        // redirect user to index.html
        window.location.href = 'index.html';
    }
    catch (error) {
        errorText.textContent = error.message;
        errorText.classList.remove('hidden');
        console.error(error);
    }

}