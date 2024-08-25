document.addEventListener("DOMContentLoaded", () => {
    // Check if the user is authenticated
    const isAuthenticated = localStorage.getItem("isAuthenticated");

    // If the user is not authenticated, redirect to the login page
    if (!isAuthenticated) {
      window.location.href = "login.html";
    }
  });
  document.addEventListener('DOMContentLoaded', () => {
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;
    const themeIcon = document.getElementById('theme-icon');
    const themePath = document.getElementById('theme-path');

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


  // Data for Orders Overview Chart
  const ordersCtx = document.getElementById("ordersChart").getContext("2d");
  const ordersChart = new Chart(ordersCtx, {
    type: "line",
    data: {
      labels: [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday",
      ],
      datasets: [
        {
          label: "Orders Delivered",
          data: [30, 25, 40, 35, 50, 45, 60],
          backgroundColor: "rgba(56, 178, 172, 0.2)",
          borderColor: "#38b2ac",
          borderWidth: 2,
        },
        {
          label: "Pending Orders",
          data: [5, 8, 7, 6, 4, 6, 3],
          backgroundColor: "rgba(255, 99, 132, 0.2)",
          borderColor: "#ff6384",
          borderWidth: 2,
        },
      ],
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });

  // Data for Subscriptions Overview Chart
  const subscriptionsCtx = document
    .getElementById("subscriptionsChart")
    .getContext("2d");
  const subscriptionsChart = new Chart(subscriptionsCtx, {
    type: "bar",
    data: {
      labels: [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
      ],
      datasets: [
        {
          label: "Active Subscriptions",
          data: [300, 320, 340, 310, 350, 370, 360],
          backgroundColor: "#319795",
          borderColor: "#319795",
          borderWidth: 1,
        },
      ],
    },
    options: {
      responsive: true,
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    },
  });

  // Logout and Theme Toggle Scripts (unchanged)
  document.getElementById("logout-button").addEventListener("click", () => {
    localStorage.removeItem("isAuthenticated");
    window.location.href = "login.html";
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

