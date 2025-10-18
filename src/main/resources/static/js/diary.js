const searchButton = document.querySelector(".search-button");

searchButton.addEventListener("click", (e) => {
    e.preventDefault();
    let query = document.querySelector(".top-nav").value.trim();
    console.log(query);
    if (query) {
        window.location.href = `/searchMenu/movies/${encodeURIComponent(query)}`;
    }
});
const setActiveButton = () => {
    const currentPath = window.location.pathname;
    const filterBtns = document.querySelectorAll('.filter-btn');

    // Extract filter from URL (e.g., /watched/liked -> 'liked')
    const pathParts = currentPath.split('/');
    const currentFilter = pathParts[pathParts.length - 1] || 'all';

    filterBtns.forEach(btn => {
        const filterText = btn.textContent.trim().toLowerCase();
        let filterParam;

        switch(filterText) {
            case "all":
                filterParam = 'all';
                break;
            case 'liked':
                filterParam = 'liked';
                break;
            case 'highest rating':
                filterParam = 'highest';
                break;
            case 'lowest rating':
                filterParam = 'lowest';
                break;
            default:
                filterParam = 'all';
        }

        // Set active class if this button matches the current filter
        if (filterParam === currentFilter) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });
};

// Set active button on page load
setActiveButton();

// Handle filter button clicks
const filterBtns = document.querySelectorAll('.filter-btn');
filterBtns.forEach(btn => {
    btn.addEventListener('click', function() {
        const filterText = this.textContent.trim().toLowerCase();
        let filterParam;

        switch(filterText) {
            case "all":
                filterParam = 'all';
                break;
            case 'liked':
                filterParam = 'liked';
                break;
            case 'highest rating':
                filterParam = 'highest';
                break;
            case 'lowest rating':
                filterParam = 'lowest';
                break;
            default:
                filterParam = 'all';
        }

        window.location.href = `/watched/${filterParam}`;
    });
});