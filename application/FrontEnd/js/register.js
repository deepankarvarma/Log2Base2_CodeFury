const togglePassword = document.getElementById("togglePassword");
      const passwordField = document.getElementById("password");
      const toggleConfirmPassword = document.getElementById(
        "toggleConfirmPassword"
      );
      const confirmPasswordField = document.getElementById("confirmPassword");

      togglePassword.addEventListener("click", function () {
        const type = passwordField.type === "password" ? "text" : "password";
        passwordField.type = type;
        this.querySelector("i").classList.toggle("fa-eye");
        this.querySelector("i").classList.toggle("fa-eye-slash");
      });

      toggleConfirmPassword.addEventListener("click", function () {
        const type =
          confirmPasswordField.type === "password" ? "text" : "password";
        confirmPasswordField.type = type;
        this.querySelector("i").classList.toggle("fa-eye");
        this.querySelector("i").classList.toggle("fa-eye-slash");
      });

      const hamburgerMenu = document.getElementById("hamburger-menu");
      const menuOverlay = document.getElementById("menu-overlay");
      const menuClose = document.getElementById("menu-close");

      hamburgerMenu.addEventListener("click", () => {
        menuOverlay.classList.add("open");
      });

      menuClose.addEventListener("click", () => {
        menuOverlay.classList.remove("open");
      });

      menuOverlay.addEventListener("click", (e) => {
        if (e.target === menuOverlay) {
          menuOverlay.classList.remove("open");
        }
      });
    </script>
    <script>
      document.addEventListener("DOMContentLoaded", () => {
        const themeToggle = document.getElementById("theme-toggle");
        const body = document.body;
        const themeIcon = document.getElementById("theme-icon");
        const themePath = document.getElementById("theme-path");

        // Load saved theme from local storage or default to light mode
        const currentTheme = localStorage.getItem("theme") || "light";
        if (currentTheme === "dark") {
          body.classList.add("dark-mode");
          themePath.setAttribute("d", "M12 3v9l4 4");
        }

        themeToggle.addEventListener("click", () => {
          if (body.classList.contains("dark-mode")) {
            body.classList.remove("dark-mode");
            localStorage.setItem("theme", "light");
            themePath.setAttribute("d", "M12 3v9l4 4");
          } else {
            body.classList.add("dark-mode");
            localStorage.setItem("theme", "dark");
            themePath.setAttribute("d", "M12 3v9l4 4");
          }
        });
      });
