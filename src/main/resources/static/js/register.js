const LOGIN_API_URL = 'http://localhost:8080/api/v1/auth/register';
const registerForm = document.querySelector('#register-form');
const errorText = document.querySelector('#error-text');

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    await registerUser();
})

async function registerUser(){

    errorText.classList.add('hidden');
    errorText.textContent = '';

    const formData = new FormData(registerForm);

    try{
        const response = await fetch(LOGIN_API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: formData.get('username'),
                password: formData.get('password')
            })
        })

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Invalid username or password');
        }


        console.log("Registered successfully!");

        // user flow:
        // register -> login (to get session) -> books/orders/carts

        // redirect user to login.html
        window.location.href = 'login.html';
    }
    catch(error){
        errorText.classList.remove('hidden');
        errorText.textContent = error.message;
        console.log(error);
    }
}