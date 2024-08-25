document.addEventListener('DOMContentLoaded', function () {
    const hamburgerMenu = document.getElementById('hamburger-menu');
    const menuOverlay = document.getElementById('menu-overlay');
    const menuClose = document.getElementById('menu-close');
    const themeToggle = document.getElementById('theme-toggle');
    const themeIcon = document.getElementById('theme-icon');

    hamburgerMenu.addEventListener('click', function () {
        menuOverlay.classList.toggle('open');
    });

    menuOverlay.addEventListener('click', function (event) {
        if (event.target === menuOverlay || event.target === menuClose) {
            menuOverlay.classList.remove('open');
        }
    });

    themeToggle.addEventListener('click', function () {
        document.body.classList.toggle('dark-mode');
        themeIcon.classList.toggle('rotate-180');
    });
});

// Function to sort the table rows by the first column (customer name)
function sortTable() {
    const table = document.getElementById("deliveryTable");
    const rows = Array.from(table.rows).slice(1); // exclude the header row

    rows.sort((row1, row2) => {
        const cell1 = row1.cells[0].innerText.toLowerCase();
        const cell2 = row2.cells[0].innerText.toLowerCase();
        return cell1.localeCompare(cell2);
    });

    rows.forEach(row => table.appendChild(row)); // append sorted rows back to the table
}
