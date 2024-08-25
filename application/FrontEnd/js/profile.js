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
document.getElementById('logout-button').addEventListener('click', () => {
    // Remove the authentication flag from local storage
    localStorage.removeItem('isAuthenticated');
    // Redirect to the login page
    window.location.href = 'login.html';
});
let sortOrder = 1; // 1 for ascending, -1 for descending

function sortTable(columnIndex) {
    const table = document.getElementById("orderTable");
    const rows = Array.from(table.rows);

    rows.sort((a, b) => {
        const cellA = a.cells[columnIndex].innerText;
        const cellB = b.cells[columnIndex].innerText;

        if (!isNaN(Date.parse(cellA))) {
            return (new Date(cellA) - new Date(cellB)) * sortOrder;
        } else if (!isNaN(cellA) && !isNaN(cellB)) {
            return (cellA - cellB) * sortOrder;
        } else {
            return cellA.localeCompare(cellB) * sortOrder;
        }
    });

    sortOrder *= -1; // Toggle sort order

    rows.forEach(row => table.appendChild(row));
}

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
