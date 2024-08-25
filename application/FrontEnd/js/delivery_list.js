    const hamburgerMenu = document.getElementById('hamburger-menu');
    const menuOverlay = document.getElementById('menu-overlay');
    const menuClose = document.getElementById('menu-close');
    document.getElementById('logout-button').addEventListener('click', () => {
    // Remove the authentication flag from local storage
    localStorage.removeItem('isAuthenticated');
    // Redirect to the login page
    window.location.href = 'login.html';
});
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
    function sortTable() {
        const table = document.getElementById('deliveryTable');
        const rows = Array.from(table.querySelectorAll('tbody tr'));
        const header = table.querySelector('thead th');
        const currentSort = header.getAttribute('data-sort') || 'asc';

        const newSort = currentSort === 'asc' ? 'desc' : 'asc';
        header.setAttribute('data-sort', newSort);

        rows.sort((a, b) => {
            const aText = a.cells[0].textContent.trim();
            const bText = b.cells[0].textContent.trim();

            if (aText < bText) return newSort === 'asc' ? -1 : 1;
            if (aText > bText) return newSort === 'asc' ? 1 : -1;
            return 0;
        });

        rows.forEach(row => table.querySelector('tbody').appendChild(row));
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
