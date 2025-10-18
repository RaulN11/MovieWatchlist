document.getElementById('signupForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('signupUsername').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const password = document.getElementById('signupPassword').value;
    const confirmPassword = document.getElementById('signupConfirmPassword').value;

    // Validation
    if (!username || !email || !password || !confirmPassword) {
        alert('Please fill in all fields.');
        return;
    }

    if (password !== confirmPassword) {
        alert('Passwords do not match!');
        return;
    }

    if (password.length < 6) {
        alert('Password must be at least 6 characters long.');
        return;
    }

    const data = {
        username: username,
        email: email,
        password: password,
    };

    const submitBtn = event.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = 'Signing up...';

    fetch('/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => {
            if (response.ok) {
                return response.text().then(text => {
                    if (text.includes('Verification Email Resent')) {
                        alert('A verification email has been resent. Please check your inbox.');
                    } else {
                        alert('Registration successful! Please check your email to verify your account.');
                    }
                    window.location.href = '/login';
                });
            } else {
                return response.text().then(text => {
                    throw new Error(text || 'Registration failed');
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert(error.message || 'An error occurred during registration. Please try again.');
            submitBtn.disabled = false;
            submitBtn.textContent = 'Sign Up';
        });
});