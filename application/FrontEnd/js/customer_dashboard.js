document.addEventListener("DOMContentLoaded", () => {
    // Check if the user is authenticated
    const isAuthenticated = localStorage.getItem("isAuthenticated");

    // If the user is not authenticated, redirect to the login page
    if (!isAuthenticated) {
      window.location.href = "login.html";
    }
  });
      const themeToggle = document.getElementById("theme-toggle");
      const themeIcon = document.getElementById("theme-icon");
      const body = document.body;

      themeToggle.addEventListener("click", () => {
        body.classList.toggle("dark-mode");
      });

      const logoutButton = document.getElementById("logout-button");
      logoutButton.addEventListener("click", () => {
        // Clear authentication status
        localStorage.removeItem("isAuthenticated");
        // Redirect to login page
        window.location.href = "login.html";
      });
 
      // Order History Chart
      const ctxOrderHistory = document
        .getElementById("orderHistoryChart")
        .getContext("2d");
      const orderHistoryChart = new Chart(ctxOrderHistory, {
        type: "line",
        data: {
          labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug"],
          datasets: [
            {
              label: "Orders",
              data: [5, 7, 3, 10, 6, 8, 5, 9],
              borderColor: "#319795",
              backgroundColor: "rgba(49, 151, 149, 0.2)",
              fill: true,
              tension: 0.4,
            },
          ],
        },
        options: {
          scales: {
            y: {
              beginAtZero: true,
            },
          },
        },
      });

      // Subscription Status Chart
      const ctxSubscriptionStatus = document
        .getElementById("subscriptionStatusChart")
        .getContext("2d");
      const subscriptionStatusChart = new Chart(ctxSubscriptionStatus, {
        type: "doughnut",
        data: {
          labels: ["Active", "Inactive"],
          datasets: [
            {
              label: "Subscriptions",
              data: [3, 1],
              backgroundColor: ["#319795", "#cbd5e0"],
            },
          ],
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: "top",
            },
          },
        },
      });
