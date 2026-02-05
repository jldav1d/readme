const addNewBookForm = document.querySelector(".add-new-book-form");
const addNewUserForm = document.querySelector(".add-new-user-form");

addNewBookForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const form = new FormData(addNewBookForm);

    console.log(Object.fromEntries(form));

    try {
        const response = await fetch(API_BASE_URL + "/books/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(Object.fromEntries(form)),
        });

        console.log(await response.json());
    }
    catch (error) {
        console.error(error);
    }
})


addNewUserForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const form = new FormData(addNewUserForm);

    try {
        const response = await fetch(API_BASE_URL + "/users/add", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                "username": form.get("username"),
                "password": form.get("password"),
                "role": form.get("role"),
            }),
        });
        console.log(await response.json());
    }
    catch (error) {
        console.error(error);
    }
})