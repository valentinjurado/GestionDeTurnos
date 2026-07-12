async function iniciarSesion() {
    const user = document.getElementById('username').value;
    const pass = document.getElementById('password').value;

    try {
        const response = await fetch('https://gestiondeturnos-dnme.onrender.com/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: user, password: pass })
        });

        if (response.ok) {
            const data = await response.json();
            // 1. Guardamos la llave
            localStorage.setItem('token', data.token); 
            // 2. Saltamos a la aplicación principal
            window.location.href = 'index.html'; 
        } else {
            console.log("Error de credenciales");
        }
    } catch (error) {
        console.error("Error conectando con el servidor:", error);
    }
}