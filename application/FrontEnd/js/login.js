const togglePassword = document.getElementById('togglePassword');
const passwordField = document.getElementById('password');

togglePassword.addEventListener('click', function () {
// Toggle the type attribute of the password field
const type = passwordField.type === 'password' ? 'text' : 'password';
passwordField.type = type;

// Toggle the eye icon
this.querySelector('i').classList.toggle('fa-eye');
this.querySelector('i').classList.toggle('fa-eye-slash');
});
const hamburgerMenu = document.getElementById('hamburger-menu');
const menuOverlay = document.getElementById('menu-overlay');
const menuClose = document.getElementById('menu-close');

hamburgerMenu.addEventListener('click', () => {
   menuOverlay.classList.add('open');
});

menuClose.addEventListener('click', () => {
   menuOverlay.classList.remove('open');
});

menuOverlay.addEventListener('click', (e) => {
   if (e.target === menuOverlay) {
       menuOverlay.classList.remove('open');
   }
});

function login() {
   event.preventDefault();

   const accessMap = new Map([
       ["abc@xyz.com", ["1111", "admin"]],
       ["def@xyz.com", ["2222", "customer"]],
       ["ghi@xyz.com", ["3333", "customer"]]
   ]);

   const email = document.getElementById("email").value.trim();
   const password = document.getElementById("password").value.trim();

   document.getElementById("error-message").innerText = "";

   // Validate email and password
   if (email === "") {
       document.getElementById("error-message").innerText = "Email is required.";
       return;
   } else if (password === "") {
       document.getElementById("error-message").innerText = "Password is required.";
       return;
   } else if (email.length < 3) {
       document.getElementById("error-message").innerText = "Email must be at least 3 characters long.";
       return;
   } else if (password.length < 3) {
       document.getElementById("error-message").innerText = "Password must be at least 3 characters long.";
       return;
   } else if (accessMap.get(email)[0] != password) {
       document.getElementById("error-message").innerText = "Incorrect email and password combination.";
       return;
   } else {
       if (accessMap.get(email)[1] === "admin") {
           localStorage.setItem('isAuthenticated', 'true');
           window.location.href = "admin_dashboard.html";
       } else {
           localStorage.setItem('isAuthenticated', 'true');
           window.location.href = "customer_dashboard.html";
       }
   }
}

document.addEventListener('DOMContentLoaded', () => {
const themeToggle = document.getElementById('theme-toggle');
const body = document.body;
const themeIcon = document.getElementById('theme-icon');
const themePath = document.getElementById('theme-path');

// Load saved theme from local storage or default to light mode
const currentTheme = localStorage.getItem('theme') || 'light';
if (currentTheme === 'dark') {
   body.classList.add('dark-mode');
   themePath.setAttribute('d', 'M12 3v9l4 4');
}

themeToggle.addEventListener('click', () => {
   if (body.classList.contains('dark-mode')) {
       body.classList.remove('dark-mode');
       localStorage.setItem('theme', 'light');
       themePath.setAttribute('d', 'M12 3v9l4 4');
   } else {
       body.classList.add('dark-mode');
       localStorage.setItem('theme', 'dark');
       themePath.setAttribute('d', 'M12 3v9l4 4');
   }
});
});
