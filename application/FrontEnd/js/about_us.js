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
